package org.example.friend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.friend.common.BaseResponse;
import org.example.friend.common.ErrorCode;
import org.example.friend.common.ResultUtils;
import org.example.friend.exception.BusinessException;
import org.example.friend.model.domain.Team;
import org.example.friend.model.domain.User;
import org.example.friend.model.request.*;
import org.example.friend.model.vo.TeamUserVo;
import org.example.friend.service.TeamService;
import org.example.friend.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 团队接口
 *
 * @author Qi
 * @since 2025-06-26
 */
@RestController
@RequestMapping("/team")
@Slf4j
@Tag(name = "团队管理", description = "团队创建、查询、更新、删除等接口")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    /**
     * 创建团队
     */
    @PostMapping("/add")
    @Operation(summary = "创建团队", description = "用户创建新团队")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long teamId = teamService.addTeam(teamAddRequest, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除团队(管理员）
     */
    @PostMapping("/delete")
    @Operation(summary = "删除团队", description = "删除指定ID的团队")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(teamUpdateRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 更新团队
     */
    @PutMapping("/update")
    @Operation(summary = "更新团队", description = "更新团队信息")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取团队详情
     */
    @GetMapping("/get")
    @Operation(summary = "获取团队详情", description = "根据ID获取团队详细信息")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getTeamById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "团队不存在");
        }

        return ResultUtils.success(team);
    }

    /**
     * 获取团队列表
     *
     * @return 团队列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取团队列表", description = "获取所有团队列表")
    public BaseResponse<List<TeamUserVo>> listTeams(TeamQueryRequest teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVo> teamList = teamService.listTeams(teamQueryRequest, isAdmin);
        return ResultUtils.success(teamList);
    }

    /**
     * 获取团队列表（分页）
     */
    @GetMapping("/list/page")
    @Operation(summary = "获取团队分页列表", description = "分页查询团队列表")
    public BaseResponse<IPage<Team>> listTeamByPage(TeamQueryRequest teamQueryRequest) {
        int pageNum = teamQueryRequest.getPageNum() <= 0 ? 1 : teamQueryRequest.getPageNum();
        int pageSize = teamQueryRequest.getPageSize() <= 0 ? 10 : teamQueryRequest.getPageSize();
        Page<Team> page = new Page<>(pageNum, pageSize);
        IPage<Team> teamPage = teamService.pageTeam(page, teamQueryRequest);
        return ResultUtils.success(teamPage);
    }

    /**
     * 获取我的团队列表
     */
    @GetMapping("/list/myTeam")
    @Operation(summary = "获取我的团队", description = "获取当前用户创建的团队列表")
    public BaseResponse<List<Team>> listMyTeams(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Team> teamList = teamService.listMyTeams(loginUser.getId());
        return ResultUtils.success(teamList);
    }

    /**
     * 加入团队
     */
    @PostMapping("/join")
    @Operation(summary = "加入团队", description = "用户加入指定团队")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 退出团队
     */
    @PostMapping("/exit")
    @Operation(summary = "退出团队", description = "用户退出指定团队")
    public BaseResponse<Boolean> exitTeam(@RequestBody TeamExitRequest teamExitRequest, HttpServletRequest request) {
        if (teamExitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.exitTeam(teamExitRequest, loginUser);
        return ResultUtils.success(result);
    }
    /**
     * 解散队伍
     */
    @PostMapping("/dismiss")
    @Operation(summary = "解散队伍", description = "用户解散指定队伍")
    public BaseResponse<Boolean> dismissTeam(@RequestBody TeamExitRequest teamExitRequest, HttpServletRequest request) {
        if (teamExitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.dismissTeam(teamExitRequest, loginUser);
        return ResultUtils.success(result);
    }

}