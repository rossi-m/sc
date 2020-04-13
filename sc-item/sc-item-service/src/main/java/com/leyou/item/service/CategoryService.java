package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByPid(Long pid) {
        //查询条件，mapper会把对象中的非空属性作为查询条件
        Category category=new Category();
        category.setParentId(pid);
        List<Category> list = categoryMapper.select(category);
        //判断结果
        if (CollectionUtils.isEmpty(list)){
            new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return list;
    }
    public List<Category> queryByIds(List<Long> ids){
        List<Category> categories = categoryMapper.selectByIdList(ids);     //根据一堆id查询
        if (CollectionUtils.isEmpty(categories)){
            new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return categories;
    }
}
