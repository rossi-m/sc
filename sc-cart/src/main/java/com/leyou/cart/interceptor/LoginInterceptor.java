package com.leyou.cart.interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import io.jsonwebtoken.Jwt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private JwtProperties jwtProperties;

    private static  final  ThreadLocal<UserInfo> THREAD_LOCAL=new ThreadLocal();
    //controller执行之前之后执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName(), null);
        //判断是否为空
        if (StringUtils.isBlank(token)){
            return false;
        }
        //解析token，获取用户信息
        UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());

        THREAD_LOCAL.set(userInfo);
        return true;
    }
    public static UserInfo get(){
        return THREAD_LOCAL.get();
    }

    /**
     * 前端页面执行完之后执行这个方法
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        //清除线程变量，必须操作。因为我们使用的是线程池，一次请求执行完成之后，线程并没有结束
        THREAD_LOCAL.remove();    //把线程放回线程池
    }
}
