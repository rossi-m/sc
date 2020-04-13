package com.leyou.item.web;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    /**
     * 分页查询品牌
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @param key
     * @return
     */
    @RequestMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(name = "page",defaultValue = "1") Integer page,       //页数
            @RequestParam(name = "rows",defaultValue = "5") Integer rows,       //每页大小
            @RequestParam(name = "sortBy",required = false) String sortBy,     //排序的字段
            @RequestParam(name = "desc",defaultValue = "false") Boolean desc,    //降序还是升序
            @RequestParam(name = "key",required = false) String key     //搜索内容
    ){

        PageResult<Brand> result=brandService.queryBrandByPage(page,rows,sortBy,desc,key);
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     * @return
     */
    @RequestMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam(name = "cids") List<Long> cids){
            brandService.saveBrand(brand,cids);
           return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据cid查询品牌
     * @param cid  分类的id，中间表查询
     * @return
     */
    @RequestMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable(name = "cid")Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    /**
     * 根据id查询品牌
     * @param id  品牌id
     * @return
     */
    @RequestMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(brandService.queryById(id));
    }



}
