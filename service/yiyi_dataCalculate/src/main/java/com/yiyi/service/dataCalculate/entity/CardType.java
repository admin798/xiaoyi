package com.yiyi.service.dataCalculate.entity;

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

    @ApiModelProperty(value = "记忆库ID")
    private String fileId;

    @ApiModelProperty(value = "卡片集标题")
    private String title;

    @ApiModelProperty(value = "用户是否下载卡片集")
    @TableField("is_upload")
    private Boolean upload;

    @ApiModelProperty(value = "是否收藏该卡片集")
    @TableField("is_collect")
    private Boolean collect;

    @ApiModelProperty(value = "卡片集学习的次数")
    private Integer studyCount;

    @ApiModelProperty(value = "卡片集学习天数")
    private Integer studyTime;

    @ApiModelProperty(value = "用于提醒用户下次学习时间")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date nextStudyTime;

    @ApiModelProperty(value = "卡片集描述")
    private String introduce;



}
