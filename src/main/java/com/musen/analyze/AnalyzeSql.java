package com.musen.analyze;

import com.musen.config.FieldCalculated;
import com.musen.config.GlobalConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  21:50
 * @Description: 解析sql接口
 */
public interface AnalyzeSql {

    SqlConfig SQL_CONFIG = LoadConfigUtils.getSqlConfig();
    GlobalConfig GLOBAL_CONFIG = LoadConfigUtils.getGlobalConfig();
    List<String> deleteFieldList = LoadConfigUtils.getDeleteFieldList();

    /**
     * 解析 sql
     * 把读取到的 sqlConfig 加载到 statement 中
     *
     */
    void analyzeSqlBySqlConfig();

    /**
     * 解析 sql
     * 把 sql 加载到 statement 中
     *
     */
    void analyzeSql();

    Statement replace(Statement statement, Map<String, String> map);

    Statement calculated (Statement statement, Map<String, FieldCalculated> fieldCalculatedMap);
}
