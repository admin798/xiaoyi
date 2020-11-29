package com.yiyi.service.global.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyi.commom.base.result.R;
import com.yiyi.service.global.filter.LoginFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : Jack.F
 * @date : 2020/11/3
 */
@RestController
@RequestMapping("/global/recommend")
@CrossOrigin
@Api("算法接口")
public class CalculateController {

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("getCalculateCardType")
    @ApiOperation("获取推荐的卡片集")
    public R getCalculateCardType() throws JsonProcessingException {
        String prefix = "recommend";
        String userId = loginFilter.getUser().getId();
        String s = redisTemplate.opsForValue().get(prefix + userId);
        List<Map<String, Object>> list = objectMapper.readValue(s, new TypeReference<List<Map<String, Object>>>() {
        });
        list.forEach(l->{
            l.remove("card");
        });
        return R.ok().data("items",list );
    }

    @GetMapping("getCalculateCard/{id}")
    @ApiOperation("根据id获取推荐的卡片")
    public R getCalculateCard(
            @ApiParam(value = "id",required = true)
            @PathVariable("id") String id)
            throws JsonProcessingException {
        String prefix = "recommend";
        String userId = loginFilter.getUser().getId();
        String s = redisTemplate.opsForValue().get(prefix + userId);
        List<Map<String, Object>> list = objectMapper.readValue(s, new TypeReference<List<Map<String, Object>>>() {
        });
        for (Map<String, Object> map : list) {
            if (map.get("id").equals(id)){
                return R.ok().data("item",map );
            }
        }
        return R.error().message("传入的参数有误！");
    }
}
