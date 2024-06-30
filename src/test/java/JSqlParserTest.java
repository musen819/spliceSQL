import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import org.junit.Test;

/**
 * @Author: musen
 * @CreateTime: 2024-06-30  10:54
 * @Description:
 */
public class JSqlParserTest {

    @Test
    public void parseSqlTest () {
        String sql = "INSERT INTO `user_center`.`user` (`id`, `username`, `userAccount`, `userPassword`, `gender`, `avatar`, `phone`, `email`, `userStatus`, `userRole`, `createTime`, `updateTime`, `isDelete`) VALUES (5, 'musen', 'musen', 'e95bfd8448bda367e40a03bfd62d8954', NULL, 'https://s11.ax1x.com/2024/02/13/pF8t5md.jpg', NULL, NULL, 0, 1, '2024-02-14 10:44:24', '2024-02-27 16:42:12', 0);";


        try {
            Insert insert = (Insert)CCJSqlParserUtil.parse(sql);
            System.out.println("【插入目标表】：" + insert.getTable());
            System.out.println("【插入字段】：" + insert.getColumns());
            System.out.println("【插入值】：" + insert.getValues());
            System.out.println("1111");
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
    }
}
