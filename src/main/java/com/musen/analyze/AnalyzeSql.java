package com.musen.analyze;

import com.musen.config.FieldCalculated;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  21:50
 * @Description: 解析sql接口
 */
public interface AnalyzeSql {

    /**
     * 解析 sql
     * 把读取到的 sqlConfig 加载到 statement 中
     */
    Statement analyzeSqlBySqlConfig();

    /**
     * 解析 sql
     * 把 sql 加载到 statement 中
     */
    Statement analyzeSql();

    /**
     * 字段替换方法
     *
     * @param statement
     * @param map
     * @return
     */
    Statement replace(Statement statement, Map<String, String> map);

    /**
     * 字段计算方法
     *
     * @param statement
     * @param fieldCalculatedMap
     * @return
     */
    Statement calculated (Statement statement, Map<String, FieldCalculated> fieldCalculatedMap);

    /**
     * 字段删除方法
     *
     * @param statement
     * @param deleteFieldList
     * @return
     */
    Statement deleteField (Statement statement, List<String> deleteFieldList);
}
