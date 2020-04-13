package com.leyou.item.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Data
@Table(name = "tb_spu")
public class Spu {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private Long brandId;
    //商品分类的3个级别类目
    private Long cid1;
    private Long cid2;
    private Long cid3;
    private String title;
    private String subTitle;
    private Boolean saleable; //是否上架

    @JsonIgnore
    private Boolean valid; //是否有效，逻辑删除用
    private Date createTime;  //创建事件

    @JsonIgnore             //生成json 时不生成lastUpdateTime 属性
    private Date lastUpdateTime; //最后修改时间

    @Transient      //这个变量不存入数据库中
    private String cname;
    @Transient
    private String bname;

    @Transient
    private List<Sku> skus;
    @Transient
    private SpuDetail spuDetail;

}
