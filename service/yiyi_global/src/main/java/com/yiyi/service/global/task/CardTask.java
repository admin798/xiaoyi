package com.yiyi.service.global.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.mapper.CardMapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : Jack.F
 * @date : 2020/10/24
 */
@Component
public class CardTask {

    @Autowired
    private CardMapper cardMapper;

    /**
     * 如果还没学习就自增一天继续学习
     */
    //0/5 * * * * ?
    //0 0 1 1/1 * ?
    @Scheduled(cron = "0 0 1 1/1 * ?")
    public void autoAddNextStudyTime(){
        UpdateWrapper<Card> updateWrapper = new UpdateWrapper<>();
        String preDay = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        updateWrapper.eq("next_study_time", preDay);
        Card card = new Card();
        card.setNextStudyTime(new Date());
        cardMapper.update(card,updateWrapper );
    }
}
