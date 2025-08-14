package org.example.friend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.friend.model.domain.UserTeam;
import org.example.friend.service.UserTeamService;
import org.example.friend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Qi
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2025-08-11 15:05:50
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




