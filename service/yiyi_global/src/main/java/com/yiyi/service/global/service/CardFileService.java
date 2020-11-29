package com.yiyi.service.global.service;

import com.yiyi.service.global.entity.CardFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
public interface CardFileService extends IService<CardFile> {

    void createFile(String title, String introduce, List<String> cardTypeId);

    void deleteFile(String id);

    List<Map<String, Object>> getCardTypeByFileId(String id, List<String> condition, String sort);
}
