package org.example.friend.constant;

/**
 * 用户常量
 *
 * @author Qi
 */
public interface UserConstant {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;

    /**
     * 好友推荐缓存键
     */
    String REDISSON_NAME = "friend";


}
