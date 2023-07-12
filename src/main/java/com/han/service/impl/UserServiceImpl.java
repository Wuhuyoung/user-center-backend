package com.han.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.han.exception.BusinessException;
import com.han.mapper.UserMapper;
import com.han.model.domain.User;
import com.han.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.han.common.ErrorCode.PARAMS_ERROR;
import static com.han.common.ErrorCode.SYSTEM_ERROR;
import static com.han.constant.UserConstants.SALT;
import static com.han.constant.UserConstants.USER_LOGIN_STATE;


/**
 * 用户服务实现类
 * @author han
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;


    @Override
    public long userRegister(String account, String password, String checkedPassword, String planetCode) {
        //1.校验
        if(StringUtils.isAnyEmpty(account, password, checkedPassword, planetCode)) {
            throw new BusinessException(PARAMS_ERROR, "参数为空");
        }
        if(account.length() < 4) {
            throw new BusinessException(PARAMS_ERROR, "账号长度不足4位");
        }
        if(password.length() < 8 || checkedPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "密码长度不足8位");
        }
        if(planetCode.length() > 5) {
            throw new BusinessException(PARAMS_ERROR, "星球编号超过长度限制");
        }
        //账号不包含特殊字符(只能由中文、英文、数字包括下划线组成)
        String validPattern = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";
        if(!account.matches(validPattern)) {
            throw new BusinessException(PARAMS_ERROR, "账号不能包含特殊字符，只能由中文、英文、数字、下划线组成");
        }
        //密码和校验密码相同
        if(!password.equals(checkedPassword)) {
            throw new BusinessException(PARAMS_ERROR, "密码和校验密码不相同");
        }
        //账号不能重复
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        long count = userMapper.selectCount(lqw);
        if(count > 0) {
            throw new BusinessException(PARAMS_ERROR, "该账号已存在");
        }
        //星球编号不能重复
        lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPlanetCode, planetCode);
        count = userMapper.selectCount(lqw);
        if(count > 0) {
            throw new BusinessException(PARAMS_ERROR, "该星球编号已存在");
        }
        //2.对密码进行加密(md5加密)
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        //3.向数据库添加用户
        User user = new User();
        user.setUserAccount(account);
        user.setPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if(!saveResult) {
            throw new BusinessException(SYSTEM_ERROR, "注册失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String account, String password, HttpServletRequest request) {
        //1.校验
        if(StringUtils.isAnyEmpty(account, password)) {
            throw new BusinessException(PARAMS_ERROR, "参数为空");
        }
        if(account.length() < 4) {
            throw new BusinessException(PARAMS_ERROR, "账号长度不足4位");
        }
        if(password.length() < 8) {
            throw new BusinessException(PARAMS_ERROR, "密码长度不足8位");
        }
        //账号不包含特殊字符(只能由中文、英文、数字包括下划线组成)
        String validPattern = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+$";
        if(!account.matches(validPattern)) {
            throw new BusinessException(PARAMS_ERROR, "账号不能包含特殊字符，只能由中文、英文、数字、下划线组成");
        }

        //2.校验密码
        //2.1 对密码进行加密(md5加密)
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        //2.2 查询用户
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUserAccount, account);
        lqw.eq(User::getPassword, encryptPassword);
        User user = userMapper.selectOne(lqw);
        //用户不存在或密码错误(不要将具体信息返回给用户,只要告诉账号和密码不匹配即可)
        if(user == null) {
            log.info("user login failed, userName cannot match password");
            throw new BusinessException(PARAMS_ERROR, "账号和密码不匹配");
        }

        //3.用户信息脱敏
        User safetyUser = getSafetyUser(user);

        //4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        //5.返回
        return safetyUser;
    }


    @Override
    public User getSafetyUser(User originalUser) {
        if(originalUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originalUser.getId());
        safetyUser.setUsername(originalUser.getUsername());
        safetyUser.setUserAccount(originalUser.getUserAccount());
        safetyUser.setAvatarUrl(originalUser.getAvatarUrl());
        safetyUser.setGender(originalUser.getGender());
        safetyUser.setPhone(originalUser.getPhone());
        safetyUser.setEmail(originalUser.getEmail());
        safetyUser.setStatus(originalUser.getStatus());
        safetyUser.setCreateTime(originalUser.getCreateTime());
        safetyUser.setUserRole(originalUser.getUserRole());
        safetyUser.setPlanetCode(originalUser.getPlanetCode());
        safetyUser.setTags(originalUser.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户（内存过滤）
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(PARAMS_ERROR);
        }

        // 实现方式二：全部查询，然后用Java代码在内存中实现标签筛选（标签较多时更快）
        // 查询所有用户
        List<User> users = userMapper.selectList(null);
        Gson gson = new Gson();
        // 根据标签筛选
        List<User> userList = users.stream().filter(user -> {
            String tagNameStr = user.getTags();  // ["Java", "Python"]
            if (StringUtils.isBlank(tagNameStr)) {
                return false;
            }
            // json反序列化为set集合
            Set<String> tagList = gson.fromJson(tagNameStr, new TypeToken<Set<String>>(){}.getType());
            // 集合遍历之前需要判空
            tagList = Optional.ofNullable(tagList).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tagList.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        return userList;
    }

    /**
     * 使用SQL模糊查询 根据标签搜索用户
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(PARAMS_ERROR);
        }
        // 实现方式一：使用SQL语句模糊查询标签（标签较少时更快，标签较多时多次使用like查询效率降低）
        // where tags like '%Java%' and tags like '%C++%'
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        for (String tagName : tagNameList) {
            lqw.like(User::getTags, tagName);
        }
        List<User> users = userMapper.selectList(lqw);
        return users.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




