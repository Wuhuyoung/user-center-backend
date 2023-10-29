package com.han.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.model.domain.UserTeam;
import com.han.service.UserTeamService;
import com.han.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 86183
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-10-29 22:01:18
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




