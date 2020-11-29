package com.yiyi.service.global.config;

import com.yiyi.service.global.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Jack.F
 * @Date: 2020/9/16 10:08
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginFilter loginFilter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginFilter)
                .addPathPatterns("/global/card-file/**","/global/user/**","/global/card/**","/global/card-type/**","/global/recommend/**","/global/dataCalculate/**","global/image/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**")
                .excludePathPatterns("/global/user/login/**","/global/user/register/**","/global/user/getSmsCode/**")
        ;
    }
}
