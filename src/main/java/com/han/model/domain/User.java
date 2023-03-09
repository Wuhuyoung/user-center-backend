package com.han.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String password;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 状态 0 正常
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
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 角色 0-普通用户 1-管理员
     */
    private Integer userRole;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                Objects.equals(username, user.username) &&
                Objects.equals(userAccount, user.userAccount) &&
                Objects.equals(gender, user.gender) &&
                Objects.equals(password, user.password) &&
                Objects.equals(phone, user.phone) &&
                Objects.equals(email, user.email) &&
                Objects.equals(planetCode, user.planetCode) &&
                Objects.equals(status, user.status) &&
                Objects.equals(isDelete, user.isDelete) &&
                Objects.equals(userRole, user.userRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, userAccount, gender, password, phone, email, planetCode, status, isDelete, userRole);
    }
}