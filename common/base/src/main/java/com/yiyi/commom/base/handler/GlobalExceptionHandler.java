package com.yiyi.commom.base.handler;

import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.utils.ExceptionUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : Jack.F
 * @date : 2020/9/9
 */

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R catchGlobalException(Exception e) {
        log.error(ExceptionUtil.getMessage(e));
        return R.error();
    }

    @ResponseBody
    @ExceptionHandler(YiyiException.class)
    public R catchYiyiException(YiyiException e) {
         log.error(ExceptionUtil.getMessage(e));
         return R.error().message(e.getMessage()).code(e.getCode());
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R catchMissingServletRequestParameterException(MissingServletRequestParameterException m){
        log.error(ExceptionUtil.getMessage(m));
        return R.error().message("参数传递异常");
    }

    //ExpiredJwtException
    @ResponseBody
    @ExceptionHandler(ExpiredJwtException.class)
    public R catchExpiredJwtException(ExpiredJwtException e){
        log.error(ExceptionUtil.getMessage(e));
        return R.error().message("需要登录").code(28004);
    }

}
