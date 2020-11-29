package com.yiyi.service.global.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.service.global.entity.Image;
import com.yiyi.service.global.mapper.ImageMapper;
import com.yiyi.service.global.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : Jack.F
 * @date : 2020/11/20
 */
@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageMapper imageMapper;

    @Override
    public List<Map<String, Object>> getStudyType(String date, String userId) {
        QueryWrapper<Image> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId )
                .eq("study_date",date );

        List<Map<String, Object>> list = imageMapper.selectMaps(queryWrapper);
        return list;
    }
}
