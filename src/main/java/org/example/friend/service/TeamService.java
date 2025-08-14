package org.example.friend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.friend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.friend.model.domain.User;
import org.example.friend.model.request.TeamAddRequest;
import org.example.friend.model.request.TeamJoinRequest;
import org.example.friend.model.request.TeamQueryRequest;
import org.example.friend.model.request.TeamUpdateRequest;
import org.example.friend.model.vo.TeamUserVo;

import java.util.List;

/**
* @author Qi
* @description 针对表【team】的数据库操作Service
* @createDate 2025-08-11 15:04:25
*/
public interface TeamService extends IService<Team> {

    /**
     * 根据id查询队伍
     */
    Team getTeamById(Long id);
    /**
     * 分页查询队伍
     */
    IPage<Team> pageTeam(Page<Team> page, TeamQueryRequest teamQueryRequest);
    /**
     * 新增队伍
     */
    long addTeam(TeamAddRequest teamAddRequest, User loginUser);
    /**
     * 删除队伍
     */
    boolean deleteTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
    /**
     * 更新队伍
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);
    /**
     * 获取我创建的队伍
     */
    List<Team> listMyTeams(Long id);

    /**
     * 列表队伍
     */
    List<TeamUserVo> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);

    /**
     * 加入队伍
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
