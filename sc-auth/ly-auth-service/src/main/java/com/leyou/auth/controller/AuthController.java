package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.service.AuthService;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.CookieUtils;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties prop;

    @Value("${ly.jwt.cookieName}")
    private String cookieName;

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password, HttpServletRequest request,
                                      HttpServletResponse response) {
        // 登录
        String token = authService.login(username, password);
        // 写入cookie
        CookieUtils.setCookie(request, response, cookieName, token, prop.getExpire());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 校验用户登录状态
     * 校验是否有cookie值
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(@CookieValue(value = "LY_TOKEN", required = false) String token, HttpServletRequest request,
                                           HttpServletResponse response) {


        try {
            //使用公钥解析token ,根据token获取用户信息
            UserInfo userInfo=JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            //为空，说明未正常登录
            if (userInfo==null){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //刷新jwt过期时间,重新生成jwt
             token = JwtUtils.generateToken(userInfo, prop.getPrivateKey(), prop.getExpire());
            //刷新cookie的过期时间
            CookieUtils.setCookie(request,response,cookieName, token,prop.getExpire()*60, null);
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }



    }
}