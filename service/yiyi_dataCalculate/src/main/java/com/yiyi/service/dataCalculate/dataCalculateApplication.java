package com.yiyi.service.dataCalculate;

import com.yiyi.commom.base.result.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;



/**
 * @author : Jack.F
 * @date : 2020/11/4
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.yiyi")
public class dataCalculateApplication {

    public static void main(String[] args) {
        SpringApplication.run(dataCalculateApplication.class,args);
    }

}
