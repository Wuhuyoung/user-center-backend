package com.han.service;

import com.han.mapper.UserMapper;
import com.han.model.domain.User;
import io.netty.util.concurrent.CompleteFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.han.constant.UserConstants.SALT;

/**
 * 用户服务测试
 * @author han
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("admin");
        user.setUserAccount("123");
        user.setAvatarUrl("https://images.zsxq.com/FrVxEXU_R1P2G_I698UP3XZgq4G8?e=1680278399&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:FMi9lGRvaGV3cY3VbFj3vlJP0jE=");
        user.setPassword("123");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String account = "";
        String password = "123456789";
        String checkedPassword = "123456789";
        String planetCode = "1";
        long result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        account = "122";
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        account = "12345";
        password = "123";
        checkedPassword = "123";
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        password = "123 45";
        checkedPassword = "123 45";
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        checkedPassword = "999999999";
        password = "111111111";
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        account = "123";
        password = "123456789";
        checkedPassword = password;
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);
        account = "4280";
        planetCode = "4280";
        result = userService.userRegister(account, password, checkedPassword, planetCode);
        Assertions.assertEquals(-1, result);

    }

    @Test
    void searchUserByTags() {
        List<String> tagNameList = Arrays.asList("Java", "Android");
        List<User> users = userService.searchUserByTags(tagNameList);
        Assertions.assertNotNull(users);
    }

    /**
     * 批量插入用户
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch(); //计时工具，使用System.currentTimeMillis()计时也可以
        stopWatch.start();
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("young");
            user.setUserAccount("young");
            user.setAvatarUrl("https://typora-1314662469.cos.ap-shanghai.myqcloud.com/img/202308042002520.jpg");
            user.setGender(0);
            user.setProfile("热爱编程的精神小伙一枚");
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
            user.setPassword(encryptPassword);
            user.setPhone("13956780312");
            user.setEmail("1138841120@qq.com");
            user.setPlanetCode(i + 6 + "");
            user.setTags("[\"男\",\"Java\", \"Python\", \"Go\"]");
            user.setStatus(0);
            user.setUserRole(0);
            userList.add(user);
        }
        // 利用saveBatch批量插入，每插入100条数据建立一次数据库连接
        // 耗时0.5s
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    @Test
    public void doCurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch(); //计时工具，使用System.currentTimeMillis()计时也可以
        stopWatch.start();

        int batchSize = 1000;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 40, 1000, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10000));

        for (int i = 0; i < 10; i++) {
            List<User> userList = new ArrayList<>(batchSize);
            for (int j = 0; j < batchSize; j++) {
                User user = new User();
                user.setUsername("young");
                user.setUserAccount("young");
                user.setAvatarUrl("https://typora-1314662469.cos.ap-shanghai.myqcloud.com/img/202308042002520.jpg");
                user.setGender(0);
                user.setProfile("热爱编程的精神小伙一枚");
                String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
                user.setPassword(encryptPassword);
                user.setPhone("13956780312");
                user.setEmail("1138841120@qq.com");
                user.setPlanetCode(i + 6 + "");
                user.setTags("[\"男\",\"Java\", \"Python\", \"Go\"]");
                user.setStatus(0);
                user.setUserRole(0);
                userList.add(user);
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                // 利用saveBatch批量插入，每插入batchSize条数据建立一次数据库连接
                userService.saveBatch(userList, batchSize);
            }, executor);
            futureList.add(future);
        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
