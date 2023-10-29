package com.han.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 6007543533295472677L;

    /**
     * 当前页码
     */
    protected int currentPage = 1;
    /**
     * 页面大小
     */
    protected int pageSize = 10;
}
