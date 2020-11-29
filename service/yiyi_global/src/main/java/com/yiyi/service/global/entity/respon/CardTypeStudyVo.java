package com.yiyi.service.global.entity.respon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.CardType;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author : Jack.F
 * @date : 2020/9/24
 */
@Data
public class CardTypeStudyVo extends CardType {

    @ApiModelProperty("片库中一共需要学习的卡片数量")
    private Integer totalStudyCard;

    @ApiModelProperty("卡片库新建的卡片数量")
    private Integer createCardCount;

    @ApiModelProperty("卡片库中今天需要学习的卡片数量")
    private Integer studyCard;

    @ApiModelProperty("卡片库中要复习的卡片数量")
    private Integer reviewCard;

    private List<Card> cards;

    @JsonIgnore
    private Date preViewDay;


}
