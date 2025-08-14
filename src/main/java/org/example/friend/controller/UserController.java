package org.example.friend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.friend.common.BaseResponse;
import org.example.friend.common.ErrorCode;
import org.example.friend.common.ResultUtils;
import org.example.friend.constant.UserConstant;
import org.example.friend.exception.BusinessException;
import org.example.friend.model.domain.User;
import org.example.friend.model.request.UserLoginRequset;
import org.example.friend.model.request.UserRegisterRequset;
import org.example.friend.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.friend.constant.UserConstant.ADMIN_ROLE;
import static org.example.friend.constant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author Qi
 * @since 2025-06-26
 */
@RestController
@RequestMapping("/user")
@Slf4j
@Data
@Tag(name = "用户管理", description = "用户注册、登录、查询等接口")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    public UserController() {
    }

    //注册接口
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册接口，需要提供账号、密码和确认密码")
    @ApiResponse(responseCode = "200", description = "注册成功", 
                content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<Long> register(@RequestBody UserRegisterRequset userRegisterRequset) {
        if (userRegisterRequset == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequset.getUserAccount();
        String userPassword = userRegisterRequset.getUserPassword();
        String checkPassword = userRegisterRequset.getCheckPassword();
        if (userAccount == null || userPassword == null || checkPassword == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword);
        //调用服务层
//        return new BaseResponse<>(0, l, "注册成功");
        return ResultUtils.success(l);
    }

    //登录接口
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，需要提供账号和密码")
    @ApiResponse(responseCode = "200", description = "登录成功", 
                content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    public BaseResponse<User> login(@RequestBody UserLoginRequset userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (userAccount == null || userPassword == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        //调用服务层
//        return new BaseResponse<>(0, user, "登录成功");
        return ResultUtils.success(user);
    }

    //注销接口
    @PostMapping("/logout")
    @Operation(summary = "用户注销")
    public BaseResponse<Integer> logout(HttpServletRequest request) {
        if (request == null) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int i = userService.userLogout(request);
        //调用服务层
        return ResultUtils.success(i);

    }

    @GetMapping("/current")
    @Operation(summary = "获取当前用户")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    //查询用户接口
    @GetMapping("/search")
    @Operation(summary = "查询用户")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
//            return new ArrayList<>();
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> collect = userList.stream().peek(user -> user.setUserPassword(null)).collect(Collectors.toList());
        return ResultUtils.success(collect);

    }

    //删除用户接口
    @GetMapping("/delete")
    @Operation(summary = "删除用户")
    public BaseResponse<Boolean> deleteUsers(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (id <= 0) {
//            return null;
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    //推荐用户接口
    @GetMapping("/recommend")
    public BaseResponse<List<User>> recommendUsers(@RequestParam(defaultValue = "1") long pageNum, @RequestParam(defaultValue = "10") long pageSize, HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ValueOperations<String, Object> objectValueOperations = redisTemplate.opsForValue();

        //缓存存在
        String redisKey = String.format("%s:recommend:%s", UserConstant.REDISSON_NAME,loginUser.getId());
        Page<User> userPage = (Page<User>) objectValueOperations.get(redisKey);
        if (userPage != null) {
            List<User> collect = userPage.getRecords().stream().peek(user -> user.setUserPassword(null)).collect(Collectors.toList());
            return ResultUtils.success(collect);
        }

        //缓存不存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        List<User> userList = userService.list(queryWrapper);
        IPage<User> page = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<User> collect = page.getRecords().stream().peek(user -> user.setUserPassword(null)).collect(Collectors.toList());

        //缓存数据
        try {
            objectValueOperations.set(redisKey, page, 1, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set error", e);
        }

        return ResultUtils.success(collect);
    }

    //根据标签搜索用户
    @GetMapping("/search/tags")
    @Operation(summary = "根据标签搜索用户")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    //更新用户接口
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        //校验参数
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //调用服务层
        User loginUser = userService.getLoginUser(request);
        Integer result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }



    private boolean isAdmin(HttpServletRequest request) {
        //仅管理员可以查询所有用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }


    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof UserController)) return false;
        final UserController other = (UserController) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$userService = this.getUserService();
        final Object other$userService = other.getUserService();
        if (this$userService == null ? other$userService != null : !this$userService.equals(other$userService))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof UserController;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $userService = this.getUserService();
        result = result * PRIME + ($userService == null ? 43 : $userService.hashCode());
        return result;
    }

    public String toString() {
        return "UserController(userService=" + this.getUserService() + ")";
    }


}
