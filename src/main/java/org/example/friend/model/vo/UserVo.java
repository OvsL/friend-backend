package org.example.friend.model.vo;
import com.baomidou.mybatisplus.annotation.TableLogic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

/**
 * 用户信息封装类
 */
@Data
public class UserVo {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String userName;

    /**
     * 账号
     */
    @Schema(description = "用户账号")
    private String userAccount;

    /**
     * 头像
     */
    @Schema(description = "头像URL")
    private String avatarUrl;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private Integer gender;


    /**
     * 电话
     */
    @Schema(description = "用户电话")
    private String userPhone;

    /**
     * 邮件
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 状态信息
     */
    @Schema(description = "用户状态")
    private Integer userStatus;

    /**
     * 是否删除
     */
    @TableLogic
    @Schema(description = "是否删除")
    private Integer isDelete;

    /**
     * 创建时间
     */
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 角色权限 0-普通用户 1-管理员
     */
    @Schema(description = "用户角色 0-普通用户 1-管理员")
    private Integer userRole;

    /**
     * 标签列表 json
     */
    @Schema(description = "用户标签列表")
    private String tags;
}
