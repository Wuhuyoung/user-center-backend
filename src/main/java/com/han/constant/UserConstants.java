package com.han.constant;

/**
 * 用户常量
 */
public interface UserConstants {

    /**
     * 用户登录态 键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 盐值，混淆密码
     */
    String SALT = "han";

    //----- 权限 -----
    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理权限
     */
    int ADMIN_ROLE = 1;
}
