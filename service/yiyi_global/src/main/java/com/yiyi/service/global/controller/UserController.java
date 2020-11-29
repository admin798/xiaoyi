package com.yiyi.service.global.controller;


import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.User;
import com.yiyi.service.global.entity.request.UserLogin;
import com.yiyi.service.global.entity.request.UserRegisterVo;
import com.yiyi.service.global.entity.respon.UserInfo;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.omg.PortableInterceptor.LOCATION_FORWARD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@RestController
@RequestMapping("/global/user")
@Api("用户中心接口")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginFilter loginFilter;

    @PostMapping("/register")
    @ApiOperation("用户注册接口")
    public R register(
            @RequestBody @Valid UserRegisterVo user
    ){
        userService.regimster(user);
        return R.ok().message("注册成功");

    }

    @GetMapping("/getSmsCode/{mobile}")
    @ApiOperation("获取短信验证码")
    public R getSmsCode(
            @ApiParam(value = "用户的手机号",required = true)
            @PathVariable("mobile") String mobile
    ) throws Exception {
        userService.getSmsCode(mobile);
        return R.ok().message("短信发送成功");
    }

    @ApiOperation("用户登录接口")
    @PostMapping("login")
    public R login(
            @ApiParam(value = "用户手机号",required = true)
            @RequestParam("mobile") String mobile,
            @ApiParam(value = "用户密码",required = true)
            @RequestParam("password") String password,
            HttpServletRequest request
    ){

        String token = userService.login(mobile, password, request);

        if (StringUtils.isBlank(token)){
            return R.error().message("登录失败");
        }else {
            return R.ok().message("登录成功").data("item",token);
        }
    }

    @GetMapping("/getUserInfo")
    @ApiOperation("查看用户信息 可用于查看设置中的个人信息")
    public R getUserInfo(HttpServletRequest request){
        UserInfo userInfo = loginFilter.getUser();
        if (userInfo!=null){
            return R.ok().data("user",userInfo);
        }else {
            return R.setResult(ResultCodeEnum.LOGIN_AUTH);
        }
    }

    @GetMapping("getUserCollection")
    @ApiOperation("查看用户的收藏信息")
    public R getUserCollection(){
        return null;
    }

    @PostMapping("/upload")
    @ApiOperation("用户图片上传")
    public R uploadAvatar(
            @ApiParam(value = "图片流信息",required = true)
            @RequestParam("file") MultipartFile file,
            HttpServletRequest request
    ){
        String path = userService.uploadAvatar(file, request);
        return R.ok().message("上传图片成功").data("item",path);
    }

    @ApiOperation("PDF文件导出笔记")
    @GetMapping("fileToPDF/{id}")
    public R fileToPDF(@PathVariable("id") String id){
        userService.fileToPDF(id);
        return R.ok();
    }

    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public R logout(HttpServletRequest request){
        userService.logout(request);
        return R.ok().message("退出成功");
    }
}

