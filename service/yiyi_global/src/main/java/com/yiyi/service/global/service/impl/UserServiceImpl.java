package com.yiyi.service.global.service.impl;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yiyi.commom.base.exception.YiyiException;
import com.yiyi.commom.base.result.ResultCodeEnum;
import com.yiyi.commom.utils.CodecUtils;
import com.yiyi.commom.utils.RandomUtils;
import com.yiyi.service.global.entity.User;
import com.yiyi.service.global.entity.request.UserRegisterVo;
import com.yiyi.service.global.entity.respon.UserInfo;
import com.yiyi.service.global.filter.LoginFilter;
import com.yiyi.service.global.mapper.UserMapper;
import com.yiyi.service.global.oss.FileOssUtils;
import com.yiyi.service.global.propertiesUtil.JwtUtils;
import com.yiyi.service.global.propertiesUtil.SmsProperty;
import com.yiyi.service.global.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
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
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SmsProperty smsProperty;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LoginFilter loginFilter;

    /**
     * 发送注册短信
     *
     * @param mobile
     * @throws Exception
     */
    @Override
    public void getSmsCode(String mobile) throws Exception {
        DefaultProfile profile = DefaultProfile.getProfile(
                smsProperty.getRegionId(),
                smsProperty.getAccessKeyId(),
                smsProperty.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", smsProperty.getRegionId());
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("SignName", smsProperty.getSignName());
        request.putQueryParameter("TemplateCode", smsProperty.getTemplateCode());

        HashMap<String, String> map = new HashMap<>();
        String code = RandomUtils.getFourBitRandom();
        map.put("code", code);
        String codeJSON = objectMapper.writeValueAsString(map);
        request.putQueryParameter("TemplateParam", codeJSON);

        CommonResponse response = client.getCommonResponse(request);
        Map<String, Object> dataMap = objectMapper.readValue(response.getData(),
                new TypeReference<Map<String, Object>>() {
                });

        redisTemplate.opsForValue().set("sms::" + mobile, code, 5, TimeUnit.MINUTES);

        if (!"OK".equals(dataMap.get("Code"))) {
            log.error("消息发送失败{}", dataMap.get("Message"));
            throw new YiyiException(ResultCodeEnum.SMS_SEND_ERROR);
        }
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regimster(UserRegisterVo user) {

        if (StringUtils.isBlank(user.getCode())) {
            throw new YiyiException(28014, "验证码为空");
        }

        if (StringUtils.isBlank(user.getNickname())) {
            throw new YiyiException(28010, "用户昵称为空");
        }

        if (StringUtils.isBlank(user.getPassword())) {
            throw new YiyiException(28011, "用户密码为空");
        }
        User otherUser = this.baseMapper.selectOne(new QueryWrapper<User>()
                .eq("mobile", user.getMobile()));

        if (otherUser != null) {
            throw new YiyiException(28012, "该用户已经被注册");
        }

        String registerCode = redisTemplate.opsForValue().get("sms::" + user.getMobile());

        if (registerCode == null && !user.getCode().equals(registerCode)) {
            throw new YiyiException(28013, "您的验证码已经过期");
        }

        String salt = CodecUtils.generateSalt();

        String userPasswordPro = CodecUtils.md5Hex(user.getPassword(), salt);

        user.setAvatar("https://yiyi-file.oss-cn-shenzhen.aliyuncs.com/user_avatar/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg");

        user.setSalt(salt);
        user.setPassword(userPasswordPro);
        user.setId(null);

        this.baseMapper.insert(user);

        redisTemplate.delete("sms::" + user.getMobile());

    }

    /**
     * 用户登录
     *
     * @param mobile
     * @param password
     * @return
     */

    @Override
    public String login(String mobile, String password, HttpServletRequest request) {

        if (StringUtils.isBlank(mobile) || StringUtils.isBlank(password)) {
            throw new YiyiException(28200, "用户名或密码为空");
        }

        User user = this.baseMapper.selectOne(new QueryWrapper<User>().eq("mobile", mobile));

        if (user == null) {
            throw new YiyiException(ResultCodeEnum.UNKNOWN_REASON);
        }

        String salt = user.getSalt();

        if (salt != null && user.getPassword().equals(CodecUtils.md5Hex(password, salt))) {
            UserInfo userInfo = new UserInfo();
            BeanUtils.copyProperties(user, userInfo);

            String token = JwtUtils.getJwtToken(userInfo, 10);
            String prefix = "userLogin::";

            String oldToken = request.getHeader("token");

            //先删除redis中的key
            if (StringUtils.isNotBlank(oldToken)){
                Boolean hasKey = redisTemplate.hasKey(prefix + oldToken);
                if (hasKey!=null && hasKey) {
                    redisTemplate.delete(prefix + oldToken);
                }
            }

            //将token存入redis设置超时时间
            redisTemplate.opsForValue().set(prefix + token, "token", 10, TimeUnit.DAYS);

            return token;
        }

        return null;

    }

    // 需要修改session方式
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadAvatar(MultipartFile file, HttpServletRequest request) {
        try {


            String path = FileOssUtils.uploadFile(file, "avatar");

            UserInfo userInfo = loginFilter.getUser();

            User user = new User();
            user.setMobile(userInfo.getMobile());
            user.setAvatar(path);

            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mobile", user.getMobile());

            this.baseMapper.update(user, queryWrapper);
            return path;
        } catch (IOException e) {
            throw new YiyiException(ResultCodeEnum.FILE_UPLOAD_ERROR);
        }
    }

    /**
     * PDF文件导出
     *
     * @param id
     */
    @Override
    public void fileToPDF(String id) {

    }

    /**
     * 退出登录
     */
    @Override
    public void logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        String prefix = "userLogin::";
        redisTemplate.delete(prefix + token);
    }
}
