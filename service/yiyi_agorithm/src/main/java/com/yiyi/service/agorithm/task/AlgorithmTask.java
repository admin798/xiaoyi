package com.yiyi.service.agorithm.task;

import com.yiyi.service.agorithm.service.AlgorithmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author : Jack.F
 * @date : 2020/11/6
 */
@Component
public class AlgorithmTask {

    @Autowired
    private AlgorithmService algorithmService;

    //0/5 * * * * ?
    @Scheduled(cron = "0 0 1 1/1 * ?")
    public void autoAddRedis() throws IOException {
        algorithmService.recommend();
    }
}
