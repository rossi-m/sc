package com.leyou.item.web;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据父节点id查询商品分类
     *
     * @param pid
     * @return
     */
    @RequestMapping("/list")
    public ResponseEntity<List<Category>> queryCategoryListByPid(@RequestParam(name = "pid") Long pid) {

        return ResponseEntity.ok(categoryService.queryCategoryListByPid(pid));
    }

    /**
     * 根据分类的id查询分类信息
     * @param ids 分类id集合
     * @return
     */
    @RequestMapping("/list/ids")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam(name = "ids") List<Long> ids){
        return ResponseEntity.ok(categoryService.queryByIds(ids));
    }
}
