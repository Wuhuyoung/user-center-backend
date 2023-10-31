package com.han.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.common.BaseResponse;
import com.han.common.ErrorCode;
import com.han.exception.BusinessException;
import com.han.model.domain.Team;
import com.han.model.domain.User;
import com.han.model.request.TeamAddRequest;
import com.han.model.request.TeamQueryRequest;
import com.han.service.TeamService;
import com.han.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 * @author han
 */
@RestController
@RequestMapping("/team")
@CrossOrigin
@Slf4j
public class TeamController {
    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        long teamId = teamService.addTeam(team, loginUser);
        return BaseResponse.ok(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean success = teamService.removeById(id);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍失败");
        }
        return BaseResponse.ok(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean success = teamService.updateById(team);
        if (!success) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍失败");
        }
        return BaseResponse.ok(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return BaseResponse.ok(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<Team>> listTeam(TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryRequest, team);
        LambdaQueryWrapper<Team> lqw = new LambdaQueryWrapper<>(team);
        List<Team> teamList = teamService.list(lqw);
        return BaseResponse.ok(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPage(TeamQueryRequest teamQueryRequest) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryRequest, team);
        LambdaQueryWrapper<Team> lqw = new LambdaQueryWrapper<>(team);
        Page<Team> page = new Page<>(teamQueryRequest.getCurrentPage(), teamQueryRequest.getPageSize());
        page = teamService.page(page, lqw);
        return BaseResponse.ok(page);
    }
}
