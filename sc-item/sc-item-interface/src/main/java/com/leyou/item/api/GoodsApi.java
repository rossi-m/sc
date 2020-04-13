package com.leyou.item.api;

import com.leyou.common.dto.CartDTO;
import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GoodsApi {
    @RequestMapping("/spu/detail/{id}")
    SpuDetail queryDetailById(@PathVariable(name = "id") Long spuId);
    @RequestMapping("/sku/list")
    List<Sku> querySkuBySpuId(@RequestParam(name = "id") Long spuId) ;
    @RequestMapping("/spu/page")
    PageResult<Spu> querySpuByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,       //页数
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,       //每页大小
            @RequestParam(name = "saleable",required = false) Boolean saleable,     //是否上架，0下架，1上架
            @RequestParam(name = "key",required = false) String key     //搜索内容
    );

    /**
     * 根据spuid查询
     * spu信息
     * @param id
     * @return
     */
    @RequestMapping("/spu/{id}")
    Spu querySpuById(@PathVariable(name = "id") Long id);

    @GetMapping("/sku/{skuId}")
    public Sku querySkuById(@PathVariable("skuId")Long skuId);

    /**
     * 根据skuid查询sku信息
     *
     * @param ids
     * @return
     */
    @GetMapping("/sku/list/ids")
    public List<Sku> querySkuBySkuId(@RequestParam("ids")List<Long> ids);

    /**
     * 减库存
     * @param cartDTO
     */
    @PostMapping("/decrease/stock")
    public void decreaseStock(@RequestBody List<CartDTO> cartDTO);
}
