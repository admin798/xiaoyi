package com.yiyi.service.agorithm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiyi.service.agorithm.entity.Calculate;
import com.yiyi.service.agorithm.entity.Card;
import com.yiyi.service.agorithm.entity.CardType;
import com.yiyi.service.agorithm.entity.Data;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalculateMapper extends BaseMapper<Calculate> {

    List<CardType> recommend(@Param("preDate")String preDate,@Param("date")String date);

    CardType getType(String id);

    List<Card> listCard(String id);

    List<Data> getData(@Param("title") String title);
}
