package com.yiyi.service.global.service;

import java.util.List;
import java.util.Map;

/**
 * @author : Jack.F
 * @date : 2020/11/20
 */
public interface ImageService {


    List<Map<String, Object>> getStudyType(String date, String userId);
}
