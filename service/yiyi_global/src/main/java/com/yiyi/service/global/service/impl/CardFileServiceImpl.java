package com.yiyi.service.global.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.CardFile;
import com.yiyi.service.global.entity.CardType;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.CardFileMapper;
import com.yiyi.service.global.mapper.CardMapper;
import com.yiyi.service.global.mapper.CardTypeMapper;
import com.yiyi.service.global.service.CardFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiyi.service.global.service.CardTypeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Service
public class CardFileServiceImpl extends ServiceImpl<CardFileMapper, CardFile> implements CardFileService {

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private CardMapper cardMapper;

    /**
     * 创建新的文件夹
     * @param title
     * @param introduce
     * @param cardTypeId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createFile(String title, String introduce, List<String> cardTypeId) {
        //新增文件夹
        CardFile cardFile = new CardFile();
        cardFile.setIntroduce(introduce);
        cardFile.setTitle(title);
        cardFile.setUserId(loginFilter.getUser().getId());
        this.baseMapper.insert(cardFile);

        //新增卡片库
        cardTypeId.forEach(id->{
            CardType cardType = new CardType();
            cardType.setFileId(cardFile.getId());
            cardType.setId(id);
            cardTypeMapper.updateById(cardType);
        });
    }

    /**
     * 删除文件夹
     * @param id
     */
    @Override
    public void deleteFile(String id) {
        this.baseMapper.deleteById(id);
        List<CardType> cardTypeList = cardTypeMapper.selectList(new QueryWrapper<CardType>().eq("file_id", id));

        if (CollectionUtils.isEmpty(cardTypeList)){
            return;
        }

        List<String> cardTypeIds = cardTypeList.stream().map(cardType -> {
            return cardType.getId();
        }).collect(Collectors.toList());

        cardTypeIds.forEach(cardTypeId ->{
            cardTypeService.deleteCardType(cardTypeId);
        });
    }

    /**
     * 展示一个文件夹的所有卡片集
     * @param id
     * @param condition
     * @param sort
     * @return
     */
    @Override
    public List<Map<String, Object>> getCardTypeByFileId(String id, List<String> condition, String sort) {

        QueryWrapper<CardType> queryWrapper = new QueryWrapper<>();

        if (!CollectionUtils.isEmpty(condition) && StringUtils.isNotBlank(sort)) {
            String[] strings = String.join(",", condition).split(",");
            queryWrapper.orderBy(true, "asc".equals(sort), strings);
        }
        queryWrapper.eq("file_id", id).select("id", "title", "introduce", "is_collect");

        List<Map<String, Object>> list = cardTypeMapper.selectMaps(queryWrapper);
        list.forEach(l->{
            Integer count = cardMapper.selectCount(new QueryWrapper<Card>().eq("type_id", l.get(id)));
            l.put("totalCount",count );
        });

        return list;
    }


}
