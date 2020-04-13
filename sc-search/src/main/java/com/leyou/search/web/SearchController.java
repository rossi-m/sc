package com.leyou.search.web;

import com.leyou.common.vo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
     * 搜索功能，分页查询
     * @param searchRequest
     * @return
     */
    @RequestMapping("/page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest request){//把前端页面传递过来的json数据封装到SearchRequest对象中去
        return ResponseEntity.ok(searchService.search(request));
    }
}
