package once;


import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Excel对应用户信息
 */
@Data
public class ExcelUserInfo {
    @ExcelProperty("用户名")
    private String username;

    @ExcelProperty("账号")
    private String userAccount;

    @ExcelProperty("性别")
    private Integer gender;

    @ExcelProperty("密码")
    private String password;

    @ExcelProperty("头像")
    private String avatarUrl;
}
