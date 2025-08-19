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
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    /**
     * 用户脱敏
     */
    User getSafetyUser(User user);
    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     */
    Integer updateUser(User user, User loginUser);

    /**
     * 管理员更新用户信息
     */
    Integer updateUserByAdmin(User user, User loginUser);

    /**
     * 获取当前登录用户
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     */
    boolean isAdmin(User loginUser);

    /**
     * 根据标签搜索用户
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 匹配用户
     */
    List<User> matchUsers(long pageSize, User loginUser);
}
