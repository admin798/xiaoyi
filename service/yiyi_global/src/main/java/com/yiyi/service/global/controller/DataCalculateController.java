package com.yiyi.service.global.controller;

import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.client.DataCalculateClient;
import com.yiyi.service.global.filter.LoginFilter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author : Jack.F
 * @date : 2020/11/6
 */
@RestController
@RequestMapping("/global/dataCalculate")
@CrossOrigin
@Api("算法接口")
public class DataCalculateController {

    @Autowired
    private DataCalculateClient dataCalculateClient;

    @Autowired
    private LoginFilter loginFilter;

    @GetMapping("/getCardType")
    @ApiOperation("获取卡片集下拉框")
    public R getCardType(){
        String userId = loginFilter.getUser().getId();
        return dataCalculateClient.getCardType(userId);
    }

    @GetMapping("/getLine")
    @ApiOperation("获取折线图和雷达图数据 y学习曲线 y1预测曲线 picture雷达图")
    public R getLine(@RequestParam("typeId") String typeId){
        String userId = loginFilter.getUser().getId();
        if (StringUtils.isBlank(typeId)){
            throw new YiyiException(ResultCodeEnum.PARAM_ERROR);
        }
        return dataCalculateClient.getLine(userId,typeId );
    }
}
