package com.yiyi.service.global.entity.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserLogin {
    private String mobile;
    private String password;
}
