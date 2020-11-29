package com.yiyi.service.global.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.yiyi.service.global.entity.CardType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yiyi.service.global.entity.respon.CardTypeStudyVo;
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
public interface CardTypeMapper extends BaseMapper<CardType> {

    List<CardTypeStudyVo> showStudy(@Param(Constants.WRAPPER) QueryWrapper<CardTypeStudyVo> queryWrapper);

    Integer getCardCreateCount(@Param("id") String id, @Param("createTime") String createTime);

    Integer getCardStudyCount(@Param("id") String id, @Param("studyTime") String studyTime);

    Integer getCardReviewCount(@Param("id") String id, @Param("reviewTime") String reviewTime);
}
