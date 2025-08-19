package org.example.friend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.friend.common.ErrorCode;
import org.example.friend.exception.BusinessException;
import org.example.friend.model.domain.User;
import org.example.friend.service.UserService;
import org.example.friend.mapper.UserMapper;
import org.example.friend.util.AlgorithmUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.example.friend.constant.UserConstant.ADMIN_ROLE;
import static org.example.friend.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Qi
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-06-26 11:42:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private static final String salt = "qi";

    private static final Gson gson = new Gson();

    // 优化点2：缓存TypeToken，避免重复创建
    private static final TypeToken<Set<String>> TAG_SET_TYPE = new TypeToken<Set<String>>() {};

    /**
     * 用户注册
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
//            return -1;
        }
        if(userAccount.length() < 4) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4");
        }
        if(userPassword.length() < 8 || checkPassword.length() < 8) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8");
        }

        //账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }

        //密码和校验密码相同
        if(!userPassword.equals(checkPassword)) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不相同");
        }

        //账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0) {
//            return -1;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }

        // 2. 加密
        String encrypt = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());

        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encrypt);
        boolean saveResult = this.save(user);
        if(!saveResult) {
            return -1;
        }
        return user.getId();
    }
    /**
     * 用户登录
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            //todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
//            return null;
        }
        if(userAccount.length() < 4) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4");
        }
        if(userPassword.length() < 8) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8");
        }

        //账号不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }

        // 2. 加密
        String encrypt = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encrypt);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null) {
              log.info("user login failed, userAccount cannot match userPassword");
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }
        // 3. 返回脱敏后的用户信息
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);

        return safetyUser;
    }

    /**
     * 用户脱敏
     * @return safetyUser
     */
    public User getSafetyUser(User user) {
        if(user == null) {
            throw new RuntimeException("用户不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUserName(user.getUserName());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserPhone(user.getUserPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setTags(user.getTags());
        return safetyUser;
    }
    /**
     * 用户注销
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 更新用户信息（用户)
     */
    @Override
    public Integer updateUser(User user, User loginUser) {
        // 1. 校验参数
        if (user == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (user.getId() == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (!Objects.equals(user.getId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(loginUser.getId());
        if(oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //更新用户信息
        return userMapper.updateById(user);
    }

    /**
     * 更新用户信息（管理员）
     */
    @Override
    public Integer updateUserByAdmin(User user, User loginUser) {
        // 1. 校验权限
        if (isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        // 2. 校验参数
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if (user.getId() == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User oldUser = userMapper.selectById(user.getId());
        if(oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        if(oldUser.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 3. 更新用户信息
        return userMapper.updateById(user);
    }


    /**
     * 获取当前登录用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if ( userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 是否为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可以查询所有用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser == null || loginUser.getUserRole() != ADMIN_ROLE;
    }

    /**
     * 内存过滤查询
     * 根据标签搜索用户
     * @return List<User>
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1.查询数据库中所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2.在内存中判断是否包含要求的标签
//        return users.stream()
//                .filter(user -> {
//                    String tagsStr = user.getTags();
//                    if(StringUtils.isBlank(tagsStr)) {
//                        return false;
//                    }
//                    Set<String> tempTags = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
//                    for(String tag : tempTags) {
//                        if(!tagNameList.contains(tag)) {
//                            return false;
//                        }
//                    }
//                    return true;
//                }).map(this::getSafetyUser).collect(Collectors.toList());
        return users.stream()
                .filter(user -> {
                    String tagsStr = user.getTags();
                    if(StringUtils.isBlank(tagsStr)) {
                        return false;
                    }
                    try {
                        Set<String> tempTags = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {}.getType());
                        return tempTags != null && tagNameList.stream().anyMatch(tempTags::contains); // 改为包含任意一个标签
                    } catch (Exception e) {
                        log.error("JSON parse error", e);
                        return false;
                    }
                }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * SQL查询
     * 根据标签搜索用户
     * @return List<User>
     */
    @SuppressWarnings("unused")
    @Deprecated
    private List<User> searchUsersBySQL(List<String> tagNameList) {
        if(CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 拼接查询条件
        // SELECT * FROM user WHERE tags LIKE '%tagName%' OR tags LIKE '%tagName%' OR ...
        // 这里的%是通配符，用于匹配任意字符
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = this.list(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 匹配用户
     */
    @Override
    public List<User> matchUsers(long pageSize, User loginUser) {
        // 1. 校验参数
        if (pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        // 2. 获取当前用户的标签
        String loginUserTagsStr = loginUser.getTags();
        if (StringUtils.isBlank(loginUserTagsStr)) {
            log.info("当前用户没有标签，返回空列表");
            // 当前用户没有标签，返回空列表
            return Collections.emptyList();
        }
//        Set<String> loginUserTags = gson.fromJson(loginUserTagsStr, new TypeToken<Set<String>>() {}.getType());
        Set<String> loginUserTags = gson.fromJson(loginUserTagsStr, TAG_SET_TYPE.getType());

        // 3.查询其他用户（优化查询字段和条件）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 优化点1：只查询必要字段，减少IO传输和对象创建
        queryWrapper.ne("id", loginUser.getId())
                .select("id", "tags"); // 仅需id和tags字段
        List<User> userList = this.list(queryWrapper);


        // 4. 计算相似度并排序
        return userList.stream()
                .filter(user -> StringUtils.isNotBlank(user.getTags())) // 提前过滤无标签用户
                .map(user -> {
                    try {
                        Set<String> userTags = gson.fromJson(user.getTags(), new TypeToken<Set<String>>() {}.getType());
                        double similarity = AlgorithmUtil.calculateTagsSimilarity(
                                new ArrayList<>(loginUserTags),
                                new ArrayList<>(userTags)
                        );
                        // 优化点2：使用数组代替AbstractMap.SimpleEntry，减少对象开销
                        return new Object[]{user, similarity};
                    } catch (Exception e) {
                        log.error("解析用户标签失败", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(arr -> (double) arr[1] > 0)
                .sorted((arr1, arr2) -> Double.compare((double) arr2[1], (double) arr1[1]))
                .limit(pageSize)
                .map(arr -> this.getSafetyUser((User) arr[0]))
                .collect(Collectors.toList());
    }
}




