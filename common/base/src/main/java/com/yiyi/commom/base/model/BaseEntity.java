package com.yiyi.commom.base.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: Jack.F
 * @Date: 2020/9/12 23:19
 */

@Data
public class BaseEntity implements Serializable {

    @ApiModelProperty(value = "用户ID 后端自动完成创建")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "创建时间 后端自动完成创建")
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private Date gmtCreate;

    @ApiModelProperty(value = "更新时间 后端自动完成创建")
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date gmtModified;
}
