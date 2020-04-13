package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.repository.GoodsRepository;
import com.netflix.discovery.converters.Auto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.ibatis.annotations.Param;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRepository repository;

    /**
     * 添加数据
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu){
        Long spuId = spu.getId();
        //查询分类
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //判断集合是否为空
        if (CollectionUtils.isEmpty(categories)){
            new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        //查询分类名称
        //根据分类查询分类名称，并组成集合
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null){
            new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        //搜索字段
        String all=spu.getTitle()+ StringUtils.join(names,"") +brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)){
            throw new LyException(ExceptionEnum.GOODS_SKU_NOT_FOUND);
        }
        //对sku进行处理
        List<Map<String,Object>> skus=new ArrayList<>();
        //获取价格集合
        Set<Long> priceList=new HashSet<>();
        for (Sku sku : skuList) {
            Map<String,Object> map=new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            map.put("image",StringUtils.substringBefore(sku.getImages(),","));//截取sku.getImages中第一个逗号之前的数据

            //价格添加
            priceList.add(sku.getPrice());
            //sku添加
            skus.add(map);
        }
        //查询规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)){
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        //查询商品详情
        SpuDetail spuDetail = goodsClient.queryDetailById(spuId);

        //获取通用规格参数,把json数据解析成map集合,json数据中不存在list集合只是：{1:"其他"}这样的数据
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);

        //获取特有规格参数,把json数据解析成map集合，json数据中存在list数据集合：{"4":["金","粉","灰","银"]}，中括号里面的就是list数据
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });

        //规格参数,key是规格参数的名字，值是规格参数的值
        Map<String,Object> specs=new HashMap<>();
        for (SpecParam param : params) {
            //规格名称
            String key = param.getName();
            Object value="";
            //判断是否是通用规格
            if (param.getGeneric()){
                value= genericSpec.get(param.getId());
                //判断是否数值类型
                if(param.getNumeric()){
                   value= chooseSegment(value.toString(),param);
                }

            }else{
                value=specialSpec.get(param.getId());
            }
            //存入map
            specs.put(key,value);

        }


        //构建goods对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all);//搜索字段，包含标题，分类，品牌，规格等
        goods.setPrice(priceList); // 所有sku的价格集合
        goods.setSkus(JsonUtils.toString(skus)); //所有sku的集合json格式
        goods.setSpecs(specs); // 所有可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }
    /**
     * 将规格参数为数值型的参数划分为段
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage()-1;   //当前页，第一页是从0开始
        int size = request.getSize();   //每页大小
        //创建查询构建起
        NativeSearchQueryBuilder queryBuilder=new NativeSearchQueryBuilder();
        //显示结果过滤
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page,size));
        //搜索条件过滤
        queryBuilder.withQuery(QueryBuilders.matchQuery("all",request.getKey()));

        //查询
        Page<Goods> result = repository.search(queryBuilder.build());

        //解析结果
        long total = result.getTotalElements(); //总结果
        int totalPages = result.getTotalPages();//总页数
        List<Goods> goodsList = result.getContent();//获取结果


        return new PageResult<>(total,totalPages,goodsList);
    }

    public void createOrUpdateIndex(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //构建goods对象
        Goods goods = buildGoods(spu);
        //存入索引库
        repository.save(goods);

    }

    public void deleteIndex(Long spuId) {
        repository.deleteById(spuId);
    }
}
