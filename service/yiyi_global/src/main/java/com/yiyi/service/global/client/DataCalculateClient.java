package com.yiyi.service.global.client;

import com.yiyi.commom.base.result.R;
import com.yiyi.service.global.client.fallback.DataCalculateFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author : Jack.F
 * @date : 2020/11/6
 */
@FeignClient(value = "dataCalculate", fallback = DataCalculateFallback.class)
public interface DataCalculateClient {

    @GetMapping("/dataCalculate/getCardType")
    @ApiOperation("获取卡片集 下拉框")
    R getCardType(@RequestParam("userId") String userId);

    @GetMapping("/dataCalculate/getLine")
    @ApiOperation("获取折线图和雷达图数据")
    R getLine(@RequestParam("userId") String userId,
              @RequestParam("typeId") String typeId);
}
