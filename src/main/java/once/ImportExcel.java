package once;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入Excel用户信息
 */
public class ImportExcel {
    public static void main(String[] args) {
        readData();
    }

    private static void readData() {
        String fileName = "D:\\program\\project\\星球项目\\用户中心\\code\\user-center\\user-center\\src\\main\\resources\\userInfo.xlsx";
        listenerRead(fileName);
//        synchronousRead(fileName);
    }

    /**
     * 利用监听器读，一条一条数据读出并处理，适用于数据量大的场景
     * @param fileName
     */
    private static void listenerRead(String fileName) {
        EasyExcel.read(fileName, ExcelUserInfo.class, new ExcelUserInfoListener())
                .sheet().doRead();
    }

    /**
     * 同步读，当数据量大时可能延迟时间长，且会将数据全部一次性读入内存
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<ExcelUserInfo> list = EasyExcel.read(fileName).head(ExcelUserInfo.class).sheet().doReadSync();
        for (ExcelUserInfo data : list) {
            System.out.println(data);
        }

    }

}
