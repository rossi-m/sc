package com.leyou.page.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;

/**
 * 查询分类信息
 */
@FeignClient(value = "item-service")

public interface CategoryClient extends CategoryApi {


}
