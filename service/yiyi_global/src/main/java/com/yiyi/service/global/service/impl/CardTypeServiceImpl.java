package com.yiyi.service.global.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.CardType;
import com.yiyi.service.global.entity.respon.CardTypeListVo;
import com.yiyi.service.global.entity.respon.UserInfo;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.CardMapper;
import com.yiyi.service.global.mapper.CardTypeMapper;
import com.yiyi.service.global.service.CardTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Service
public class CardTypeServiceImpl extends ServiceImpl<CardTypeMapper, CardType> implements CardTypeService {

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardMapper cardMapper;

    /**
     * 此方法用户创建卡片时查询学习集
     * @return
     */
    @Override
    public List<CardType> getCardType() {

        UserInfo userInfo = loginFilter.getUser();

        String userInfoId = userInfo.getId();

        QueryWrapper<CardType> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id",userInfoId )
                .select("id","title","introduce");

        List<CardType> typeList = this.baseMapper.selectList(queryWrapper);

        return typeList;

    }

    /**
     * 此方法用于创建卡片集时添加卡片
     * @param cardTypeId
     * @param title
     * @param introduce
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createCardTypeAndCard(String cardTypeId, String title, String introduce,String question,String answer) {
        if (StringUtils.isBlank(cardTypeId)){

            //如果没有传递卡片集ID 就是新增卡片集和卡片
            CardType cardType = new CardType();

            cardType.setTitle(title);
            cardType.setIntroduce(introduce);
            cardType.setUserId(loginFilter.getUser().getId());
            cardType.setNextStudyTime(new Date());

            this.baseMapper.insert(cardType);

            //更新卡片信息
            Card card = new Card();
            card.setQuestion(question);
            card.setAnswer(answer);
            card.setTypeId(cardType.getId());
            card.setNextStudyTime(new Date());
            card.setUserId(loginFilter.getUser().getId());
            card.setPage("1");

            cardMapper.insert(card);

            return cardType.getId();
        }else {
            //如果传递了卡片集ID 就只是增加卡片集
            CardType cardType = this.baseMapper.selectById(cardTypeId);
            if (cardType == null){
                throw new YiyiException(29001,"没有该卡片集");
            }

            Integer page = cardMapper.selectCount(
                    new QueryWrapper<Card>()
                            .eq("type_id", cardTypeId)
            );

            //更新卡片信息
            Card card = new Card();
            card.setQuestion(question);
            card.setAnswer(answer);
            card.setTypeId(cardType.getId());
            card.setUserId(loginFilter.getUser().getId());
            card.setPage(String.valueOf(++page));

            //设定下一次学习的时间
            card.setNextStudyTime(new Date());
            cardMapper.insert(card);

            return cardType.getId();
        }

    }

    /**
     * 展示用户的卡片集和一张卡片
     * @return
     */
    @Override
    public List<Map<String, Object>> listCardType() {

        List<Map<String, Object>> cardTypeList = this.baseMapper.selectMaps(
                new QueryWrapper<CardType>()
                        .eq("user_id", loginFilter.getUser().getId())
                        .select("id", "title", "is_collect", "introduce")
        );

        cardTypeList.forEach(c->{
            Integer cardCount = cardMapper.selectCount(new QueryWrapper<Card>()
                    .eq("user_id", loginFilter.getUser().getId())
                    .eq("type_id", c.get("id")));
            c.put("totalCount",cardCount );
            c.put("model","cardType");
        });

        return cardTypeList;
    }

    /**
     * 删除卡片集
     * @param id
     */
    @Override
    public void deleteCardType(String id) {
        //首先删除卡片集
        this.baseMapper.deleteById(id);

        List<Card> cards = cardMapper.selectList(new QueryWrapper<Card>().eq("type_id", id));

        List<String> cardIds = cards.stream().map(card -> {
            return card.getId();
        }).collect(Collectors.toList());

        if (!CollectionUtils.isEmpty(cardIds)) {
            cardMapper.deleteBatchIds(cardIds);
        }
    }


}
