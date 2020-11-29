package com.yiyi.service.global.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yiyi.service.global.entity.Card;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiyi.service.global.entity.respon.CardSearchVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Repository
public interface CardMapper extends BaseMapper<Card> {

    /**
     * 分页查询
     * @param queryWrapper
     * @return
     */
    List<CardSearchVo> searchCard(@Param(Constants.WRAPPER) QueryWrapper<CardSearchVo> queryWrapper);

    Integer getCreateCardCount(@Param("typeId") String id, @Param("userId") String id1, @Param("dateString") String dateString);

}
