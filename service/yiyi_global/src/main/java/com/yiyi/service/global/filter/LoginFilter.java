package com.yiyi.service.global.filter;

import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.User;
import com.yiyi.service.global.entity.respon.UserInfo;
import com.yiyi.service.global.propertiesUtil.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Jack.F
 * @Date: 2020/9/15 17:31
 */

@Component
public class LoginFilter implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final ThreadLocal<UserInfo> t1 = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("token");
        String prefix = "userLogin::";
        String redisToken = redisTemplate.opsForValue().get(prefix+token);

        if (StringUtils.isBlank(redisToken)){
            throw new YiyiException(ResultCodeEnum.LOGIN_AUTH);
        }

        UserInfo userInfo = JwtUtils.getUserInfo(request);
        if (userInfo == null){
            throw new YiyiException(ResultCodeEnum.LOGIN_AUTH);
        }
        t1.set(userInfo);
        return true;
    }

    public UserInfo getUser(){
        return t1.get();
    }


    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex)
            throws Exception {
        t1.remove();
    }
}
