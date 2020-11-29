package com.yiyi.service.global.entity.respon;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author : Jack.F
 * @date : 2020/9/22
 */
@Data
public class UserInfo {


    @ApiModelProperty(value = "用户ID")
    private String id;

    @ApiModelProperty(value = "用户昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String mobile;

    @ApiModelProperty(value = "用户头像地址 后端自动完成创建")
    private String avatar;

    @ApiModelProperty(value = "用户个性签名")
    private String sign;

    public UserInfo() {
    }

    public UserInfo(String id, String nickname, String mobile, String avatar, String sign) {
        this.id = id;
        this.nickname = nickname;
        this.mobile = mobile;
        this.avatar = avatar;
        this.sign = sign;
    }
}
