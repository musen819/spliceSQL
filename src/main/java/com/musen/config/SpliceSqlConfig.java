package com.musen.config;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-27  23:41
 * @Description: 全局通用配置 实体类
 */
@Data
public class SpliceSqlConfig {

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig;

    /**
     * Sql 配置
     */
    private SqlConfig sqlConfig = new SqlConfig();

    /**
     * 字段映射关系，当表字段和Excel中字段不一致的时候使用
     * 将配置以K-V的形式读入
     */
    private Map<String, String> fieldsReflectionMap = new HashMap<>();

    /**
     * 字段计算List
     * key 字段名
     * value
     */
    private Map<String, FieldCalculated> fieldCalculatedMap = new HashMap<>();

    /**
     * 字段删除List
     */
    private List<String> deleteFieldList = new ArrayList<>();

}
