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
@TableName("note_card_file")
@ApiModel(value="CardFile对象", description="")
public class CardFile extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "记忆库标题")
    private String title;

    @ApiModelProperty(value = "记忆库简介")
    private String introduce;

    @ApiModelProperty("卡片是否收藏")
    @TableField(value = "is_collect")
    private Boolean collect;

}
