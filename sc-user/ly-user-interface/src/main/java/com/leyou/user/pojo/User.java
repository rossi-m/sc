package com.leyou.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.javafx.beans.IDProperty;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name="tb_user")
@Data
public class User {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @NotNull
    private String username;    //用户名

    @Length(min = 4,max = 32,message = "用户名长度必须在4-32位")
    @JsonIgnore//作用：在实体类向前台返回数据时用来忽略不想传递给前台的属性或接口。
    private String password;
    @Pattern(regexp = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|166|198|199|(147))\\\\d{8}$",message = "手机号不正确")
    private String phone;

    private Date created;

    @JsonIgnore
    private String salt;//密码的盐值

}
