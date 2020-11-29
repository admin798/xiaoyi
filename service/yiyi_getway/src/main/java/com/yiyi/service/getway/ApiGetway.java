package com.yiyi.service.getway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author : Jack.F
 * @date : 2020/11/17
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class ApiGetway {
    public static void main(String[] args) {
        SpringApplication.run(ApiGetway.class,args);
    }
}
