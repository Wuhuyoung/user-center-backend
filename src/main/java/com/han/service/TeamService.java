package com.han.service;

import com.han.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.han.model.domain.User;

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

}
