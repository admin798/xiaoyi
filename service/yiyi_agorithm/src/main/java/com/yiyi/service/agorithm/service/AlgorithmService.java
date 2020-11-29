package com.yiyi.service.agorithm.service;


import com.yiyi.service.agorithm.entity.CardType;

import java.io.IOException;
import java.util.List;

/**
 * @author : Jack.F
 * @date : 2020/11/1
 */
public interface AlgorithmService {
    List<CardType> recommend() throws IOException;
}
