package com.han.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.han.common.BaseResponse;
import com.han.common.ErrorCode;
import com.han.exception.BusinessException;
import com.han.model.domain.User;
import com.han.model.request.UserLoginRequest;
import com.han.model.request.UserRegisterRequest;
import com.han.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.BatchUpdateException;
import java.util.Collections;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.stream.Collectors;

import static com.han.common.ErrorCode.NO_AUTH;
import static com.han.common.ErrorCode.PARAMS_ERROR;
import static com.han.constant.UserConstants.ADMIN_ROLE;
import static com.han.constant.UserConstants.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author han
 */
@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        //controller层可以再做一层校验，最好是与业务逻辑无关的校验
        if(userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkedPassword = userRegisterRequest.getCheckedPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if(StringUtils.isAllEmpty(account, password, checkedPassword, planetCode)) {
            throw new BusinessException(PARAMS_ERROR);
        }
        long result = userService.userRegister(account, password, checkedPassword, planetCode);
        return BaseResponse.ok(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        //controller层可以再做一层校验，最好是与业务逻辑无关的校验
        if(userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        if(StringUtils.isAllEmpty(account, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(account, password, request);
        return BaseResponse.ok(user);
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if(request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        int result = userService.userLogout(request);
        return BaseResponse.ok(result);
    }

    /**
     * 前端获取当前登录用户
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        //从session中获取用户信息
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if(user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //由于存储的用户信息可能已经过时，最好重新从数据库中查询，返回最新的用户信息
        Long id = user.getId();
        User currentUser = userService.getById(id);
        //todo 校验用户是否合法
        User safetyUser = userService.getSafetyUser(currentUser);
        return BaseResponse.ok(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String userName, HttpServletRequest request) {
        //只有管理员可以查询
        if(!userService.isAdmin(request)) {
            throw new BusinessException(NO_AUTH);
        }
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.like(userName != null, User::getUsername, userName);

        List<User> list = userService.list(lqw);
        List<User> result = list.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return BaseResponse.ok(result);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user, HttpServletRequest request) {
        if (user == null) {
            throw new BusinessException(PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = userService.updateUser(user, loginUser);
        return BaseResponse.ok(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        //鉴权，只有管理员可以删除
        if(!userService.isAdmin(request)) {
            throw new BusinessException(NO_AUTH);
        }

        if(id <= 0) {
            throw new BusinessException(PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return BaseResponse.ok(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return BaseResponse.ok(userList);
    }

}
