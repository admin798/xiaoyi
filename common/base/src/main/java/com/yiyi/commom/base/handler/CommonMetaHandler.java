package com.yiyi.commom.base.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author : Jack.F
 * @date : 2020/9/9
 */

@Component
public class CommonMetaHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("gmtCreate", new Date(),metaObject );
        this.setFieldValByName("gmtModified", new Date(),metaObject );
    }

    @Override
    public void updateFill(MetaObject metaObject){
        this.setFieldValByName("gmtModified", new Date(),metaObject );
    }
}
