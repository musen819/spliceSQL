package com.musen.utils;

import cn.hutool.core.util.StrUtil;
import com.musen.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @Author: musen
 * @CreateTime: 2024-07-18  23:45
 * @Description: 加载调用 字段计算类 工具 FieldsCalculated
 */
@Slf4j
public class FieldsCalculatedClassUtils {

    private static final GlobalConfig GLOBAL_CONFIG = LoadConfigUtils.getGlobalConfig();

    /**
     * 缓存计算类  不用每次都去获取
     */
    private static volatile Class<?> cacheFieldsCalculated;

    /**
     * 调用方法
     *
     * @param methodName
     * @return
     */
    public static String invokeMethod(String methodName, List<String> paramsList) {

        Class<?> loadedClass = getFieldsCalculatedClass();
        String[] params = new String[paramsList.size()];
        for (int i = 0; i < paramsList.size(); i++) {
            params[i] = paramsList.get(i);
        }
        Class<?>[] parameterTypes = new Class<?>[paramsList.size()];
        for (int i = 0; i < params.length; i++) {
            // 读取的时候 会校验参数不能为空  如果参数可能为空 需要专门对空参数进行处理
            // 因为如果 params[i] == null 的话 params[i].getClass() 会报错
            parameterTypes[i] = params[i].getClass();
        }

        // 获取指定方法名的方法
        Method method;
        try {
            method = loadedClass.getMethod(methodName, parameterTypes);
            return (String) method.invoke(null, params);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("获取方法失败, 方法名： %s", methodName), e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(String.format("方法调用失败, 方法名： %s", methodName), e);
        }
    }

    /**
     * 获取类对象
     */
    public static Class<?> getFieldsCalculatedClass() {
        if (!OtherUtils.isTrue(GLOBAL_CONFIG.getNeedCacheCalculatedFieldClass())) {
            return loadFieldsCalculatedClass();
        }
        if (cacheFieldsCalculated == null) {
            synchronized (FieldsCalculatedClassUtils.class) {
                if (cacheFieldsCalculated == null) {
                    cacheFieldsCalculated = loadFieldsCalculatedClass();
                }
            }
        }
        return cacheFieldsCalculated;
    }

    /**
     * 加载类
     */
    public static Class<?> loadFieldsCalculatedClass() {

        String classDir = GLOBAL_CONFIG.getCalculatedClassPath();
        String className = GLOBAL_CONFIG.getCalculatedClassName();

        String sourcePath = StrUtil.concat(false, classDir, "\\", className, ".java");
        String outputPath = GLOBAL_CONFIG.getCompileCalculatedClassName();

        // 编译类
        if (OtherUtils.isTrue(GLOBAL_CONFIG.getRecompileOrNot())) {
            compileClass(sourcePath, outputPath);
        }
        URLClassLoader classLoader = null;

        try {
            // 创建一个URLClassLoader，加载指定路径下的类
            File classPath = new File(classDir);
            URL[] urls = {classPath.toURI().toURL()};
            classLoader = new URLClassLoader(urls);

            // 加载类
            return classLoader.loadClass(className);

        } catch (MalformedURLException | ClassNotFoundException e) {
            throw new RuntimeException("类加载失败", e);
        } finally {
            // 关闭自定义的类加载器
            try {
                if (classLoader != null) {
                    classLoader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 编译指定类
     *
     * @param sourcePath java 文件路径
     * @param outputPath class 文件保存路径
     * @return
     */
    private static void compileClass(String sourcePath, String outputPath) {
        try {
            // 获取系统Java编译器
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            // 设置编译选项，这里设置的是将编译后的class文件输出到指定目录
            String[] compileOptions = new String[]{"-d", outputPath, sourcePath};

            // 编译
            int compilationResult = compiler.run(null, null, null, compileOptions);

            // 检查编译结果
            if (compilationResult == 0) {
                log.info("编译成功");
            } else {
                throw new RuntimeException("编译失败");
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(String.format("编译失败, sourcePath = %s, outputPath = %s",sourcePath, outputPath), e);
        }

    }
}
