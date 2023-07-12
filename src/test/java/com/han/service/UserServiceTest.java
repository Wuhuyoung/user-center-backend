package com.han.service;

import com.han.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
}
