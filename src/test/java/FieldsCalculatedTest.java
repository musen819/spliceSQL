import com.musen.utils.FieldsCalculatedClassUtils;
import com.musen.utils.LoadConfigUtils;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Author: musen
 * @CreateTime: 2024-07-18  23:15
 * @Description:
 */
public class FieldsCalculatedTest {

    @Test
    public void testSampleMethod () throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String classDir = "D:/WorkSpace/spliceSQL/target/"; // 指定存放类文件的文件夹路径
        String className = "FieldsCalculated"; // 类名
        String methodName = "sampleMethod"; // 方法名

        // 指定要编译的Java文件的路径
        String sourcePath = "D:/WorkSpace/spliceSQL/target/FieldsCalculated.java";
        // 指定编译后的class文件存放的目录
        String outputPath = "D:/WorkSpace/spliceSQL/target/";

        // 获取系统Java编译器
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // 设置编译选项，这里设置的是将编译后的class文件输出到指定目录
        String[] compileOptions = new String[]{"-d", outputPath, sourcePath};

        // 执行编译操作
        int compilationResult = compiler.run(null, null, null, compileOptions);

        // 检查编译结果
        if (compilationResult == 0) {
            System.out.println("Compilation is successful");
        } else {
            System.out.println("Compilation Failed");
        }


        // 创建一个URLClassLoader，加载指定路径下的类
        File classPath = new File(classDir);
        URL[] urls = {classPath.toURI().toURL()};
        URLClassLoader classLoader = new URLClassLoader(urls);

        // 加载指定的类
        Class<?> loadedClass = classLoader.loadClass(className);

        // 获取指定方法名的方法
        Method method = loadedClass.getMethod(methodName);

        // 如果方法是静态的，可以直接调用 invoke(null)
        // 如果方法是实例方法，需要先创建实例对象，然后调用 invoke(instance)
        // 这里假设 myMethod 是静态方法，直接调用 invoke(null)
        method.invoke(null);

        // 关闭自定义的类加载器
        classLoader.close();
    }

    @Test
    public void testInvokeMethod () {
        LoadConfigUtils.init();
        System.out.println(FieldsCalculatedClassUtils.invokeMethod("sampleMethod"));
        System.out.println(FieldsCalculatedClassUtils.invokeMethod("getPremPeriodType", "1"));
    }
}
