package org.example.friend.model.request;

import lombok.Data;

@Data
public class TeamJoinRequest {
    /**
     * 队伍名称
     */
    private String teamName;
    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 队伍密码
     */
    private String password;

}
