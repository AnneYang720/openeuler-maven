package com.openeuler.share.config;

import com.openeuler.share.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;



@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //注册拦截器：声明拦截器对象及拦截的请求（拦截器是谁，拦截器要干什么）
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**"); //拦截所有路径
    }
}