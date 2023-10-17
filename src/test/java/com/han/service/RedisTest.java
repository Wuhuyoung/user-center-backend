package com.han.service;

import com.han.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * Redis测试类
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() {
        // 引入新的组件前，测试一下
        redisTemplate.opsForValue().set("test:yang", "yangStr");
        redisTemplate.opsForValue().set("test:num", 100);
        redisTemplate.opsForValue().set("test:smallNum", 1.5);
        User user = new User();
        user.setId(0L);
        user.setUsername("test");
        user.setUserAccount("");
        redisTemplate.opsForValue().set("test:user", user);

        String str = (String) redisTemplate.opsForValue().get("test:yang");
        Assertions.assertEquals("yangStr", str);
        Integer num = (Integer) redisTemplate.opsForValue().get("test:num");
        Assertions.assertEquals(100, num);
        Double smallNum = (Double) redisTemplate.opsForValue().get("test:smallNum");
        Assertions.assertEquals(1.5, smallNum);
        User user1 = (User) redisTemplate.opsForValue().get("test:user");
        Assertions.assertNotNull(user1);
        Assertions.assertEquals("test", user1.getUsername());
    }

    public static void main(String[] args) {
        String commonSequence = getLongestCommonSequence("cabe", "afbc");
        System.out.println(commonSequence);
    }

    /**
     * 求最长公共子序列
     * @param text1
     * @param text2
     * @return 最长公共子序列
     */
    public static String getLongestCommonSequence(String text1, String text2) {
        if (text1 == null || text2 == null) {
            return "";
        }
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        for (int i = 1; i <= text1.length(); i++) {
            for (int j = 1; j <= text2.length(); j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        for (int i = 0; i <= text1.length(); i++) {
            for (int j = 0; j <= text2.length(); j++) {
                System.out.print(dp[i][j] + " ");
            }
            System.out.println();
        }
        StringBuilder res = new StringBuilder();
        int i = text1.length(), j = text2.length();
        while (i > 0 && j > 0) {
            if (dp[i][j] == dp[i - 1][j]) {
                i--;
            } else if (dp[i][j] == dp[i][j - 1]) {
                j--;
            } else if (dp[i][j] == dp[i - 1][j - 1] + 1) {
                res.append(text2.charAt(j - 1));
                i--;
                j--;
            }
        }
        return res.reverse().toString();
    }
}
