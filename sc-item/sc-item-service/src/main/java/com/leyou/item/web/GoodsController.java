package com.leyou.item.web;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController  //会把java数据封装成json数据进行返回
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu
     */
    @RequestMapping("/spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,       //页数
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,       //每页大小
            @RequestParam(name = "saleable",required = false) Boolean saleable,     //是否上架，0下架，1上架
            @RequestParam(name = "key",required = false) String key     //搜索内容
    ){
            return ResponseEntity.ok(goodsService.querySpuByPage(page,rows,saleable,key));
    }

    /**
     * 商品新增
     * @param spu   json数据格式 ，RequestBody接受json数据参数
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){     //spu有sku集合和spudetail数据集合。传递的参数是json结构
        System.out.println("spu数据==============================："+spu);
        goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 商品修改
     * @param spu
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spu的id查询detail详情
     * @param spuId
     * @return
     */
    @RequestMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> queryDetailById(@PathVariable(name = "id") Long spuId){
            return ResponseEntity.ok(goodsService.queryDetailById(spuId));

    }
    /**
     * 根据spu的id查询sku的信息
     * @param spuId
     * @return
     */
    @RequestMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam(name = "id") Long spuId) {
        System.out.println("=============list====================");
        return ResponseEntity.ok(goodsService.querySkuBySpuId(spuId));
    }

    /**
     * 根据spuid查询spu信息
     * @param id
     * @return
     */
    @RequestMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable(name = "id") Long id){
    return ResponseEntity.ok(goodsService.querySpuById(id));
    }

    /**
     * 根据skuid查询sku信息
     * @return
     */
    @GetMapping("/sku/{skuId}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("skuId")Long skuId){
        return ResponseEntity.ok(goodsService.querySkuById(skuId));
    }

    /**
     *根据sku的id集合查询所有的sku
     * @param skuId
     * @return
     */
    @GetMapping("/sku/list/ids")
    public ResponseEntity<List<Sku>> querySkuBySkuId(@RequestParam("ids")List<Long> ids){
        return ResponseEntity.ok(goodsService.querySkuBySkuId(ids));
    }

    @PostMapping("/decrease/stock")
    public ResponseEntity<Void> decreaseStock(@RequestBody List<CartDTO> cartDTO){
        goodsService.decreaseStock(cartDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }




}
