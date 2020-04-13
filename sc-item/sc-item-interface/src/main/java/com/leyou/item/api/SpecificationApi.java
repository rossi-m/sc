package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {

    /**
     * 查询参数集合
     * @param gid  组id
     * @param cid  分类id
     * @param searching  是否搜索
     * @return
     */
    @RequestMapping("/spec/params")
    List<SpecParam> queryParamList(
            @RequestParam(name = "gid",required = false) Long gid,
            @RequestParam(name = "cid",required = false) Long cid,
            @RequestParam(name = "searching",required = false) Boolean searching
    );

    @RequestMapping("/spec/group")
    List<SpecGroup> queryListByCid(@RequestParam("cid") Long cid);

}
