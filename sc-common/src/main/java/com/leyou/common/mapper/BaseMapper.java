package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;        //这个类的批量新增：不支持主键策略，插入前需要设置好主键的值
import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.special.InsertListMapper;       //这个类的批量新增：限制实体包含`id`属性并且必须为自增列

//IdListMapper: 批量查询，InsertListMapper: 批量添加
@RegisterMapper
public interface BaseMapper<T> extends Mapper<T>, IdListMapper<T,Long>, InsertListMapper<T> {
}
