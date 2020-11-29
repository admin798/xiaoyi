package com.yiyi.service.agorithm.controller;

import com.yiyi.commom.base.result.R;
import com.yiyi.service.agorithm.entity.Card;
import com.yiyi.service.agorithm.entity.CardType;
import com.yiyi.service.agorithm.service.AlgorithmService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author : Jack.F
 * @date : 2020/11/1
 */
@RestController
@RequestMapping("/algorithm")
@CrossOrigin
public class AlgorithmController {

    @Autowired
    private AlgorithmService algorithmService;


    @GetMapping("recommend")
    public R recommend() throws IOException {
        //对推荐系统进行算法推荐
        List<CardType> typeList = algorithmService.recommend();
        return R.ok().data("items", typeList);
    }

}
