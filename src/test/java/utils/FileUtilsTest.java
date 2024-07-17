package utils;


import cn.hutool.core.util.StrUtil;
import com.musen.utils.OtherUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author: musen
 * @CreateTime: 2024-06-28  22:43
 * @Description:
 */
public class FileUtilsTest {

    @Test
    public void testGetFilePath() {
        String configFile = OtherUtils.getFilePath();
        System.out.println(configFile);
        Assert.assertNotNull(configFile);
    }


    @Test
    public void nullTest() {
        String value = "null";
        System.out.println(StrUtil.isBlank(value));
    }
}
