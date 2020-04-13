package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Controller     //需要跳转到页面
public class PageController {
    @Autowired
    private PageService pageService;
    @RequestMapping("/item/{id}.html")
    public String toItemPage(@PathVariable("id") Long spuId, Model model){
        //查询模型数据
        Map<String,Object> attributes=pageService.loadModel(spuId);
        //准备模型数据
        model.addAllAttributes(attributes);
        //创建静态网页
        pageService.createHtml(spuId);
        //返回视图
        return "item";
    }
}
