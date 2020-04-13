package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

public interface BrandApi {
    @RequestMapping("/brand/{id}")
    Brand queryBrandById(@PathVariable(name = "id") Long id);

}
