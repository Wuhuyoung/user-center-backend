package once;
import java.util.Date;

import com.han.mapper.UserMapper;
import com.han.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

@Component
public class InsertUser {

    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch(); //计时工具，使用System.currentTimeMillis()计时也可以
        stopWatch.start();
        for (int i = 0; i < 1000; i++) {
            User user = new User();
            user.setUsername("");
            user.setUserAccount("");
            user.setAvatarUrl("");
            user.setGender(0);
            user.setProfile("");
            user.setPassword("");
            user.setPhone("");
            user.setEmail("");
            user.setPlanetCode("");
            user.setTags("");
            user.setStatus(0);
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
