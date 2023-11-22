package com.han.service;

import com.han.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.han.model.domain.User;
import com.han.model.request.TeamQueryRequest;
import com.han.model.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 86183
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-10-29 22:01:11
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建用户
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍列表
     * @param teamQueryRequest
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQueryRequest teamQueryRequest, boolean isAdmin);
}
