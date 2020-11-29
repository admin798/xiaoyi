package com.yiyi.service.global.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yiyi.service.global.entity.Card;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiyi.service.global.entity.respon.CardSearchVo;
import com.yiyi.service.global.entity.respon.CardTypeStudyVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
public interface CardService extends IService<Card> {

    void saveCard(String cardTypeId, String question, String answer);

    List<Map<String, Object>> listCards(String cardTypeId) throws JsonProcessingException;

    List<CardSearchVo> searchCard(String search);

    List<CardTypeStudyVo> showStudy();

    List<CardTypeStudyVo> showStudyAccomplish(Boolean flag);

    Card startStudy(String id, String cardId, Integer answerTime, String familiarity) throws JsonProcessingException;

    long countUserStudyDay();
}
