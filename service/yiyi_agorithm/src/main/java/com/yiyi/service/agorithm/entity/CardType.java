package com.yiyi.service.agorithm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yiyi.commom.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("note_card_type")
@ApiModel(value="CardType对象", description="")
public class CardType extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "卡片集标题")
    private String title;

    @ApiModelProperty(value = "卡片集描述")
    private String introduce;

    private String pubStr;

    private String sameCardType;

    private String userNickId;

    private List<Card> cards;


}
