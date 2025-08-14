package org.example.friend.common;

import lombok.Data;

@Data
public class PageRquest{


    /**
     * 分页当前页
     */
    private int pageNum = 1;
    /**
     * 分页大小
     */
    private int pageSize = 10;

}
