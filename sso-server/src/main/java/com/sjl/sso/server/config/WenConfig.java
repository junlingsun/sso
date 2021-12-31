package com.sjl.sso.server.config;

import com.sjl.sso.server.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WenConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;



    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor).excludePathPatterns("/doLogin");
    }
}
