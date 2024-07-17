package com.musen.utils;

import cn.hutool.core.util.StrUtil;
import com.musen.Main;
import com.musen.analyze.AnalyzeSqlEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-28  22:26
 * @Description: 其他工具类
 */
@Slf4j
public class OtherUtils {

    private static final String CONFIG_FILE_PATH = getFilePathByName("config.xlsx");
    private static volatile Path jarPath;


    /**
     * 获取config文件路径
     *
     * @param
     * @return
     */
    public static String getFilePath () {
        return CONFIG_FILE_PATH;
    }

    /**
     * 获取jar包 所在路径
     *
     * @return
     */
    public static Path getJarPath () {
        if (jarPath == null) {
            synchronized (OtherUtils.class) {
                if (jarPath == null) {
                    try {
                        jarPath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return jarPath;
    }

    /**
     * 获取Jar 同目录下指定名称的配置文件
     *
     * @param fileName
     * @return
     */
    public static String getFilePathByName(String fileName) {
        String filePath;
        filePath = getJarPath().resolve(fileName).toString();
        log.info("配置文件路径 = {}", filePath);
        return filePath;
    }

    /**
     * 判断是否真  为 1、true、TRUE
     *
     * @return
     */
    public static boolean isTrue(String value) {
        return ("true".equals(value) || "TRUE".equals(value) || "1".equals(value));
    }


    /**
     * 解析sql
     * 根据传来的sql类型，通过枚举类获取类名，调用 callDefaultMethod 执行对应方法
     *
     * @param sqlType
     * @param methodName
     */
    public static void analyzeSql (String sqlType, String methodName) {
        String className = AnalyzeSqlEnum.getClassName(sqlType);
        if (StrUtil.isBlank(className)) {
            throw new RuntimeException(String.format("未获取到该sql类型对应的类名, sql 类型 = %s", sqlType));
        }
        callMethod(className, methodName);
    }

    /**
     * 根据className，获取实例并调用 methodName 方法
     */
    public static void callMethod(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            method.invoke(clazz.newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("获取对象实例或调用方法失败", e);
        }
    }

    public static Statement callMethod(String className, String methodName, Statement statement, Map<String, String> map) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName, Statement.class, Map.class);
            statement = (Statement) method.invoke(clazz.newInstance(), statement, map);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException("获取对象实例或调用方法失败", e);
        }
        return statement;
    }
}
