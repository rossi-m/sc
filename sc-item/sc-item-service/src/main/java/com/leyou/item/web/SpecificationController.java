package com.leyou.item.web;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询规格组
     * @param cid
     * @return
     */
    @RequestMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable(name = "cid") Long cid ){
        return ResponseEntity.ok(specificationService.queryGroupByCid(cid));
    }


    /**
     * 查询参数集合
     * @param gid  组id
     * @param cid  分类id
     * @param searching  是否搜索
     * @return
     */
    @RequestMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParamList(
            @RequestParam(name = "gid",required = false) Long gid,
            @RequestParam(name = "cid",required = false) Long cid,
            @RequestParam(name = "searching",required = false) Boolean searching
    ){
        return ResponseEntity.ok(specificationService.queryParamList(gid,cid,searching));
    }

    /**
     *根据分类查询规格组及组内参数
     * @param cid
     * @return
     */
    @RequestMapping("/group")
   public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specificationService.queryListByCid(cid));
    }



}
