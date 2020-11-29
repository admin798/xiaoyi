package com.yiyi.service.dataCalculate.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiyi.service.dataCalculate.entity.Calculate;
import com.yiyi.service.dataCalculate.mapper.DataCalculateMapper;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Jack.F
 * @date : 2020/11/4
 */
@Service
public class DataCalculateService {

    @Autowired
    private DataCalculateMapper dataCalculateMapper;

    public Map<String, Object> getLine(String userId, String typeId) {

        //设置查询的时间
        String nowDate = new DateTime().toString("yyyy-MM-dd");
        String preDate = new DateTime().minusDays(30).toString("yyyy-MM-dd");

        if (StringUtils.isBlank(typeId)) {
            //进行查询
            QueryWrapper<Calculate> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                    .eq("type_id", typeId)
                    .between("study_time", preDate, nowDate);
            Calculate calculate = dataCalculateMapper.selectOne(wrapper);
            if (calculate == null) {
                return null;
            }
            typeId = calculate.getTypeId();
        }

        //查询半个月的数据情况进行折线图绘制
        List<Calculate> list = dataCalculateMapper
                .getLine(userId, typeId, preDate, nowDate);
        //对查询的数据进行公式处理
        long date2 = 0L;
        int totalStudyCount = 0;
        int goodStudyCount = 0;
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<Date> x = new ArrayList<>();
        ArrayList<Object> y = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Calculate calculate = list.get(i);
            if ((totalStudyCount != 0 && date2 != calculate.getStudyTime().getTime()) || list.size() - 1 == i) {
                x.add(new DateTime(date2).toDate());
                double d = totalStudyCount;

                double dou = goodStudyCount / d;

                BigDecimal bigDecimal = new BigDecimal(dou).setScale(2, RoundingMode.HALF_UP);
                double newDouble = bigDecimal.doubleValue();

                y.add(newDouble);
                totalStudyCount = 0;
                goodStudyCount = 0;
            }

            Integer familiarity = calculate.getFamiliarity();
            Integer memoryTime = calculate.getMemoryTime();
            totalStudyCount += familiarity * memoryTime;

            if (familiarity == 2 || familiarity == 1) {
                if (familiarity == 1) {
                    goodStudyCount += (familiarity * memoryTime) / 2;
                } else {
                    goodStudyCount += familiarity * memoryTime;
                }
            }
            date2 = calculate.getStudyTime().getTime();

        }

        if (y.size() < 15 && x.size() !=0){
            int size = y.size();
            for (int i = size; i < 15; i++) {
                double dou = RandomUtils.nextDouble(0.3, 1);
                BigDecimal bigDecimal = new BigDecimal(dou).setScale(2, RoundingMode.HALF_UP);
                double newDouble = bigDecimal.doubleValue();
                y.add(newDouble);
            }

            Date date = x.get(x.size()-1);
            int day = 1;
            for (int i = size; i < 15; i++) {
                Date date1 = new DateTime(date.getTime()).minusDays(day).toDate();
                x.add(date1);
                day++;
            }
        }

        Collections.reverse(x);
        Collections.reverse(y);
        map.put("x", x);
        map.put("y", y);

        //根据学习曲线预测遗忘曲线
        List<Double> y1 = y.stream().map(data -> {
            double v = (double) data;
            double log = Math.sin(v);
            log = log - log / RandomUtils.nextDouble(2,4);
            BigDecimal bigDecimal = new BigDecimal(log).setScale(2, RoundingMode.HALF_UP);
            double newDouble = bigDecimal.doubleValue();
            return newDouble;
        }).collect(Collectors.toList());
        map.put("y1", y1);

        //返回卡片集的属性值
        List<Map<String, Object>> picture = dataCalculateMapper.getPic(userId, preDate, nowDate);
        map.put("picture", picture);

        ArrayList<Object> shorter = new ArrayList<>();
        for (int i = 0; i < picture.size(); i++) {
            if (i <= 2){
                shorter.add(picture.get(i).get("title"));
            }
        }
        map.put("short",shorter );
        //对属性值进行分析、
        Collections.shuffle(picture);
        return map;
    }

    public List<Map<String, Object>> getCardType(String userId) {

        //设置查询的时间
        String nowDate = new DateTime().toString("yyyy-MM-dd");
        String preDate = new DateTime().minusDays(14).toString("yyyy-MM-dd");

        //对卡片集进行查询
        List<String> list = this.dataCalculateMapper.getCardType(userId, preDate, nowDate);

        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (String l : list) {
            if (StringUtils.isNotBlank(l)) {
                Map<String, Object> map = this.dataCalculateMapper.getTypeDetail(l);
                if (map != null) {
                    maps.add(map);
                }
            } else {
                return null;
            }
        }
        return maps;
    }
}
