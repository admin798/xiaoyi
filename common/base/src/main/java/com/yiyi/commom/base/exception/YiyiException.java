package com.yiyi.commom.base.exception;

import com.yiyi.commom.base.result.ResultCodeEnum;
import lombok.Data;

/**
 * @author : Jack.F
 * @date : 2020/9/9
 */

@Data
public class YiyiException extends RuntimeException {

    private Integer code;

    private String message;

    public YiyiException(Integer code,String message){
        super(message);
        this.message = message;
        this.code = code;
    }

    public YiyiException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.message = resultCodeEnum.getMessage();
        this.code = resultCodeEnum.getCode();
    }

}
