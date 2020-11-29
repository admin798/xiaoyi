package com.yiyi.service.global;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: Jack.F
 * @Date: 2020/9/12 23:46
 */

@SpringBootApplication
@ComponentScan("com.yiyi")
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
public class GlobalApplication {
    public static void main(String[] args) {
        SpringApplication.run(GlobalApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
