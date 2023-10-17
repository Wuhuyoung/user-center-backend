package com.han.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 * @author han
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param account 账号
     * @param password 密码
     * @param checkedPassword 校验密码
     * @param planetCode 星球编号
     * @return 新用户 id
     */
    long userRegister(String account, String password, String checkedPassword, String planetCode);

    /**
     * 用户登录
     * @param account 账号
     * @param password 密码
     * @param request http请求
     * @return 脱敏后的用户信息
     */
    User userLogin(String account, String password, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originalUser
     * @return
     */
    User getSafetyUser(User originalUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    List<User> searchUserByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    boolean updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 分页查询推荐用户
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    Page<User> recommendUsers(HttpServletRequest request, int pageSize, int pageNum);
}
