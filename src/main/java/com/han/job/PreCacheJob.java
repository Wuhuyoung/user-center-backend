package com.han.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.han.model.domain.User;
import com.han.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 缓存预热
 */
@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private List<Integer> mainUserIds = Arrays.asList(1); // 后续可以动态配置活跃用户的id

    /**
     * 每天凌晨1点，预热推荐用户缓存
     */
    @Scheduled(cron = "0 50 21 * * *") // 每天21:50进行缓存预热
    // 在线cron表达式生成：https://qqe2.com/cron
    public void doCacheCommendUsers() {
        // 查询缓存
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        for (Integer userId : mainUserIds) {
            // 查询数据库
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
            userPage.setRecords(userPage.getRecords().stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList()));

            // 写缓存
            String key = String.format("plusyou:user:recommend:%s", userId);
            try {
                opsForValue.set(key, userPage, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.error("redis set key error", e);
            }
        }
    }
}
