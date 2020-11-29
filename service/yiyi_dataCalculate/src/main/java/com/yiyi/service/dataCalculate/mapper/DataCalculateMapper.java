package com.yiyi.service.dataCalculate.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiyi.service.dataCalculate.entity.Calculate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author : Jack.F
 * @date : 2020/11/4
 */
@Repository
public interface DataCalculateMapper extends BaseMapper<Calculate> {
    List<String> getCardType(@Param("userId") String userId, @Param("preDate") String preDate, @Param("nowDate") String nowDate);

    Map<String,Object> getTypeDetail(@Param("l") String l);

    List<Calculate> getLine(@Param("userId") String userId, @Param("typeId") String typeId, @Param("preDate") String preDate, @Param("nowDate") String nowDate);

    List<Map<String, Object>> getPic(@Param("userId") String userId, @Param("preDate") String preDate, @Param("nowDate") String nowDate);
}
