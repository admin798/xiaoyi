package com.yiyi.service.global.service;

import com.yiyi.service.global.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiyi.service.global.entity.request.UserRegisterVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jack.F
 * @since 2020-09-12
 */
public interface UserService extends IService<User> {

    void getSmsCode(String mobile) throws Exception;

    void regimster(UserRegisterVo user);

    String login(String mobile, String password, HttpServletRequest request);

    String uploadAvatar(MultipartFile file, HttpServletRequest request);

    void fileToPDF(String id);

    void logout(HttpServletRequest request);
}
