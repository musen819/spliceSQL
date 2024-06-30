package com.musen.analyze;

import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;

import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  21:50
 * @Description: 解析sql接口
 */
public interface AnalyzeSql {

    SqlConfig SQL_CONFIG = LoadConfigUtils.getSpliceSqlConfig().getSqlConfig();

    Map<String, String> GLOBAL_CONFIG = LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap();

    default void defaultMethod() {
        analyzeSql();
    }

    void analyzeSql();
}
