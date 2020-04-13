package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.pojo.UserInfo;
import com.leyou.user.pojo.User;
import com.leyou.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties prop;

    //登录校验
    public String login(String username, String password) {
        try {
            //校验用户名和密码
            User user = userClient.queryUserByUsernameAndPassword(username, password);
            if (user == null) {
                throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
            }
            //生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), username),//生成token就把id和username赋值给了UserInfo类
                    prop.getPrivateKey(), prop.getExpire());
            return token;
        } catch (Exception e) {
            log.error("[授权中心] 用户名或者密码有误,用户名称:{}", username, e);
            throw new LyException(ExceptionEnum.INVALID_USERNAME_PASSWORD);
        }
    }

}
