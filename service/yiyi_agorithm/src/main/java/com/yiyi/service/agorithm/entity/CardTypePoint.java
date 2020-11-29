package com.yiyi.service.agorithm.entity;

import lombok.Data;
import lombok.ToString;

/**
 * @author : Jack.F
 * @date : 2020/11/1
 */
@Data
@ToString
public class CardTypePoint {

    private String cardId;
    private String userId;
    private String sameCardType;
    private String point;
}
