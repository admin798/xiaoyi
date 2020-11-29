package com.yiyi.service.agorithm.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyi.service.agorithm.entity.Card;
import com.yiyi.service.agorithm.entity.CardType;
import com.yiyi.service.agorithm.entity.Data;
import com.yiyi.service.agorithm.mapper.CalculateMapper;
import com.yiyi.service.agorithm.service.AlgorithmService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : Jack.F
 * @date : 2020/11/1
 */
@Service
@Slf4j
public class AlgorithmServiceImpl implements AlgorithmService {

    @Autowired
    private CalculateMapper calculateMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 推荐算法的实现
     *
     * @return
     */
    @Override
    public List<CardType> recommend() throws IOException {
        ///String path = "C:\\Users\\86130\\Desktop\\TEST\\demo.txt";
        String path = "/usr/local/yiyi/demo.txt";
        //查询数据库中的数据存入txt文件中
        String date = new DateTime().toString("yyyy-MM-dd");
        String perDate = new DateTime().minusDays(7).toString("yyyy-MM-dd");
        List<CardType> cardTypes = calculateMapper.recommend(perDate,date);
        int userId = 0;
        //对list遍历进行公共词条合并
        cardTypes.forEach(cardType -> {
            StringBuffer buffer = new StringBuffer();
            buffer.append(cardType.getTitle() + cardType.getIntroduce());
            cardType.getCards().forEach(card -> {
                buffer.append(card.getQuestion() + card.getAnswer1());
            });
            cardType.setPubStr(buffer.toString());
        });
        //对卡片集进行匹配对比
        int t = 1;
        for (int i = 0; i < cardTypes.size(); i++) {
            cardTypes.get(i).setUserNickId(++userId+"");
            for (int j = 0; j < cardTypes.size(); j++) {
                if (i == j) {
                    continue;
                }
                float res = this.sim(cardTypes.get(i).getPubStr(), cardTypes.get(j).getPubStr());
                if (res >= 0.5) {
                    //则认为是相似的卡片集
                    if (StringUtils.isBlank(cardTypes.get(i).getSameCardType())) {
                        cardTypes.get(i).setSameCardType(t+"");
                        t++;
                    }
                    if (StringUtils.isBlank(cardTypes.get(j).getSameCardType())) {
                        cardTypes.get(j).setSameCardType(cardTypes.get(i).getSameCardType());
                    }
                }else {

                    if (StringUtils.isBlank(cardTypes.get(i).getSameCardType())) {
                        cardTypes.get(i).setSameCardType(t+"");
                        t++;
                    }

                    if (StringUtils.isBlank(cardTypes.get(j).getSameCardType())) {
                        cardTypes.get(j).setSameCardType(t+"");
                        t++;
                    }
                }
            }
        }
        if (cardTypes.size() == 1){
            cardTypes.get(0).setSameCardType("0");
        }
        //使用IO流将数据写入文本文件进行算法预测
        //1.将list进行map转化
        FileWriter fileWriter = new FileWriter(new File(path), true);
        for (CardType cardType : cardTypes) {

            double point = 0D;
            for (Card card : cardType.getCards()) {
                Integer familiarity = card.getCalculate().getFamiliarity();
                Integer memoryTime = card.getCalculate().getMemoryTime();
                if (familiarity == 0){
                    familiarity = 2;
                }else if (familiarity == 1){
                    familiarity = 1;
                }else {
                    familiarity = 2;
                }
                point += familiarity * memoryTime;
            }
            int pointInt = (int)point;
            pointInt = pointInt / 10000;
            if (pointInt >= 10){
                pointInt = 9;
            }
            String line = cardType.getUserId()+","+cardType.getSameCardType()+"," +pointInt;
            try {
                fileWriter.write(line);
                fileWriter.write(System.getProperties().getProperty("line.separator"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        fileWriter.close();
        cardTypes.forEach(cardType -> {
            cardType.getCards().forEach(card -> {
                String[] answer = new String[1];
                answer[0] = card.getAnswer1();
                card.setAnswer(answer);
            });
        });
        getPredictData(path,cardTypes);
        return cardTypes;
    }

    //
    private float sim(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }

        System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        System.out.println("差异步骤：" + dif[len1][len2]);
        //计算相似度
        float similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        System.out.println("相似度：" + similarity);
        return similarity;
    }

    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    //算法
    private void getPredictData(String path, List<CardType> cardTypes) {

        //用户-物品-评分数据文件
        String filePath = path;
        //数据模型
        DataModel dataModel = null;
        try {
            //文件数据转换成数据模型
            dataModel = new FileDataModel(new File(filePath));
            /**
             * 物品相似度定义
             */
            //余弦相似度
          ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(dataModel);
            //定义推荐引擎
            Recommender recommender = new GenericItemBasedRecommender(dataModel, itemSimilarity);
            //获取物品迭代器
            LongPrimitiveIterator itemIDIterator = dataModel.getItemIDs();
            //遍历所有物品
            while (itemIDIterator.hasNext()) {
                System.out.println("==================================================");
                Long itermID = itemIDIterator.next();
                LongPrimitiveIterator otherItemIDIterator = dataModel.getItemIDs();
                //打印物品相似度
                while (otherItemIDIterator.hasNext()) {
                    Long otherItermID = otherItemIDIterator.next();
                    System.out.println("物品 " + itermID + " 与物品 " + otherItermID + " 的相似度为： " + itemSimilarity.itemSimilarity(itermID, otherItermID));
                }
            }
            //获取用户迭代器
            LongPrimitiveIterator userIDIterator = dataModel.getUserIDs();
            //遍历用户
            while (userIDIterator.hasNext()) {
                ArrayList<Map<String,Object>> list = new ArrayList<>();
                //获取用户
                Long userID = userIDIterator.next();
                //获取用户userID的推荐列表
                List<RecommendedItem> itemList = recommender.recommend(userID, 2);
                if (itemList.size() > 0) {
                    for (RecommendedItem item : itemList) {
                        System.out.println("用户 " + userID + " 推荐物品 " +
                                item.getItemID() + ",物品评分 " +
                                item.getValue());
                        log.error("用户 " + userID + " 推荐物品 " +
                                item.getItemID() + ",物品评分 " +
                                item.getValue());
                        int max = 0;
                        //查询相应的cardType 找出物品对应的ID列表 list中
                        for (CardType cardType : cardTypes) {
                            if (item.getItemID() != 0L) {
                                if ((item.getItemID() + "").equals(cardType.getSameCardType())) {
                                    if (max <= 3) {
                                        max++;
                                        HashMap<String, Object> map = new HashMap<>();
                                        //对数据库进行查询
                                        CardType type = calculateMapper.getType(cardType.getId());
                                        map.put("title", cardType.getTitle());
                                        map.put("introduce", cardType.getIntroduce());
                                        map.put("card", cardType.getCards());
                                        map.put("UserCount", RandomUtils.nextInt(500, 5000));
                                        map.put("id", type.getId());
                                        list.add(map);
                                    } else {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    //对list集合进行判断 如果少于5个推荐则进行数据仓库推荐
                    if (list.size() < 8 ){
                        int count = 8 - list.size();
                        //对数据库进行查询
                        String title = "%计算机%";
                        List<Data> data = calculateMapper.getData(title);
                        for (int i = 0; i < count; i++) {
                            try {
                                HashMap<String, Object> map = new HashMap<>();
                                String noteTitle = data.get(i).getNoteTitle();
                                String introduce = data.get(i).getNoteIntroduce();
                                String cardsString = data.get(i).getNoteDetail();
                                List<String> answer = objectMapper.readValue(cardsString, new TypeReference<List<String>>() {

                                });
                                List<HashMap<String, Object>> mapList = new ArrayList<>();
                                for (int h = 0; h < answer.size(); h++) {
                                    String ans = answer.get(h);
                                    int index = ans.indexOf("A.");
                                    if (index != -1) {
                                        String question = ans.substring(0, index);
                                        String answerData = ans.substring(index);
                                        String[] split = answerData.split(" ");
                                        ArrayList<String> list1 = new ArrayList<>();
                                        if (split.length == 4) {
                                            for (int j = 0; j < split.length; j++) {
                                                list1.add(split[j]);
                                            }
                                            if (!CollectionUtils.isEmpty(list1) && !StringUtils.isBlank(question)) {
                                                HashMap<String, Object> map1 = new HashMap<>();
                                                map1.put("question", question);
                                                map1.put("answer", list1);
                                                mapList.add(map1);
                                            }
                                        }
                                    }
                                }

                                //对mapList进行null值去除



                                int index = noteTitle.lastIndexOf(" ");
                                String substringNoteTitle = noteTitle.substring(0,index);
                                int index1 = substringNoteTitle.lastIndexOf(" ");
                                String substring = substringNoteTitle.substring(++index1);
                                map.put("title", substring);
                                map.put("introduce", introduce);
                                map.put("card", mapList);
                                map.put("UserCount", RandomUtils.nextInt(500, 5000));
                                map.put("id",data.get(i).getId() );
                                list.add(map);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                    log.debug(list.toString());
                } else {
                    int count = 8 ;
                    //对数据库进行查询
                    String title = "%计算机%";
                    List<Data> data = calculateMapper.getData(title);
                    for (int i = 0; i < count; i++) {
                        try {
                            HashMap<String, Object> map = new HashMap<>();
                            String noteTitle = data.get(i).getNoteTitle();

                            int index2 = noteTitle.lastIndexOf(" ");
                            String substringNoteTitle = noteTitle.substring(0,index2);
                            int index1 = substringNoteTitle.lastIndexOf(" ");
                            String substring = substringNoteTitle.substring(++index1);

                            String introduce = data.get(i).getNoteIntroduce();
                            String cardsString = data.get(i).getNoteDetail();
                            List<String> answer = objectMapper.readValue(cardsString, new TypeReference<List<String>>() {

                            });
                            List<HashMap<String, Object>> mapList = new ArrayList<>();
                            for (String ans : answer) {
                                int index = ans.indexOf("A.");
                                if (index == -1){
                                    continue;
                                }else {
                                    String question = ans.substring(0, index);
                                    String answerData = ans.substring(index);
                                    String[] split = answerData.split(" ");
                                    ArrayList<String> list1 = new ArrayList<>();
                                    for (int j = 0; j < split.length; j++) {
                                        list1.add(split[j]);
                                    }
                                    if (!CollectionUtils.isEmpty(list1) && !StringUtils.isBlank(question)) {
                                        HashMap<String, Object> map1 = new HashMap<>();
                                        map1.put("question", question);
                                        map1.put("answer", list1);
                                        mapList.add(map1);
                                    }
                                }
                            }


                            map.put("title", substring);
                            map.put("introduce", introduce);
                            map.put("card", mapList);
                            map.put("UserCount", RandomUtils.nextInt(500, 5000));
                            map.put("id", data.get(i).getId());
                            list.add(map);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(list);
                //对每个用户进行redis存储
                String value = objectMapper.writeValueAsString(list);
                redisTemplate.opsForValue().set("recommend"+userID,value );
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
