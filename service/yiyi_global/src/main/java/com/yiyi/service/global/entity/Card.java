package com.yiyi.service.global.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.yiyi.commom.base.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("note_card")
@ApiModel(value="Card对象", description="")
public class Card extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "记忆库ID")
    private String fileId;

    @ApiModelProperty(value = "卡片集ID")
    private String typeId;

    @ApiModelProperty(value = "卡片问题")
    private String question;

    @ApiModelProperty(value = "卡片答案")
    private String answer;

    @ApiModelProperty("卡片在卡片集中的页码")
    private String page;

    @ApiModelProperty(value = "记忆时间 毫秒为单位")
    private Integer memoryTime;

    @ApiModelProperty(value = "卡片熟悉程度 0(陌生) 1(犹豫) 2(熟悉)")
    private Integer familiarity;

    @ApiModelProperty(value = "卡片下一次学习时间")
    private Date nextStudyTime;

    @ApiModelProperty(value = "卡片上一次学习时间")
    private Date preStudyTime;

    @ApiModelProperty(value = "用于对卡片答案的配对 json形式的数组")
    private String otherAnswer;

    @ApiModelProperty("卡片是否收藏")
    @TableField(value = "is_collect")
    private Boolean collect;

    @TableField(exist = false)
    public boolean flag = false;


}
