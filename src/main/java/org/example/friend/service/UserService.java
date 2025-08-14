package org.example.friend.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.friend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Qi
* @description 针对表【user】的数据库操作Service
* @createDate 2025-06-26 11:42:04
*/
public interface UserService extends IService<User> {


    /**
     * 用户注册
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafetyUser(User user);

    int userLogout(HttpServletRequest request);


    Integer updateUser(User user, User loginUser);

    Integer updateUserByAdmin(User user, User loginUser);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    List<User> searchUsersByTags(List<String> tagNameList);

}
