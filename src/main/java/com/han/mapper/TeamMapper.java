package com.han.mapper;

import com.han.model.domain.Team;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.han.model.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 86183
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-10-29 22:01:11
* @Entity com.han.model.domain.Team
*/
public interface TeamMapper extends BaseMapper<Team> {

    /**
     * 查询加入队伍的用户
     * @param teamId
     * @return
     */
    List<User> selectJoinUser(@Param("teamId") Long teamId);
}




