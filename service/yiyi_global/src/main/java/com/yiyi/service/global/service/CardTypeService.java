package com.yiyi.service.global.service;

import com.yiyi.service.global.entity.CardType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiyi.service.global.entity.respon.CardTypeListVo;

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
public interface CardTypeService extends IService<CardType> {

    List<CardType> getCardType();

    String createCardTypeAndCard(String cardTypeId, String title, String introduce,String question,String answer);

    List<Map<String, Object>> listCardType();

    void deleteCardType(String id);
}
