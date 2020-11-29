package com.yiyi.service.global.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.CardType;
import com.yiyi.service.global.entity.User;
import com.yiyi.service.global.entity.respon.CardSearchVo;
import com.yiyi.service.global.entity.respon.CardTypeStudyVo;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.CardMapper;
import com.yiyi.service.global.mapper.CardTypeMapper;
import com.yiyi.service.global.service.CardService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@RestController
@RequestMapping("/global/card")
@CrossOrigin
public class CardController {

    @Autowired
    private CardService cardService;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardMapper cardMapper;

    @ApiOperation("增加卡片")
    @PostMapping("saveCard")
    public R saveCard(
            @ApiParam(value = "卡片集ID",required = true)
            @RequestParam("cardTypeId") String cardTypeId,
            @ApiParam(value = "卡片问题 正面",required = true)
            @RequestParam("question") String question,
            @ApiParam(value = "卡片答案 反面",required = true)
            @RequestParam("answer") String answer
    ){
        cardService.saveCard(cardTypeId,question,answer);
        return R.ok().message("新增卡片成功");
    }

    @ApiOperation("点击卡片集查看卡片信息")
    @GetMapping("listCards")
    public R listCards(
            @ApiParam(value = "卡片集的ID",required = true)
            @RequestParam("cardTypeId") String cardTypeId
    ) throws JsonProcessingException {

        String userId = loginFilter.getUser().getId();

        String redisPrefix = "card::"+userId;

        //如果redis中有缓存 从redis取卡片集的卡片
        String cardListString = redisTemplate.opsForValue().get(redisPrefix+cardTypeId);
        if (cardListString != null){
            Map<String, Object> map = objectMapper.readValue(cardListString, new TypeReference<Map<String, Object>>() {
            });

            return R.ok().data(map);
        }



        List<Map<String, Object>> list = cardService.listCards(cardTypeId);

        HashMap<String, Object> map = new HashMap<>();

        map.put("items",list );

        //获取卡片集的标题
        CardType cardType = cardTypeMapper
                .selectOne(new QueryWrapper<CardType>()
                        .eq("id", cardTypeId));

        map.put("cardTypeTitle",cardType.getTitle());

        //获取卡片的数量
        int count = this.cardService.count(
                new QueryWrapper<Card>()
                        .eq("type_id", cardTypeId)
        );

        map.put("totalCard",count);

        String stringMap = objectMapper.writeValueAsString(map);

        //将上面的全部数据存进redis中进行缓存
        redisTemplate.opsForValue().set(redisPrefix+userId,stringMap,5, TimeUnit.HOURS );

        return R.ok().data(map);
    }

    @ApiOperation("收藏和取消收藏卡片 每点击一次切换一次收藏状态 返回收藏的状态 true为收藏")
    @GetMapping("collectCard")
    public R collectCard(
            @ApiParam(value = "卡片ID",required = true)
            @RequestParam("id") String id
    ){
        Card one = this.cardService.getOne(
                new QueryWrapper<Card>()
                        .eq("id", id)
        );
        if (one == null){
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }

        Boolean collect = one.getCollect();

        one.setCollect(!collect);

        this.cardService.updateById(one);

        return R.ok().data("item",one.getCollect());

    }

    @ApiOperation("修改卡片信息")
    @PutMapping("updateCard")
    public R updateCard(
            @RequestBody Card card
    ){
        boolean update = this.cardService.updateById(card);
        if (update){
            return R.ok().message("修改成功");
        }else {
            return R.ok().message("没有这张卡片");
        }
    }

    @ApiOperation("删除卡片")
    @DeleteMapping("deleteCard")
    public R delete(
            @ApiParam(value = "卡片ID",required = true)
            @RequestParam("id") String id
            ){
        boolean remove = this.cardService.removeById(id);
        if (remove){
            return R.ok().message("删除成功");
        }else {
            return R.ok().message("没有这张卡片");
        }
    }

    @ApiOperation("搜索卡片")
    @GetMapping("searchCard")
    public R searchCard(
        @ApiParam(value = "搜索的条件",required = true)
        @RequestParam("search") String search
    ){
        List<CardSearchVo> cardSearchVoList = this.cardService.searchCard(search);
        if (CollectionUtils.isEmpty(cardSearchVoList)){
            return R.ok().message("没有搜索到东西哦");
        }else {
            return R.ok().data("items",cardSearchVoList );
        }
    }


    //================================= 学习功能

    @ApiOperation("点击首页学习时要展示的内容 包括要学习的卡片集和要学习的卡片和卡片集数量")
    @GetMapping("/study/showStudy")
    public R showStudy(){

        List<CardTypeStudyVo> voList = this.cardService.showStudy();

        Integer totalCardType = voList.size();

        int totalCardCount = 0;

        for (CardTypeStudyVo vo : voList) {
            totalCardCount += vo.getTotalStudyCard();
        }

        long day = this.cardService.countUserStudyDay();

        HashMap<String, Object> map = new HashMap<>();
        map.put("items",voList );
        map.put("totalCardType",totalCardType );
        map.put("totalCard", totalCardCount);
        map.put("studyDay", day);
        return R.ok().data(map);
    }

    @ApiOperation("点击查看当天卡片集完成和待完成情况")
    @GetMapping("/study/showStudyAccomplish")
    public R showStudyAccomplish(
            @ApiParam(value = "点击要查看哪种情况 false表示未完成的 true表示已经完成的情况",required = true)
            @RequestParam("flag") Boolean flag
    ){
        List<CardTypeStudyVo> voList = cardService.showStudyAccomplish(flag);
        return R.ok().data("items",voList );
    }


    @ApiOperation("点击 ‘开始学习’ 时调用的接口")
    @GetMapping("/study/startStudy/{type}/{cardTypeId}")
    public R startStudy(
            @ApiParam(value = "学习的方式 0表示配对 1表示排序 2表示书写答案",required = true)
            @PathVariable(value = "type") Integer type,

            @ApiParam(value = "学习集的ID",required = true)
            @PathVariable(value = "cardTypeId") String id,

            @ApiParam(value = "卡片ID",required = false)
            @RequestParam(value = "cardId",required = false) String cardId,

            @ApiParam(value = "答题时间 毫秒为单位",required = false)
            @RequestParam(value = "answerTime" ,required = false) Integer answerTime,

            @ApiParam(value = "熟练度",required = false)
            @RequestParam(value = "familiarity",required = false) String familiarity
    ) throws JsonProcessingException {
        if (type == 0) {
            Card card = cardService.startStudy(id,cardId,answerTime,familiarity);
            if (card!=null) {
                return R.ok().data("item", card);
            }else {
                return R.ok().code(99999).message("您已经学习完该卡片集了哦");
            }
        }else {
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }
    }

}

