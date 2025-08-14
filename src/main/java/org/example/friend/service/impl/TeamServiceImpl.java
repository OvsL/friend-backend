package org.example.friend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.friend.common.ErrorCode;
import org.example.friend.constant.TeamStatusEnum;
import org.example.friend.exception.BusinessException;
import org.example.friend.model.domain.Team;
import org.example.friend.model.domain.User;
import org.example.friend.model.domain.UserTeam;
import org.example.friend.model.request.TeamAddRequest;
import org.example.friend.model.request.TeamJoinRequest;
import org.example.friend.model.request.TeamQueryRequest;
import org.example.friend.model.request.TeamUpdateRequest;
import org.example.friend.model.vo.TeamUserVo;
import org.example.friend.model.vo.UserVo;
import org.example.friend.service.TeamService;
import org.example.friend.mapper.TeamMapper;
import org.example.friend.service.UserService;
import org.example.friend.service.UserTeamService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author Qi
* @description 针对表【team】的数据库操作Service实现
* @createDate 2025-08-11 15:04:25
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    /**
     * 根据id查询队伍
     */
    @Override
    public Team getTeamById(Long id) {
        return getById(id);
    }

    /**
     * 分页查询队伍
     */
    @Override
    public IPage<Team> pageTeam(Page<Team> page, TeamQueryRequest teamQueryRequest) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<Team>();
        if (teamQueryRequest != null) {
            queryWrapper.like("teamName", teamQueryRequest.getTeamName());
        }
        return page(page, queryWrapper);
    }

    /**
     * 新增队伍
     */
    @Override
    //事务
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(TeamAddRequest teamAddRequest, User loginUser) {
        // 校验队伍信息
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍信息不能为空");
        }
        // 校验用户信息
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户未登录");
        }

        String teamName = teamAddRequest.getTeamName();
        // 校验队伍名称
        if (teamName == null || teamName.isEmpty() || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不能为空或超过20个字符");
        }


        String description = teamAddRequest.getDescription();
        // 校验队伍描述
        if (description.length() > 200) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不能超过200个字符");
        }


        Integer maxNum = teamAddRequest.getMaxNum();
        // 校验队伍人数
        if (maxNum <= 0 || maxNum > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不能小于0或大于10");
        }


        Integer status = teamAddRequest.getStatus();
        // 校验队伍状态
        if (status != TeamStatusEnum.PUBLIC.getValue() && status != TeamStatusEnum.PRIVATE.getValue() && status != TeamStatusEnum.SECRET.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态错误");
        }

        String password = teamAddRequest.getPassword();
        // 校验队伍密码
        if (status == TeamStatusEnum.SECRET.getValue()) {
            if (password == null || password.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须设置密码");
            }

        }

        Date expireTime = teamAddRequest.getExpireTime();
        // 校验队伍过期时间
        if (expireTime == null || expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍过期时间不能为空或者过期时间不能早于当前时间");
        }



        //校验用户队伍数量
        // todo bug
        long teamCount = count(new QueryWrapper<Team>().eq("userId", loginUser.getId()));
        if (teamCount >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }

        Long userId = loginUser.getId();
        // 校验用户是否存在
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }


        // 新增队伍到队伍表
        Team team = new Team();
        team.setTeamName(teamName);
        team.setDescription(description);
        team.setMaxNum(maxNum);
        team.setStatus(status);
        team.setPassword(password);
        team.setExpireTime(expireTime);
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        team.setUserId(userId);
        boolean saved = save(team);

        Long teamId = team.getId();
        // 校验队伍是否创建成功
        if (!saved || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍创建失败");
        }

        //插入队伍用户关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        userTeam.setExpireTime(expireTime);
        boolean saved1 = userTeamService.save(userTeam);
        if (!saved1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍创建失败");
        }
        return teamId;
    }

    /**
     * 删除队伍
     */
    @Override
    public boolean deleteTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        return removeById(loginUser.getId());
    }
    /**
     * 更新队伍
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        // 校验队伍是否存在
        Long teamId = teamUpdateRequest.getId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Team team = getTeamById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (!team.getUserId().equals(loginUser.getId()) || !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if(team.getStatus() == TeamStatusEnum.SECRET.getValue()){
            if(teamUpdateRequest.getPassword() == null || teamUpdateRequest.getPassword().isEmpty()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须设置密码");
            }
        }
        if(teamUpdateRequest.getStatus() == TeamStatusEnum.PUBLIC.getValue() || teamUpdateRequest.getStatus() == TeamStatusEnum.PRIVATE.getValue()){
            if(teamUpdateRequest.getPassword() != null && !teamUpdateRequest.getPassword().isEmpty()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "公开或私有队伍不能设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }
    /**
     * 获取我创建的队伍
     */
    private boolean updateById(TeamUpdateRequest teamUpdateRequest) {
        Team team = getById(teamUpdateRequest.getId());
        if (team == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        team.setTeamName(teamUpdateRequest.getTeamName());
        team.setDescription(teamUpdateRequest.getDescription());
        team.setMaxNum(teamUpdateRequest.getMaxNum());
        team.setStatus(teamUpdateRequest.getStatus());
        team.setPassword(teamUpdateRequest.getPassword());
        return updateById(team);
    }
    /**
     * 获取我加入的队伍
     */
    @Override
    public List<Team> listMyTeams(Long id) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<Team>();
        queryWrapper.eq("userId", id);
        return list(queryWrapper);
    }

    @Override
    public List<TeamUserVo> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamQueryRequest != null) {
            Long id = teamQueryRequest.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            // 搜索关键词（同时对队伍名称和描述搜索）
            String searchText = teamQueryRequest.getSearchText();
            if (searchText != null && !searchText.isEmpty()) {
                queryWrapper.like("teamName", searchText).or().like("description", searchText);
            }
            String teamName = teamQueryRequest.getTeamName();
            if (teamName != null && !teamName.isEmpty()) {
                queryWrapper.like("teamName", teamName);
            }
            String description = teamQueryRequest.getDescription();
            if (description != null && !description.isEmpty()) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQueryRequest.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Integer status = teamQueryRequest.getStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getTeamStatusEnumByValue(status);
            if (teamStatusEnum == null) {
                teamStatusEnum = TeamStatusEnum.PUBLIC;
            }
            if(!isAdmin && !teamStatusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", teamStatusEnum.getValue());

            Long userId = teamQueryRequest.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
        }
        //不展示过期时间不为空且已过期的队伍
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));


        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询已加入的用户
        List<TeamUserVo> teamUserVoList = new ArrayList<>();
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if(userId == null){
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVo teamUserVo = new TeamUserVo();
            BeanUtils.copyProperties(team, teamUserVo);
            // 关联查询已加入的用户列表
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user, userVo);

            teamUserVo.setCreateUser(userVo);
            teamUserVoList.add(teamUserVo);
        }
        return teamUserVoList;
    }

    /**
     * 加入队伍
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Team team = getTeamById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if(team.getStatus() == TeamStatusEnum.SECRET.getValue()){
            if(teamJoinRequest.getPassword() == null || teamJoinRequest.getPassword().isEmpty()){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须输入密码");
            }
            if(!teamJoinRequest.getPassword().equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        if(team.getStatus() == TeamStatusEnum.PRIVATE.getValue()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "私有队伍不能加入");
        }
        //不允许加入自己的队伍
        if(team.getUserId().equals(loginUser.getId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入自己的队伍");
        }
        //判断用户是否已加入队伍
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("userId", loginUser.getId());
        userTeamQueryWrapper.eq("teamId", teamId);
        UserTeam userTeam = userTeamService.getOne(userTeamQueryWrapper);
        if(userTeam != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入队伍");
        }
        //判断队伍人数是否已满
        int count = (int) userTeamService.count(userTeamQueryWrapper);
        if(count >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数已满");
        }
        //判断队伍是否过期
        if(team.getExpireTime() != null && team.getExpireTime().before(new Date())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        //加入队伍
        UserTeam newUserTeam = new UserTeam();
        newUserTeam.setUserId(loginUser.getId());
        newUserTeam.setTeamId(teamId);
        newUserTeam.setJoinTime(new Date());
        return userTeamService.save(newUserTeam);
    }

}




