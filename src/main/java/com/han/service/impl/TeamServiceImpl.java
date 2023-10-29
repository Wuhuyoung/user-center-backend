package com.han.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.han.model.domain.Team;
import com.han.service.TeamService;
import com.han.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 86183
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-10-29 22:01:11
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




