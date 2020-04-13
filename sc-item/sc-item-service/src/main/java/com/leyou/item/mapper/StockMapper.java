package com.leyou.item.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.IdListMapper;
;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

//IdListMapper: 批量查询，InsertListMapper: 批量添加
public interface StockMapper extends Mapper<Stock>,IdListMapper<Stock,Long>,InsertListMapper<Stock>{

    @Update("Update tb_stock set stock=stock - #{num} where sku_id =#{id} and stock >= #{num}")
    int decreaseStock(@Param("id")Long id,@Param("num")Integer num);
}
