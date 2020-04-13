package com.leyou.cart.config;

import com.leyou.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "ly.jwt")
public class JwtProperties {
    private PublicKey publicKey;        //公钥位置
    private String pubKeyPath;          //公钥
    private String cookieName;



    //对象一旦实例化后，就应该读取公钥和私钥,这个注解是在构造方法实例化执行
    @PostConstruct
    public void init(){


        //获取公钥
        try {
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化公钥失败");
        }

    }

}