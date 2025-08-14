package org.example.friend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team
 */
@TableName(value ="team")
@Data
public class Team {
    /**
     * 队伍ID
     */
    @TableId(type = IdType.AUTO)
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
     * 队伍密码
     */
    private String password;

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
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}