package com.leyou.item.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *  用于前台交互的品牌实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandVo {

    /** 品牌id */
    private Long id;
    /** 品牌名称 */
    private String name;
    /** 品牌图片 */
    private String image;
    /** xxx */
    private List<Long> cids;
    /** 品牌首字母 */
    private Character letter;
}
