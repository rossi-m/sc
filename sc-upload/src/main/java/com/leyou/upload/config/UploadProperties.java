package com.leyou.upload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
         //一定要注入容器
@ConfigurationProperties(prefix = "ly.upload")      //如果一个配置类只配置@ConfigurationProperties注解，而没有使用@Component，那么在IOC容器中是获取不到properties 配置文件转化的bean
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;

}
