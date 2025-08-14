package org.example.friend.model.vo;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 队伍用户信息封装类
 */
@Data
public class TeamUserVo  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 队伍ID
     */
    private Long id;

    /**
     * 创建人Id
     */
    private Long userId;

    /**
     * 队伍名称
     */
    private String teamName;


    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 0-公开，1-私密，2-加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 已加入的用户列表
     */
    List<UserVo> userList;

    /**
     * 创建用户信息
     */
    UserVo createUser;



}
