package com.leyou.gateway.filter;

import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.leyou.pojo.UserInfo;
import com.leyou.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * 过滤器
 */
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    public LoginFilter() {
        super();
    }

    //过滤器类型
    @Override
    public String filterType() {
        return "pre";//：在请求被路由（转发）之前调用
    }

    //过滤器顺序
    @Override
    public int filterOrder() {
        return 10;
    }
    //过滤器是否生效
    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        String url = request.getRequestURL().toString(); //返回请求的url，完整路径，包含域名

        for (String path:this.filterProperties.getAllowPaths() ) {
                if (url.contains(path)){
                    return false;       //包含过滤器不生效
                }
        }

        return true;        //不包含过滤器生效
    }
    //处理逻辑
    @Override
    public Object run() throws ZuulException {
        //初始化zuul上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request=context.getRequest();
        try {

            String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
            //根据token获取用户信息
             JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            context.setSendZuulResponse(false); //不转发请求
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());//设置响应状态码401
        }
        return null;
    }
}
