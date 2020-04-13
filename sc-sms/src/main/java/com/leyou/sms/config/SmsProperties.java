package com.leyou.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@ConfigurationProperties(prefix ="ly.sms")
public class SmsProperties {
     String accessKeyId;
    String accessKeySecret;
    String signName;
   String verifycodeTemplate;

}
