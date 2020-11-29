package com.yiyi.service.global.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yiyi.commom.base.model.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
@TableName("note_user")
@ApiModel(value="User对象", description="")
public class User extends BaseEntity {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    @Pattern(regexp = "^1[356789]\\d{9}$",message = "手机号码不正确")
    private String mobile;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "用户头像地址 后端自动完成创建")
    private String avatar;

    @ApiModelProperty(value = "用户个性签名")
    private String sign;

    @ApiModelProperty(value = "是否注销 1(删除) 0(未删除) 作为用户注销的操作 后端自动完成创建")
    @TableField("is_deleted")
    @JsonIgnore
    private Boolean deleted;

    @ApiModelProperty(value = "密码盐值 后端自动完成创建")
    @JsonIgnore
    private String salt;

}
