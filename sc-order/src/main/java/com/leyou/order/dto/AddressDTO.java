package com.leyou.order.dto;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
public class AddressDTO {

    private Long id;
    private String name;    //收件人姓名
    private String phone;   //电话
    private String state;   //省份
    private String city;    //城市
    private String district;    //区
    private String address;     //街道地址
    private String zipCode; //邮编
    private Boolean isDefault;   //是否默认


}
