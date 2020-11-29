package com.yiyi.service.global.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.CardFile;
import com.yiyi.service.global.entity.CardType;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.service.CardTypeService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@RestController
@RequestMapping("/global/card-type")
@CrossOrigin
@Slf4j
public class CardTypeController {

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardFileController cardFileController;

    @PostMapping("/createCardTypeAndCard")
    @ApiOperation("创建新的卡片集和卡片 " +
            "将会返回一个卡片集ID方便下一张卡片传递的时候传递卡片集ID")
    public R createCardTypeAndCard(
            @ApiParam(value = "卡片集ID 首次创建的时候不需要传递")
            @RequestParam(value = "cardTypeId",required = false) String cardTypeId,
            @ApiParam(value = "卡片集标题")
            @RequestParam(value = "title",required = false) String title,
            @ApiParam(value = "卡片集描述")
            @RequestParam(value = "introduce",required = false) String introduce,
            @ApiParam(value = "卡片问题 即正面内容",required = true)
            @RequestParam("question") String question,
            @ApiParam(value = "卡片答案 即反面",required = true )
            @RequestParam("answer") String answer
            ){

        String typeId = cardTypeService.createCardTypeAndCard(cardTypeId,title,introduce,question,answer);
        return R.ok().message("创建成功").data("item",typeId );
    }

    @GetMapping("/getCardType")
    @ApiOperation("用于新建卡片和文件夹的时候列出 卡片库 进行展示和选择")
    public R getCardType(){
        List<CardType> typeList =
                cardTypeService.getCardType();
        if (CollectionUtils.isEmpty(typeList)){
            return R.ok().message("您暂时还没有创建卡片库哦");
        }else {
            return R.ok().data("items",typeList );
        }
    }

    @ApiOperation("展示用户的卡片集")
    @GetMapping("listCardType/1")
    public R listCardType(){

        List<Map<String, Object>> listCardType = cardTypeService.listCardType();

        Integer count = this.cardTypeService.count(new QueryWrapper<CardType>().eq("user_id", loginFilter.getUser().getId()));


        if (listCardType == null){
            return R.ok().message("您还没有卡片集哦");
        }else {
            return R.ok().data("items",listCardType).data("count",count );
        }
    }

    @ApiOperation("展示用户的文件夹")
    @GetMapping("listCardType/2")
    public R backToListCardFile(){
        return cardFileController.listCardFile();
    }

    @ApiOperation("删除用户卡片集")
    @DeleteMapping("deleteCardType")
    public R deleteCardType(
            @ApiParam(value = "卡片集ID",required = true)
            @RequestParam("id") String id
    ){
        this.cardTypeService.deleteCardType(id);
        return R.ok().message("删除成功");
    }

    @ApiOperation("收藏和取消收藏文件夹 每点击一次切换一次收藏状态 返回收藏的状态 true为收藏")
    @GetMapping("collectCard")
    public R collectCard(
            @ApiParam(value = "文件夹ID",required = true)
            @RequestParam("id") String id
    ){
        CardType one = this.cardTypeService.getOne(
                new QueryWrapper<CardType>()
                        .eq("id", id)
        );
        if (one == null){
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }

        Boolean collect = one.getCollect();

        one.setCollect(!collect);

        this.cardTypeService.updateById(one);

        return R.ok().data("item",one.getCollect());

    }
}

