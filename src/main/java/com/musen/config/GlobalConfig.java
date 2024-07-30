package com.musen.config;

import com.musen.utils.OtherUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: musen
 * @CreateTime: 2024-07-07  18:05
 * @Description: 全局配置类
 */
@Data
public class GlobalConfig {

    /**
     * 是否需要读sql
     */
    private String needLoadSql = String.valueOf(true);

    /**
     * sql
     */
    private String sql;

    /**
     * 是否需要读取sqlConfig 默认读取
     */
    private String needLoadSqlConfig = String.valueOf(true);

    /**
     * sqlConfig Excel的位置
     */
    private String sqlConfigPath = OtherUtils.getDefaultConfigurationFilePath();

    /**
     * sqlConfig sheet页名字
     */
    private String sqlConfigSheet = "sqlConfig";

    /**
     * 是否预组装sql
     */
    private String needPreassembly = String.valueOf(true);

    /**
     * 是否保存预组装sql
     */
    private String savePreassembly = String.valueOf(true);

    /**
     * 预组装sql保存路径
     */
    private String savePreassemblyPath = OtherUtils.getDefaultConfigurationFilePath();

    /**
     * 预组装sql保存sheet页
     */
    private String savePreassemblySheet = "preassemblySql";

    /**
     * 是否要读取数据
     */
    private String needLoadData = String.valueOf(true);

    /**
     * 数据文件存放的位置
     */
    private String dataFilePath;

    /**
     * 数据文件 sheet页名字
     */
    private List<String> dataFileSheet = new ArrayList<>();

    /**
     * 是否需要字段映射
     */
    private String needFieldsReflection = String.valueOf(true);

    /**
     * 字段映射配置的文件位置
     */
    private String fieldsReflectionPath = OtherUtils.getDefaultConfigurationFilePath();

    /**
     * 字段映射配置 sheet页名字
     */
    private String fieldsReflectionSheet = "reflection";

    /**
     * 是否有需要删除字段
     */
    private String deleteFieldsOrNot = String.valueOf(true);

    /**
     * 删除字段配置存放位置
     */
    private String deleteFieldsConfigPath = OtherUtils.getDefaultConfigurationFilePath();

    /**
     * 删除字段配置存放Sheet页
     */
    private String deleteFieldsConfigSheet = "deleteFields";

    /**
     * 是否有需要计算的字段
     */
    private String needCalculatedField = String.valueOf(true);

    /**
     * 缓存字段计算类
     */
    private String needCacheCalculatedFieldClass = String.valueOf(true);

    /**
     * 是否需要重新编译字段加载类
     */
    private String recompileOrNot = String.valueOf(true);

    /**
     * 字段计算配置位置
     */
    private String calculatedClassConfigPath = OtherUtils.getDefaultConfigurationFilePath();

    /**
     * 字段计算配置Sheet页名字
     */
    private String calculatedClassConfigSheet = "fieldsCalculated";

    /**
     * 字段计算类位置
     */
    private String calculatedClassPath = String.valueOf(OtherUtils.getJarPath());

    /**
     * 字段计算类类名
     */
    private String calculatedClassName = "FieldsCalculated";

    /**
     * 字段计算类编译后存放位置
     */
    private String compileCalculatedClassName = String.valueOf(OtherUtils.getJarPath());

    /**
     * 结果存放的位置
     */
    private String resultFilePath = OtherUtils.getJarPath() + "\\resultFile.sql";

    /**
     * 打印详细日志
     */
    private String printLogs = String.valueOf(true);

    /**
     * 是否保存日志
     */
    private String saveLogs = String.valueOf(true);

    /**
     * 日志存放位置
     */
    private String logsFilePath = OtherUtils.getJarPath() + "\\logs.txt";

    /**
     * 是否直接保存到数据库
     */
    private String saveToDataBase = String.valueOf(false);
}
