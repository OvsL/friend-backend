package org.example.friend.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 用户队伍关系表
 * @TableName user_team
 */
@TableName(value ="user_team")
@Data
public class UserTeam {
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
     * 队伍ID
     */
    private Long teamId;

    /**
     * 加入时间
     */
    private Date joinTime;

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