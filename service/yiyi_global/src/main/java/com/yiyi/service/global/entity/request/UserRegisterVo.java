package com.yiyi.service.global.entity.request;

import com.yiyi.service.global.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : Jack.F
 * @date : 2020/9/22
 */
@Data
public class UserRegisterVo extends User {
    @ApiModelProperty("验证码")
    private String code;
}
