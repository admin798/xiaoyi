package com.yiyi.service.global.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.service.global.entity.Calculate;
import com.yiyi.service.global.entity.Card;
import com.yiyi.service.global.entity.User;
import com.yiyi.service.global.entity.respon.CardSearchVo;
import com.yiyi.service.global.entity.respon.CardTypeStudyVo;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.CalculateMapper;
import com.yiyi.service.global.mapper.CardMapper;
import com.yiyi.service.global.mapper.CardTypeMapper;
import com.yiyi.service.global.mapper.UserMapper;
import com.yiyi.service.global.service.CardService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private LoginFilter loginFilter;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CalculateMapper calculateMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

    /**
     *
     * @param cardTypeId
     * @param question
     * @param answer
     */
    @Override
    public void saveCard(String cardTypeId, String question, String answer) {

        Integer page = this.baseMapper.selectCount(
                new QueryWrapper<Card>()
                        .eq("type_id", cardTypeId)
        );

        String userId = loginFilter.getUser().getId();
        Card card = new Card();
        card.setTypeId(cardTypeId);
        card.setUserId(userId);
        card.setQuestion(question);
        card.setAnswer(answer);
        card.setNextStudyTime(new Date());
        card.setPage(String.valueOf(page++));
        this.baseMapper.insert(card);
    }

    /**
     * 展示用户一个卡片集的所有卡片
     *
     * @param cardTypeId
     * @return
     */
    @Override
    public List<Map<String, Object>> listCards(String cardTypeId) throws JsonProcessingException {

        String userId = loginFilter.getUser().getId();


        //获取卡片集合
        List<Map<String, Object>> cardList = this.baseMapper.selectMaps(
                new QueryWrapper<Card>()
                        .eq("type_id", cardTypeId)
                        .eq("user_id", userId)
                        .select("id", "question", "answer", "page", "is_collect")
        );

        return cardList;

    }

    /**
     * 搜索卡片
     *
     * @param search
     */
    @Override
    public List<CardSearchVo> searchCard(String search) {

        QueryWrapper<CardSearchVo> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isBlank(search)) {
            queryWrapper
                    .eq("c.user_id", loginFilter.getUser().getId())
                    .like("c.question", search);
        }

        return this.baseMapper.searchCard(queryWrapper);
    }

    /**
     * 进行学习首页的展示
     *
     * @return
     */
    @Override
    public List<CardTypeStudyVo> showStudy() {

        QueryWrapper<CardTypeStudyVo> queryWrapper = new QueryWrapper<>();
        String dateString = new DateTime().toString("yyyy-MM-dd");

        int totalCard = 0;

        //首先获取type的部分内容
        String reviewDay = new DateTime().minusDays(4).toString("yyyy-MM-dd");

        queryWrapper
                .eq("c.user_id", loginFilter.getUser().getId())
                .eq("c.next_study_time", dateString);

        List<CardTypeStudyVo> voList = cardTypeMapper.showStudy(queryWrapper);



        //获取每一类卡片集中的卡片数量
        voList.forEach(vo -> {

            //获取今天新建的卡片数量
//            Integer createCardCount = cardMapper
//                    .getCreateCardCount(vo.getId(), loginFilter.getUser().getId(), dateString);
//
//            vo.setCreateCardCount(createCardCount);

//            //获取获取今天需要学习的卡片数量
//            QueryWrapper<Card> queryWrapper1 = new QueryWrapper<>();
//            queryWrapper1.eq("type_id", vo.getId())
//                    .eq("user_id", loginFilter.getUser().getId())
//                    .eq("next_study_time", dateString);
//            Integer studyCard = cardMapper.selectCount(queryWrapper1);
//            vo.setStudyCard(studyCard);

            //获取今天需要复习的卡片数量
            QueryWrapper<Card> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("type_id", vo.getId())
                    .eq("user_id", loginFilter.getUser().getId())
                    .eq("pre_study_time", reviewDay);
            Integer reviewCard = cardMapper.selectCount(queryWrapper2);
            vo.setReviewCard(reviewCard);

            vo.setTotalStudyCard(vo.getStudyCard() + vo.getCreateCardCount()+vo.getReviewCard());
        });

        return voList;
    }

    /**
     * 展示用户今天已经完成和尚未完成的卡片集
     *
     * @param flag
     */
    @Override
    public List<CardTypeStudyVo> showStudyAccomplish(Boolean flag) {
        QueryWrapper<CardTypeStudyVo> queryWrapper = new QueryWrapper<>();
        String dateString = new DateTime().toString("yyyy-MM-dd");

        //首先获取type的部分内容
        queryWrapper
                .eq("c.user_id", loginFilter.getUser().getId());
        if (flag) {
            //查看已经完成的卡片
            queryWrapper.eq("c.pre_study_time", dateString);
        } else {
            //查看未完成的卡片
            queryWrapper.eq("c.next_study_time", dateString);
        }

        List<CardTypeStudyVo> voList = cardTypeMapper.showStudy(queryWrapper);

        //获取每一类卡片集中的卡片数量
        voList.forEach(vo -> {
            Integer studyCard;
            Integer reviewCard;

            //获取今天新建的额卡片数量
            Integer createCardCount = cardMapper
                    .getCreateCardCount(vo.getId(), loginFilter.getUser().getId(), dateString);

            vo.setCreateCardCount(createCardCount);

            if (flag) {
                studyCard = 0;
                vo.setStudyCard(studyCard);
            } else {

                //获取获取今天需要学习的卡片数量
                QueryWrapper<Card> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("type_id", vo.getId())
                        .eq("user_id", loginFilter.getUser().getId())
                        .eq("next_study_time", dateString);
                studyCard = cardMapper.selectCount(queryWrapper1);
                vo.setStudyCard(studyCard);
            }

            if (flag) {
                reviewCard = 0;
                vo.setReviewCard(reviewCard);
            } else {
                //获取今天需要复习的卡片数量
                String reviewDay = new DateTime().minusDays(4).toString("yyyy-MM-dd");
                QueryWrapper<Card> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("type_id", vo.getId())
                        .eq("user_id", loginFilter.getUser().getId())
                        .eq("next_study_time", reviewDay);
                reviewCard = cardMapper.selectCount(queryWrapper2);
                vo.setReviewCard(reviewCard);
            }

            vo.setTotalStudyCard(studyCard + reviewCard);
//            vo.setTotalStudyCard(vo.getStudyCard() + vo.getCreateCardCount()+vo.getReviewCard());

        });

        return voList;
    }

    /**
     * 学习功能
     *
     * @param id
     * @param cardId
     * @param answerTime
     * @param familiarity
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Card startStudy(String id, String cardId,
                           Integer answerTime, String familiarity)
            throws JsonProcessingException {

        //定义一个是否从redis中移除的标志
        boolean isRemoveFromRedis = false;

        // 封装对应的key
        String redisKey = "study::" + loginFilter.getUser().getId() + "::" + id;

        String date = new DateTime().toString("yyyy-MM-dd");
        String reviewDate = new DateTime().minusDays(4).toString("yyyy-MM-dd");
        // 先从redis中获取相应的卡片集
        Boolean flag = redisTemplate.hasKey(redisKey);
        if (flag != null && !flag) {
            //如果没有从数据库中取出数据 存入redis
            QueryWrapper<Card> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", loginFilter.getUser().getId())
                    .eq("type_id", id)
                    .eq("next_study_time", date);
            List<Card> cardList = this.baseMapper.selectList(queryWrapper);

            QueryWrapper<Card> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", loginFilter.getUser().getId())
                    .eq("type_id", id)
                    .eq("pre_study_time", reviewDate);
            List<Card> previewCardList = this.baseMapper.selectList(wrapper);
            cardList.addAll(previewCardList);

            if (CollectionUtils.isEmpty(cardList)){
                return null;
            }
            String cards = objectMapper.writeValueAsString(cardList);

            //将数据存入redis中
            redisTemplate.opsForValue().set(redisKey, cards, 1, TimeUnit.HOURS);
        }

        //从redis中取出值
        String cardInRedis = redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(cardInRedis)) {
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }

        List<Card> cardList = objectMapper.readValue(cardInRedis,
                new TypeReference<List<Card>>() {
                });

        if (StringUtils.isBlank(cardId)) {
            //第一次点击卡片集进行学习 直接返回一张卡片即可
            int index = RandomUtils.nextInt(0, cardList.size());
            return cardList.get(index);
        }

        int index = 0;
        //如果不是第一次点击卡片集进行学习 那么就要进行算法判断和进行数据收集
        for (int i = 0; i < cardList.size(); i++) {
            if (cardId.equals(cardList.get(i).getId())) {
                index = i;
            }
        }

        switch (familiarity) {
            case "2":
                //熟悉的情况
                Card card = cardList.get(index);

                if (card.getFamiliarity() == 5 || card.getFamiliarity() == 2
                        || card.getFamiliarity() == 1 || card.flag) {
                    //直接移除元素并且进行算法校验判断下次复习时间
                    card.setPreStudyTime(new Date());

                    //将此次学习存入分析表
                    Calculate calculate = new Calculate();
                    calculate.setCardId(cardId);
                    calculate.setTypeId(id);
                    calculate.setMemoryTime(card.getMemoryTime() == null ? answerTime : card.getMemoryTime() + answerTime);
                    calculate.setStudyTime(new Date());
                    calculate.setUserId(loginFilter.getUser().getId());
                    calculate.setFamiliarity(card.getFamiliarity() == 5 ? Integer.parseInt(familiarity) : card.getFamiliarity());

                    // 进行算法校验
                    String preDate = restTemplate.getForObject("http://localhost:5000/model?proficiency=" + calculate.getFamiliarity() + "&now=" + date, String.class);
                    card.setNextStudyTime(new DateTime(preDate.substring(1,preDate.length()-1)).toDate());

                    this.baseMapper.updateById(card);
                    calculateMapper.insert(calculate);
                    //将redis中的list集合移除该元素
                    cardList.remove(index);
                    isRemoveFromRedis = true;
                    if (cardList.isEmpty()){
                        redisTemplate.delete(redisKey);
                    }else {
                        String cardListForString = objectMapper.writeValueAsString(cardList);
                        redisTemplate.opsForValue().set(redisKey, cardListForString,1,TimeUnit.HOURS);
                    }
                } else {
                    //如果原本是陌生的情况 仍然需要继续考察学习时间
                    //设置卡片的时间
                    card.setFlag(true);
                    card.setMemoryTime(card.getMemoryTime() == null ? answerTime : card.getMemoryTime() + answerTime);
                    String cardListForString = objectMapper.writeValueAsString(cardList);
                    redisTemplate.opsForValue().set(redisKey, cardListForString,1,TimeUnit.HOURS);
                }
                break;
            case "1":
                //犹豫的情况 不移除卡片
                Card card1 = cardList.get(index);
                //设置卡片的熟练度和时间
                card1.setFamiliarity(1);
                card1.setMemoryTime(card1.getMemoryTime() == null ? answerTime : card1.getMemoryTime() + answerTime);
                String cardListForString = objectMapper.writeValueAsString(cardList);
                redisTemplate.opsForValue().set(redisKey, cardListForString,1,TimeUnit.HOURS);
                break;
            case "0":
                //陌生的情况下 再添加一条该卡片进行重复考察
                Card card2 = cardList.get(index);
                //设置卡片的熟练度和时间
                card2.setFamiliarity(0);
                card2.setMemoryTime(card2.getMemoryTime() == null ? answerTime : card2.getMemoryTime() + answerTime);
                String cardListForString2 = objectMapper.writeValueAsString(cardList);
                redisTemplate.opsForValue().set(redisKey, cardListForString2,1,TimeUnit.HOURS);
                break;
            default:
                throw new YiyiException(35066, "学习方式错误");
        }

        //定义需要返回的下标
        int indexAfter = 0;
        if (cardList.size() == 0) {
            return null;
        }
        indexAfter = RandomUtils.nextInt(0, cardList.size());
        return cardList.get(indexAfter);

    }

    /**
     * 统计用户的学习天数
     */
    @Override
    public long countUserStudyDay() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", loginFilter.getUser().getId());
        User user = userMapper.selectOne(queryWrapper);
        Date date = user.getGmtCreate();
        Date nowDay = new Date();
        long l = nowDay.getTime() - date.getTime();
        return l / (1000 * 60 * 60 * 24);
    }

}
