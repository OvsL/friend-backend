package org.example.friend.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.friend.common.PageRquest;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryRequest extends PageRquest{

    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String teamName;
    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 队伍状态
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 搜索关键词（同时对队伍名称和描述搜索）
     */
    private String searchText;


}
