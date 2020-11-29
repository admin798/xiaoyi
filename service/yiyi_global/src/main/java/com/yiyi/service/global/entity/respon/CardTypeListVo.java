package com.yiyi.service.global.entity.respon;

import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.CardType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : Jack.F
 * @date : 2020/9/23
 */
@Data
public class CardTypeListVo extends CardType {

    @ApiModelProperty("用户一个卡片集中的卡片")
    private List<Card> cardList;

    @ApiModelProperty("用户卡片集的总数")
    private Integer count;

}
