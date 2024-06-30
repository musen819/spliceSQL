package com.musen.utils;

import cn.hutool.core.util.StrUtil;
import com.musen.Main;
import com.musen.analyze.AnalyzeSqlEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author: musen
 * @CreateTime: 2024-06-28  22:26
 * @Description: 其他工具类
 */
@Slf4j
public class OtherUtils {

    /**
     * 获取config文件路径
     *
     * @param
     * @return
     */
    public static String getFilePath () {
        return getFilePathByName("config.xlsx");
    }

    /**
     * 获取Jar 同目录下指定名称的配置文件
     *
     * @param fileName
     * @return
     */
    public static String getFilePathByName(String fileName) {
        String filePath;
        try {
            Path jarPath =  Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            filePath = jarPath.resolve(fileName).toString();
            log.info("配置文件路径 = {}", filePath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
     * 根据传来的sql类型，通过枚举类获取类名，调用 callDefaultMethod 执行默认方法
     *
     * @param key sql类型
     */
    public static void analyzeSql (String key) {
        String className = AnalyzeSqlEnum.getClassName(key);
        if (StrUtil.isBlank(className)) {
            throw new RuntimeException(String.format("未获取到该sql类型对应的类名, sql 类型 = %s", key));
        }
        callDefaultMethod(className);

    }

    /**
     * 根据className，获取实例并调用默认方法
     */
    public static void callDefaultMethod(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod("defaultMethod");
            method.invoke(clazz.newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
