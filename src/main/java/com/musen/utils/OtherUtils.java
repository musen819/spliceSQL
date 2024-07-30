package com.musen.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.musen.Main;
import com.musen.analyze.AnalyzeSqlEnum;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-28  22:26
 * @Description: 其他工具类
 */
@Slf4j
public class OtherUtils {

    private static volatile Path jarPath;
    private static volatile String defaultConfigurationFilePath;

    /**
     * 获取config文件路径
     *
     * @param
     * @return
     */
    public static String getDefaultConfigurationFilePath () {
        if (StrUtil.isBlank(defaultConfigurationFilePath)) {
            synchronized (OtherUtils.class) {
                if (jarPath == null) {
                    defaultConfigurationFilePath = getFilePathByName("config.xlsx");
                    log.info("配置文件路径 = {}", defaultConfigurationFilePath);
                }
            }
        }
        return defaultConfigurationFilePath;
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
                        throw new RuntimeException("获取Jar包所在路径失败", e);
                    }
                }
            }
        }
        return jarPath;
    }

    /**
     * 获取Jar包同目录下 fileName 配置文件路径
     *
     * @param fileName
     * @return
     */
    public static String getFilePathByName(String fileName) {
        return getJarPath().resolve(fileName).toString();
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
     * 获取对应的类、方法、参数类型
     *
     * @param sqlType
     * @param methodName
     * @param args
     */
    public static Object invokeBySqlType (String sqlType, String methodName,Object ... args) {
        String className = AnalyzeSqlEnum.getClassName(sqlType);
        if (StrUtil.isBlank(className)) {
            throw new RuntimeException(String.format("未获取到 SqlType 对应的类名, SqlType = %s", sqlType));
        }
        Class<?> clazz;
        Class<?>[] argsTypes = new Class<?>[args.length];;
        try {
            for (int i = 0; i < args.length; i++) {
                // 读取的时候 会校验参数不能为空  如果参数可能为空 需要专门对空参数进行处理
                // 因为如果 params[i] == null 的话 params[i].getClass() 会报错
                Class<?> tempType = args[i].getClass();
                if ((Statement.class).isAssignableFrom(tempType)){
                    tempType = Statement.class;
                } else if ((HashMap.class).isAssignableFrom(tempType)) {
                    tempType = Map.class;
                } else if ((ArrayList.class).isAssignableFrom(tempType)) {
                    tempType = List.class;
                }
                argsTypes[i] = tempType;
            }
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("通过类名获取类失败",e);
        }
        return invoke(clazz, methodName, argsTypes, args);
    }

    /**
     * 获取方法、执行
     *
     * @param clazz
     * @param methodName
     * @param argsTypes
     * @param args
     * @return
     */
    public static Object invoke(Class<?> clazz, String methodName, Class<?>[] argsTypes, Object ...args) {
        try {
            printLogs(String.format("执行 %s 类的 %s 方法, 参数 %s 参数类型 %s",
                    clazz, methodName, JSONUtil.toJsonStr(args), JSONUtil.toJsonStr(argsTypes)));
            Method method = clazz.getMethod(methodName, argsTypes);
            return method.invoke(clazz.newInstance(), args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(String.format("获取方法失败: %s 类的 %s 方法, 参数 %s 参数类型 %s",
                    clazz, methodName, JSONUtil.toJsonStr(args), JSONUtil.toJsonStr(argsTypes)), e);
        } catch (InstantiationException e) {
            throw new RuntimeException(String.format("创建对象实例失败: %s 类的 %s 方法, 参数 %s 参数类型 %s",
                    clazz, methodName, JSONUtil.toJsonStr(args), JSONUtil.toJsonStr(argsTypes)), e);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(String.format("调用方法失败: %s 类的 %s 方法, 参数 %s 参数类型 %s",
                    clazz, methodName, JSONUtil.toJsonStr(args), JSONUtil.toJsonStr(argsTypes)), e);
        }  catch (RuntimeException e) {
            throw new RuntimeException(String.format("获取对象实例或调用方法失败: %s 类的 %s 方法, 参数 %s 参数类型 %s",
                    clazz, methodName, JSONUtil.toJsonStr(args), JSONUtil.toJsonStr(argsTypes)), e);
        }
    }

    /**
     * 删除字段中的单引号
     *
     * @param value
     * @return
     */
    public static  String deleteQuotationMark (String value) {
        if ('\'' ==value.charAt(0) &&  '\''== value.charAt(value.length() - 1)) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * 详细日志打印
     * @param message
     */
    public static void printLogs(String message) {
        if (isTrue(LoadConfigUtils.getGlobalConfig().getPrintLogs())) {
            log.info(message);
        }
    }


}
