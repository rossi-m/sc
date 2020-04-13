package com.leyou.page.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;

/**
 * 查询商品信息
 */
@FeignClient("item-service")

public interface GoodsClient extends GoodsApi {

}
