package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import com.leyou.item.pojo.Category;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 查询分类信息
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {


}
