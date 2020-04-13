package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryApi{
    @RequestMapping("/category/list/ids")
    List<Category> queryCategoryByIds(@RequestParam(name = "ids") List<Long> ids);
}
