package utils;

import cn.hutool.core.util.StrUtil;
import com.musen.utils.LoadConfigUtils;
import org.junit.Test;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:46
 * @Description:
 */
public class LoadConfigUtilsTest {

    @Test
    public void testGetGlobalConfig() {
        LoadConfigUtils.init();
    }

    @Test
    public void testSql() {
        String sql = "INSERT INTO `user_center`.`user` (`id`, `username`, `userAccount`, `userPassword`, `gender`, `avatar`, `phone`, `email`, `userStatus`, `userRole`, `createTime`, `updateTime`, `isDelete`) VALUES (5, 'musen', 'musen', 'e95bfd8448bda367e40a03bfd62d8954', NULL, 'https://s11.ax1x.com/2024/02/13/pF8t5md.jpg', , , 0, 1, '2024-02-14 10:44:24', '2024-02-27 16:42:12', 0);";

        // 当sql中 value部分有空值的时候  使用jsqlparser 解析Sql 会报错 先遍历一次sql 防止这种情况
        String[] sqlArgs = sql.split(",");
        for (int i = 0; i < sqlArgs.length; i++) {
            if (StrUtil.isBlank(sqlArgs[i])) {
                sqlArgs[i] = " NULL";
            }
        }
        sql = StrUtil.join(",", (Object) sqlArgs);
        System.out.println(sql);
    }

}
