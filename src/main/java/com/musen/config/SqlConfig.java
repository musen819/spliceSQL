package com.musen.config;

import lombok.Data;
import net.sf.jsqlparser.statement.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-27  23:38
 * @Description: SQL配置 实体类
 */
@Data
public class SqlConfig {

    /**
     * sql 类型
     */
    private String sqlType;

    /**
     * 要插入的表名
     */
    private String tableName;

    /**
     * 字段，如果是固定值，会在读取的时候赋值
     * 不是固定值，默认赋值 noValue
     */
    private Map<String, String> fieldsValueMap = new HashMap<>();

    /**
     * 为了方便快速解析SQL 引入jsqlparser
     */
    Statement statement = null;
}
