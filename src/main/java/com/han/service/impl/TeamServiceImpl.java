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
import com.han.model.request.TeamQueryRequest;
import com.han.model.vo.TeamUserVO;
import com.han.model.vo.UserVO;
import com.han.service.TeamService;
import com.han.mapper.TeamMapper;
import com.han.service.UserService;
import com.han.service.UserTeamService;
import netscape.security.UserTarget;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
    @Resource
    private TeamMapper teamMapper;
    @Resource
    private UserService userService;

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
        LambdaQueryWrapper<Team> lqw = new LambdaQueryWrapper<>();
        long userId = loginUser.getId();
        lqw.eq(Team::getUserId, userId);
        long count = this.count(lqw);
        // todo 这里有线程安全问题，可能同时创建100个队伍
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

    @Override
    public List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin) {
        LambdaQueryWrapper<Team> lqw = new LambdaQueryWrapper<>();

        if (teamQueryRequest != null) {
            Long id = teamQueryRequest.getId();
            String name = teamQueryRequest.getName();
            String description = teamQueryRequest.getDescription();
            Integer maxNum = teamQueryRequest.getMaxNum();
            Long userId = teamQueryRequest.getUserId();
            Integer status = teamQueryRequest.getStatus();
            String searchText = teamQueryRequest.getSearchText();

            if (id != null && id > 0) {
                lqw.eq(Team::getId, id);
            }
            // 可以根据关键字同时查询名称和描述
            if (StringUtils.isNotBlank(searchText)) {
                lqw.and(qw -> qw.like(Team::getName, searchText).or().like(Team::getDescription, searchText));
            }
            if (StringUtils.isNotBlank(name)) {
                lqw.like(Team::getName, name);
            }
            if (StringUtils.isNotBlank(description)) {
                lqw.like(Team::getDescription, description);
            }
            // 根据队伍最大人数查询
            if (maxNum != null && maxNum > 1) {
                lqw.eq(Team::getMaxNum, maxNum);
            }
            // 根据创建人查询
            if (userId != null && userId > 0) {
                lqw.eq(Team::getUserId, userId);
            }
            // 根据队伍状态查询
            TeamStatusEnums statusEnum = TeamStatusEnums.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnums.PUBLIC;
            }
            // 非管理员不能查看私有/加密的队伍
            if (!isAdmin && !statusEnum.equals(TeamStatusEnums.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            lqw.eq(Team::getStatus, statusEnum.getValue());
        }
        // 过期的队伍不查询
        // expire_time == null or expire_time > new Date()
        lqw.and(qw -> qw.isNull(Team::getExpireTime).or().gt(Team::getExpireTime, new Date()));

        List<Team> teamList = this.list(lqw);
        List<TeamUserVO> teamUserVOList = new ArrayList<>();

        if (CollectionUtils.isEmpty(teamList)) {
            return teamUserVOList;
        }

        // 关联查询用户
        Date now = new Date();
        for (Team team : teamList) {
            Long teamId = team.getId();
            // 查询加入队伍的用户
            List<User> userList = teamMapper.selectJoinUser(teamId);
            List<UserVO> userVOList = userList.stream().map(UserVO::convertUserToUserVO)
                    .collect(Collectors.toList());

            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            teamUserVO.setUserList(userVOList);
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }
}




