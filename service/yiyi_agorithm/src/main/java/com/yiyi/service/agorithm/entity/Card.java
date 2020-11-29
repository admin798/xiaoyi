package com.yiyi.service.agorithm.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yiyi.commom.base.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Data
@Accessors(chain = true)
@TableName("note_card")
@ApiModel(value="Card对象", description="")
public class Card extends BaseEntity{

    private String question;
    @JsonIgnore
    private String answer1;
    private String[] answer;
    @JsonIgnore
    private Calculate calculate;

}
