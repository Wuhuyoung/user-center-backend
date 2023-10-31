package com.han.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.common.ErrorCode;
import com.han.exception.BusinessException;
import com.han.mapper.UserTeamMapper;
import com.han.model.domain.Team;
import com.han.model.domain.User;
import com.han.model.domain.UserTeam;
import com.han.model.enums.TeamStatusEnums;
import com.han.service.TeamService;
import com.han.mapper.TeamMapper;
import com.han.service.UserTeamService;
import netscape.security.UserTarget;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

/**
* @author 86183
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-10-29 22:01:11
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{
    @Resource
    private UserTeamService userTeamService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1.请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 3.校验信息
        //  1. 队伍人数 >1 且 <=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求");
        }
        //  2. 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不符合要求");
        }
        //  3. 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        //  4. status是否公开(int)不传默认0(公开)
        if (team.getStatus() == null) {
            team.setStatus(0);
        }
        TeamStatusEnums statusEnums = TeamStatusEnums.getEnumByValue(team.getStatus());
        if (statusEnums == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }

        //  5. 如果status是加密状态，一定要有密码，且密码<=32
        if (TeamStatusEnums.SECRET.equals(statusEnums)) {
            String password = team.getPassword();
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码设置不正确");
            }
        }
        //  6. 超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间<当前时间");
        }
        //  7. 校验用户最多创建5个队伍
        // todo 这里有线程安全问题，可能同时创建100个队伍
        LambdaQueryWrapper<Team> lqw = new LambdaQueryWrapper<>();
        long userId = loginUser.getId();
        lqw.eq(Team::getUserId, userId);
        long count = this.count(lqw);
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户创建队伍数量达到上限");
        }
        // 4.插入队伍到数据库
        team.setId(null);
        team.setUserId(userId);
        boolean success = this.save(team);
        Long teamId = team.getId();
        if (!success || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
        }

        // 5.插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        success = userTeamService.save(userTeam);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return teamId;
    }
}




