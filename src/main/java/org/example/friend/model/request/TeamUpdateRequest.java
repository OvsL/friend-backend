package org.example.friend.model.request;

import lombok.Data;

import java.util.Date;

@Data
public class TeamUpdateRequest {

    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String teamName;
    /**
     * 队伍描述
     */
    private String description;
    /**
     * 队伍最大人数
     */
    private Integer maxNum;
    /**
     * 队伍状态
     */
    private Integer status;
    /**
     * 队伍密码
     */
    private String password;
    /**
     * 队伍过期时间
     */
    private Date expireTime;
}
