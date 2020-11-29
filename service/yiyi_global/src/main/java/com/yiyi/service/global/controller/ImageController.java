package com.yiyi.service.global.controller;

import com.yiyi.commom.base.result.R;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.service.ImageService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController("global/image")
@CrossOrigin
public class ImageController {

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private ImageService imageService;

    @ApiOperation("获取下拉框 date=yyyy-MM-dd")
    @GetMapping("getStudyType")
    public R getStudyType(
            @ApiParam("查询的日期")
            @RequestParam("date") String date){
        String userId = loginFilter.getUser().getId();
        List<Map<String, Object>> list = imageService.getStudyType(date, userId);
        return R.ok().data("items",list);
    }


    @ApiOperation("获取专注度曲线信息")
    @GetMapping("getdetail/{id}")
    public R getdetail(@PathVariable("id") String id){
        String userId = loginFilter.getUser().getId();
        //imageService.getdetail(userId,id);
        return null;
    }
}
