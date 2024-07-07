package com.musen.config;

import com.musen.utils.OtherUtils;
import lombok.Data;

import java.util.List;

/**
 * @Author: musen
 * @CreateTime: 2024-07-07  18:05
 * @Description: 全局配置类
 */
@Data
public class GlobalConfig {
    /**
     * sql
     */
    private String sql;

    /**
     * 是否需要读取sqlConfig 默认读取
     */
    private Boolean needLoadSqlConfig = true;

    /**
     * sqlConfig Excel的位置
     */
    private String sqlConfigPath = OtherUtils.getFilePath();

    /**
     * sqlConfig sheet页名字
     */
    private String sqlConfigSheet = "sqlConfig";

    /**
     * 是否预组装sql
     */
    private Boolean needPreassembly = true;

    /**
     * 是否要读取数据
     */
    private Boolean needLoadData = true;

    /**
     * 数据文件存放的位置
     */
    private String dataFilePath;

    /**
     * 数据文件 sheet页名字
     */
    private List<String> dataFileSheet;

    /**
     * 是否需要字段映射
     */
    private Boolean needFieldsReflection = true;

    /**
     * 字段映射配置的文件位置
     */
    private String fieldsReflectionPath = OtherUtils.getFilePath();

    /**
     * 字段映射配置 sheet页名字
     */
    private String fieldsReflectionSheet = "reflection";

    /**
     * 是否有需要计算的字段
     */
    private Boolean needCalculatedField = true;

    /**
     * 字段计算类位置
     */
    private String calculatedClassPath = OtherUtils.getFilePath();

    /**
     * 结果存放的位置
     */
    private String resultFilePath = OtherUtils.getJarPath() + "\\resultFile.sql";

    /**
     * 是否保存日志
     */
    private Boolean saveLogs = true;

    /**
     * 日志存放位置
     */
    private String logsFilePath = OtherUtils.getJarPath() + "\\logs.txt";

    /**
     * 是否直接保存到数据库
     */
    private Boolean saveToDataBase = false;
}
