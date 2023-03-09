package com.han.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 1813540471175202712L;
    private String account;
    private String password;
    private String checkedPassword;
    private String planetCode;
}
