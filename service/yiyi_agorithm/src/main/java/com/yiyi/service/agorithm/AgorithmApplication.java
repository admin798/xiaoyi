package com.yiyi.service.agorithm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * @author : Jack.F
 * @date : 2020/11/1
 */
@SpringBootApplication
@ComponentScan("com.yiyi")
@EnableDiscoveryClient
@EnableScheduling
public class AgorithmApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgorithmApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
