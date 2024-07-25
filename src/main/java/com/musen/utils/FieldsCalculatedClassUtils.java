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
    public String invokeMethod(String methodName) {

        Class<?> loadedClass = getInstance();

        // 获取指定方法名的方法
        Method method = null;
        try {
            method = loadedClass.getMethod(methodName);
            // 如果方法是静态的，可以直接调用 invoke(null)
            // 如果方法是实例方法，需要先创建实例对象，然后调用 invoke(instance)
            // 这里假设 myMethod 是静态方法，直接调用 invoke(null)
            return (String) method.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("获取方法失败, 方法名： %s", methodName), e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(String.format("方法调用失败, 方法名： %s", methodName), e);
        }
    }

    /**
     * 获取实例
     */
    public static Class<?> getInstance() {
        if (!OtherUtils.isTrue(GLOBAL_CONFIG.getNeedCacheCalculatedFieldClass())) {
            return newInstance();
        }
        if (cacheFieldsCalculated == null) {
            synchronized (FieldsCalculatedClassUtils.class) {
                if (cacheFieldsCalculated == null) {
                    cacheFieldsCalculated = newInstance();
                }
            }
        }
        return cacheFieldsCalculated;
    }

    /**
     * 创建实例
     */
    public static Class<?> newInstance() {

        String classDir = GLOBAL_CONFIG.getCalculatedClassPath();
        String className = GLOBAL_CONFIG.getCalculatedClassName();

        String sourcePath = StrUtil.concat(false, classDir, "/", className);
        String outputPath = GLOBAL_CONFIG.getCompileCalculatedClassName();

        compileClass(sourcePath, outputPath);
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
            throw new RuntimeException("编译失败", e);
        }

    }
}
