package com.openeuler.base.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import util.JwtUtil;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PROXY_KEY;

@Component
public class JWTAuthFilter extends ZuulFilter {
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 过滤器类型，前置过滤器
     * @return
     */
    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    /**
     * 过滤器顺序，越小越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 10;
    }

    /**
     * 过滤器是否生效
     * @return
     */
    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        if ("/user/login".equalsIgnoreCase(request.getRequestURI())){
            return false;
        } else if("/user/register".equalsIgnoreCase(request.getRequestURI())){
            return false;
        } else if("web".equals(requestContext.get(PROXY_KEY))){
            return false;
        } else if("/user/repouser/auth".equalsIgnoreCase(request.getRequestURI())){
            return false;
        }

        return true;
    }

    /**
     * 业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        System.out.println("经过拦截器");
        //只负责把头请求中包含token的令牌进行一个解析验证
        String header = request.getHeader("Authorization");//获取头信息
        System.out.println("header = " + header);

        try {
            if (header != null && !"".equals(header) && header.startsWith("Bearer ")) {
                String token = header.substring(7);//获取到token
                Claims claims = jwtUtil.parseJWT(token);//解析token,如果解析失败就会抛出异常
                String roles = (String) claims.get("roles");//获取token内自定义的声明

                if (roles == null) {
                    throw new Exception();
                } else {
                    requestContext.addZuulRequestHeader("X-User-Id", claims.getId());
                    requestContext.addZuulRequestHeader("X-User-Role", roles);
                }
            } else {
                throw new Exception();
            }
        } catch (Exception e) {
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        return null;
    }
}
