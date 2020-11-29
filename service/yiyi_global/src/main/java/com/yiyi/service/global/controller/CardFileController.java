package com.yiyi.service.global.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.CardFile;
import com.yiyi.service.global.entity.CardType;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.CardFileMapper;
import com.yiyi.service.global.mapper.CardMapper;
import com.yiyi.service.global.mapper.CardTypeMapper;
import com.yiyi.service.global.service.CardFileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping("/global/card-file")
@CrossOrigin
public class CardFileController {

    @Autowired
    private CardFileService cardFileService;

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private CardFileMapper cardFileMapper;


    @ApiOperation("创建新的文件夹")
    @PostMapping("createFile")
    public R createFile(
            @ApiParam(value = "文件夹标题",required = true)
            @RequestParam("file") String title,
            @ApiParam(value = "文件夹介绍",required = true)
            @RequestParam(value = "introduce") String introduce,
            @ApiParam(value = "卡片库ID")
            @RequestParam(value = "cardTypeId",required = false) List<String> cardTypeId
    ){
        cardFileService.createFile(title,introduce,cardTypeId);
        return R.ok().message("创建成功");
    }

    @ApiOperation("展示用户的文件夹 注意 返回的参数中count参数为该用户的文件夹总数")
    @GetMapping("listCardFile")
    public R listCardFile(){
        List<Map<String, Object>> mapList = this.cardFileService.listMaps(
                new QueryWrapper<CardFile>()
                        .eq("user_id", loginFilter.getUser().getId())
                        .select("id", "title", "introduce","is_collect")
        );

        mapList.forEach(m->{
            QueryWrapper<CardType> typeQueryWrapper = new QueryWrapper<>();
            typeQueryWrapper.eq("file_id",m.get("id") );
            Integer count = cardTypeMapper.selectCount(typeQueryWrapper);
            m.put("totalCount",count );
            m.put("model","cardFile" );
        });


        if (CollectionUtils.isEmpty(mapList)){
            return R.ok().message("您的文件夹为空");
        }else {
            return R.ok().data("items", mapList).data("count",mapList.size() );
        }

    }

    @ApiOperation("删除文件夹")
    @DeleteMapping("deleteFile")
    public R deleteFile(
            @ApiParam(value = "文件夹ID",required = true)
            @RequestParam("id") String id
    ){
        this.cardFileService.deleteFile(id);
        return R.ok().message("删除文件夹成功");
    }

    @ApiOperation("展示用户文件夹中的卡片集")
    @GetMapping("getCardTypeByFileId")
    public R getCardTypeByFileId(
            @ApiParam(value = "文件的ID",required = true)
            @RequestParam("id") String id,
            @ApiParam(value = "查询的条件")
            @RequestParam(value = "condition",required = false) List<String> condition,
            @ApiParam("按照哪种顺序排序")
            @RequestParam(value = "sort",required = false,defaultValue = "asc") String sort
    ){
        List<Map<String, Object>> list = this.cardFileService.getCardTypeByFileId(id, condition, sort);
        Integer typeCount = cardTypeMapper.selectCount(new QueryWrapper<CardType>().eq("file_id", id));
        CardFile cardFile = cardFileMapper.selectById(id);
        return R.ok().data("items",list ).data("fileTypeCount",typeCount).data("fileName",cardFile.getTitle());
    }

    @ApiOperation("收藏和取消收藏文件夹 每点击一次切换一次收藏状态 返回收藏的状态 true为收藏")
    @GetMapping("collectCard")
    public R collectCard(
            @ApiParam(value = "文件夹ID",required = true)
            @RequestParam("id") String id
    ){
        CardFile one = this.cardFileService.getOne(
                new QueryWrapper<CardFile>()
                        .eq("id", id)
        );
        if (one == null){
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }

        Boolean collect = one.getCollect();

        one.setCollect(!collect);

        this.cardFileService.updateById(one);

        return R.ok().data("item",one.getCollect());

    }

}

