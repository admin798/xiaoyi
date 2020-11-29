package com.yiyi.commom.base.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Jack.F
 * @date : 2020/9/9
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.yiyi.service.*.mapper")
public class MPConfig {
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }

}
