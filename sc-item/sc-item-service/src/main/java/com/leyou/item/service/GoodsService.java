package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper detailMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;


    @Transactional          //需要增加事务
    public PageResult<Spu> querySpuByPage(Integer page, Integer rows, Boolean saleable, String key) {
        //分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example=new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //搜索字段过滤
        //检查字符串是否为空
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title","%"+key+"%");
        }
        //上下架过滤
        if (saleable !=null){
            criteria.andEqualTo("saleable",saleable);
        }
        //默认排序
        example.setOrderByClause("last_update_time DESC");
        //查询
        List<Spu> spus = spuMapper.selectByExample(example);
        //判断
        if (CollectionUtils.isEmpty(spus)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //解析分类和品牌名称
        loadCateGoryAndBrandName(spus);

        //解析分页
        PageInfo pageInfo=new PageInfo(spus);
        return new PageResult<>(pageInfo.getTotal(),spus);

    }

    private void loadCateGoryAndBrandName(List<Spu> spus) {
        for (Spu spu : spus) {
            //处理分类名称
            List<String> names = categoryService.queryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3())).stream().map(Category::getName).collect(Collectors.toList());
            //返回连接的字符串
            spu.setCname(StringUtils.join(names,"/"));
            //处理品牌名称
            Long brandId = spu.getBrandId();
            String name = brandService.queryById(brandId).getName();
            spu.setBname(name);

        }

    }

    @Transactional
    public void saveGoods(Spu spu) {
        //新增spu
        spu.setId(null);
        spu.setCreateTime(new Date());
        spu.setLastUpdateTime(spu.getCreateTime());
        spu.setSaleable(true);
        spu.setValid(false);
        int count = spuMapper.insert(spu);
        if (count !=1){
            throw new LyException(ExceptionEnum.GOOODS_SAVE_ERROR);
        }
        //新增detail
        SpuDetail detail = spu.getSpuDetail();
        detail.setSpuId(spu.getId());
        detailMapper.insert(detail);

        //新增sku和库存
        saveSkuAndStock(spu);

        //发送mq消息
       amqpTemplate.convertAndSend("item.insert",spu.getId());



    }

    private void saveSkuAndStock(Spu spu) {
        int count;
        List<Stock> stockList=new ArrayList<>();

        //新增sku
        List<Sku> skus = spu.getSkus();
        for (Sku sku : skus) {
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            sku.setSpuId(spu.getId());

            count = skuMapper.insert(sku);
            if (count !=1){
                throw new LyException(ExceptionEnum.GOOODS_SAVE_ERROR);
            }

            //新增stock库存
            Stock stock=new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockList.add(stock);

        }

        //批量新增库存
        count = stockMapper.insertList(stockList);
        if (count !=1){
            throw new LyException(ExceptionEnum.GOOODS_SAVE_ERROR);
        }
    }

    public SpuDetail queryDetailById(Long spuId) {
        SpuDetail spuDetail = detailMapper.selectByPrimaryKey(spuId);
        if (spuDetail ==null){
            throw new LyException(ExceptionEnum.GOODS_DETAIL_NOT_FOUND);
        }
        return spuDetail;

    }


    @Transactional
    public List<Sku> querySkuBySpuId(Long spuId) {

        //查询sku
        Sku sku=new Sku();
        sku.setSpuId(spuId);
        List<Sku> skuList = skuMapper.select(sku);
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //查询库存
        for (Sku s : skuList) {
            Stock stock= stockMapper.selectByPrimaryKey(s.getId());
            if (stock ==null){
                throw new LyException(ExceptionEnum.GOODS_STOCK_NOT_FOUND);
            }
            s.setStock(stock.getStock());
        }
        return skuList;
    }


    public void updateGoods(Spu spu) {
        if (spu.getId() == null){
            throw new LyException(ExceptionEnum.GOODS_ID_CANNOT_BE_NULL);
        }

        Sku sku=new Sku();
        sku.setSpuId(spu.getId());
        //查询sku，修改sku，把之前的数据清空
        List<Sku> skuList = skuMapper.select(sku);
        if (!CollectionUtils.isEmpty(skuList)){
            //删除sku
            skuMapper.delete(sku);
            //删除stock
            List<Long> ids = skuList.stream().map(Sku::getId).collect(Collectors.toList());//得到sku的id并存入ids集合中
            stockMapper.deleteByIdList(ids);        //批量删除
        }
        //修改spu
        spu.setValid(null);
        spu.setSaleable(null);
        spu.setLastUpdateTime(new Date());
        spu.setCreateTime(null);
        int count = spuMapper.updateByPrimaryKeySelective(spu);//根据主键更新  属性  不为null的值
        System.out.println("spu更新之后==========================:"+spu);
        if (count !=1 ){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //修改detail
        count=detailMapper.updateByPrimaryKeySelective(spu.getSpuDetail());
        System.out.println("spuDetail更新之后的数据========================"+spu.getSpuDetail());
        if (count !=1 ){
            throw new LyException(ExceptionEnum.GOODS_UPDATE_ERROR);
        }
        //新增sku和stock
        saveSkuAndStock(spu);

        //发送mq消息
        amqpTemplate.convertAndSend("item.update",spu.getId());

    }

    public Spu querySpuById(Long id) {
        //查询spu
        Spu spu=new Spu();
        spu.setId(id);
        spu =  spuMapper.selectByPrimaryKey(id);
        if (spu ==null){
           throw  new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //查询sku
        List<Sku> skus = querySkuBySpuId(id);
        spu.setSkus(skus);

        //查询spuDetail
        spu.setSpuDetail(queryDetailById(id));
        return spu;
    }

    public Sku querySkuById(Long skuId) {
        Sku sku = skuMapper.selectByPrimaryKey(skuId);
        return sku;
    }

    public List<Sku> querySkuBySkuId(List<Long> ids) {
       List<Sku> skus=new ArrayList<>();
       for (Long id: ids){
           Sku sku = skuMapper.selectByPrimaryKey(id);
           skus.add(sku);
       }
       return skus;
    }

    public void decreaseStock(List<CartDTO> carts) {
        for (CartDTO cart: carts){
            //查询库存
            Long skuId = cart.getSkuId();
            //减库存
            int count=stockMapper.decreaseStock(skuId,cart.getNum());
            if(count !=1 ){
                throw new LyException(ExceptionEnum.STOCK_NOT_ENOUGH);
            }
        }

    }
}
