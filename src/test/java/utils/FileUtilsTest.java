package utils;


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
}
