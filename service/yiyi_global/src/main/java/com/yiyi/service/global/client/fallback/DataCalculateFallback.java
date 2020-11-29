package com.yiyi.service.global.client.fallback;

import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.R;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.client.DataCalculateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : Jack.F
 * @date : 2020/11/6
 */
@Component
@Slf4j
public class DataCalculateFallback implements DataCalculateClient {

    @Override
    public R getCardType(String userId) {
        throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
    }

    @Override
    public R getLine(String userId, String typeId) {
        throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
    }
}
