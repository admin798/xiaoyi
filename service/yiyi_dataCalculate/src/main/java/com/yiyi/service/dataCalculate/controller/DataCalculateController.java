package com.yiyi.service.dataCalculate.controller;

import com.yiyi.commom.base.result.R;
import com.yiyi.service.dataCalculate.entity.CardType;
import com.yiyi.service.dataCalculate.service.DataCalculateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author : Jack.F
 * @date : 2020/11/4
 */
@RestController
@RequestMapping("/dataCalculate")
@CrossOrigin
public class DataCalculateController {

    @Autowired
    private DataCalculateService dataCalculateService;

    @GetMapping("getLine")
    @ApiOperation("获取折线图和雷达图数据")
    public R getLine(
            @RequestParam("userId") String userId,
            @RequestParam("typeId") String typeId
    ){
        Map<String,Object> map = dataCalculateService.getLine(userId,typeId);
        return R.ok().data("item",map );
    }

    @GetMapping("getCardType")
    @ApiOperation("获取卡片集 下拉框")
    public R getCardType(
            @RequestParam("userId") String userId
    ){
        List<Map<String, Object>> list = dataCalculateService.getCardType(userId);
        return R.ok().data("items",list );
    }
}
