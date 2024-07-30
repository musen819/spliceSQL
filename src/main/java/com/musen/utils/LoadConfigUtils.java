package com.musen.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.musen.config.FieldCalculated;
import com.musen.config.GlobalConfig;
import com.musen.config.SpliceSqlConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.listener.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;
import java.util.Map;

import static com.musen.utils.OtherUtils.isTrue;
import static com.musen.utils.OtherUtils.printLogs;


/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:05
 * @Description: 读取配置
 */
@Slf4j
public class LoadConfigUtils {

    private static final SpliceSqlConfig SPLICE_SQL_CONFIG = new SpliceSqlConfig();


    /**
     * 初始化，获取全局配置 和 Sql配置
     */
    public static void init() {

        String defaultConfigurationFilePath = OtherUtils.getDefaultConfigurationFilePath();
        String defaultConfigurationSheet = "globalConfig";
        // 1. 获取全局配置
        log.info("开始读取 GlobalConfig");
        log.info("GlobalConfig path = {}, sheet = {}", defaultConfigurationFilePath, defaultConfigurationSheet);
        try {
            EasyExcel.read(defaultConfigurationFilePath, new GlobalConfigListener()).sheet(defaultConfigurationSheet).doRead();
        } catch (Exception e) {
            throw new RuntimeException("GlobalConfig 读取失败", e);
        }
        if (LoadConfigUtils.getGlobalConfig() == null) {
            throw new RuntimeException("globalConfig == null, 读取失败");
        }
        printLogs(String.format("GlobalConfig = %s", JSONUtil.toJsonStr(LoadConfigUtils.getGlobalConfig())));

        //2. 获取SQL配置
        loadSqlConfig();
        printLogs(String.format("SqlConfig = %s", JSONUtil.toJsonStr(LoadConfigUtils.getSqlConfig())));
    }

    /**
     * 加载Sql配置
     */
    public static void loadSqlConfig () {

        boolean needSqlConfig = isTrue(LoadConfigUtils.getGlobalConfig().getNeedLoadSqlConfig());
        boolean needLoadSql = isTrue(LoadConfigUtils.getGlobalConfig().getNeedLoadSql());
        if (!needLoadSql && !needSqlConfig) {
            throw new RuntimeException(String.format("needLoadSql = %s ,needLoadSqlConfig = %s  必须有一个为true",
                    LoadConfigUtils.getGlobalConfig().getNeedLoadSql(), LoadConfigUtils.getSpliceSqlConfig().getSqlConfig()));
        }
        // 判断是否要读 SqlConfig
        if (needSqlConfig) {
            log.info("开始读取 SqlConfig");
            String sqlConfigPath = LoadConfigUtils.getGlobalConfig().getSqlConfigPath();
            String sqlConfigSheet = LoadConfigUtils.getGlobalConfig().getSqlConfigSheet();
            log.info("SqlConfig path = {}, sheet = {}", sqlConfigPath, sqlConfigSheet);
            try {
                EasyExcel.read(sqlConfigPath, new SqlConfigListener()).sheet(sqlConfigSheet).doRead();
            } catch (Exception e) {
                throw new RuntimeException("SqlConfig 读取失败", e);
            }
            if (LoadConfigUtils.getSqlConfig() == null) {
                log.error(JSONUtil.toJsonStr(LoadConfigUtils.getSqlConfig()));
                throw new RuntimeException("读取 SqlConfig 失败");
            }
            if (MapUtil.isEmpty(LoadConfigUtils.getSqlConfig().getFieldsValueMap())) {
                LoadConfigUtils.getSqlConfig().setMapNullFlag(true);
                printLogs("sqlConfig.getFieldsValueMap == null, SqlConfig 没有指定字段");
            }
        }

        // 判断要不要读取 Sql
       if (needLoadSql) {
           String sql = LoadConfigUtils.getGlobalConfig().getSql();
           if (StrUtil.isBlank(sql)) {
               throw new RuntimeException(("当 needLoadSql = true 时, sql 不能为空"));
           }
           String[] sqlArgs = sql.split(" ");
           Statement statement = (Statement) OtherUtils.invokeBySqlType(sqlArgs[0], "analyzeSql");
           getSqlConfig().setStatement(statement);
           return;
       }

       if (needSqlConfig) {
           String sqlType = LoadConfigUtils.getSqlConfig().getSqlType();
           String tableName = LoadConfigUtils.getSqlConfig().getTableName();
           if (StrUtil.isBlank(sqlType) || StrUtil.isBlank(tableName)) {
               throw new RuntimeException(String.format("当 needLoadSql = %s ,needLoadSqlConfig = %s 时, SqlConfig 中的 SqlType 和 TableName 必须有值",
                       LoadConfigUtils.getGlobalConfig().getNeedLoadSql(), LoadConfigUtils.getSpliceSqlConfig().getSqlConfig()));
           }
           // 将sqlConfig 中的内容加载到 statement 中
           Statement statement = (Statement) OtherUtils.invokeBySqlType(sqlType, "analyzeSqlBySqlConfig");
           getSqlConfig().setStatement(statement);
           return;
       }

    }

    /**
     * 获取配置
     *
     */
    public static SpliceSqlConfig getSpliceSqlConfig () {
        return SPLICE_SQL_CONFIG;
    }

    public static GlobalConfig getGlobalConfig () {
        if (SPLICE_SQL_CONFIG.getGlobalConfig() == null) {
            init();
        }
        return SPLICE_SQL_CONFIG.getGlobalConfig();
    }

    public static SqlConfig getSqlConfig () {
        if (SPLICE_SQL_CONFIG.getSqlConfig() == null) {
            init();
        }
        return SPLICE_SQL_CONFIG.getSqlConfig();
    }


    /**
     * 获取字段映射关系
     *
     * @return
     */
    public static Map<String, String> getFieldsReflection() {
        if (!isTrue(LoadConfigUtils.getGlobalConfig().getNeedFieldsReflection())) {
            throw new RuntimeException("needFieldsReflection = false, 不能调用获取字段映射方法");
        }
        if (SPLICE_SQL_CONFIG.getFieldsReflectionMap() != null && SPLICE_SQL_CONFIG.getFieldsReflectionMap().size() != 0) {
            return SPLICE_SQL_CONFIG.getFieldsReflectionMap();
        }
        String fieldsReflectionPath = LoadConfigUtils.getGlobalConfig().getFieldsReflectionPath();
        String fieldsReflectionSheet = LoadConfigUtils.getGlobalConfig().getFieldsReflectionSheet();
        if (StrUtil.isBlank(fieldsReflectionPath) || StrUtil.isBlank(fieldsReflectionSheet)) {
            throw new RuntimeException("needFieldsReflection = true, fieldsReflectionPath 和 fieldsReflectionSheet 不能为空");
        }
        log.info("字段映射关系 path：{}, sheet: {}", fieldsReflectionPath, fieldsReflectionSheet);
        try {
            EasyExcel.read(fieldsReflectionPath, new FieldsReflectionListener()).sheet(fieldsReflectionSheet).doRead();
        } catch (RuntimeException e) {
            throw new RuntimeException("获取字段映射关系失败", e);
        }
        log.info("fieldsReflectionMap = {}", JSONUtil.toJsonStr(SPLICE_SQL_CONFIG.getFieldsReflectionMap()));
        return SPLICE_SQL_CONFIG.getFieldsReflectionMap();
    }

    /**
     * 获取字段计算配置
     *
     * @return
     */
    public static Map<String, FieldCalculated> getFieldCalculatedMap() {
        if (!isTrue(LoadConfigUtils.getGlobalConfig().getNeedCalculatedField())) {
            throw new RuntimeException("needCalculatedField = false, 不能调用获取字段计算配置方法");
        }
        if (SPLICE_SQL_CONFIG.getFieldCalculatedMap() != null && SPLICE_SQL_CONFIG.getFieldCalculatedMap().size() != 0) {
            return SPLICE_SQL_CONFIG.getFieldCalculatedMap();
        }
        String calculatedClassConfigPath = LoadConfigUtils.getGlobalConfig().getCalculatedClassConfigPath();
        String calculatedClassConfigSheet = LoadConfigUtils.getGlobalConfig().getCalculatedClassConfigSheet();
        if (StrUtil.isBlank(calculatedClassConfigPath) || StrUtil.isBlank(calculatedClassConfigSheet)) {
            throw new RuntimeException("needCalculatedField = true, calculatedClassConfigPath 和 calculatedClassConfigSheet 不能为空");
        }
        log.info("字段计算配置 path：{}, sheet: {}", calculatedClassConfigPath, calculatedClassConfigSheet);
        EasyExcel.read(calculatedClassConfigPath, new FieldCalculatedListener()).sheet(calculatedClassConfigSheet).doRead();
        log.info("getFieldCalculatedMap = {}", JSONUtil.toJsonStr(SPLICE_SQL_CONFIG.getFieldCalculatedMap()));
        return SPLICE_SQL_CONFIG.getFieldCalculatedMap();
    }

    /**
     * 获取字段删除配置
     *
     * @return
     */
    public static List<String> getDeleteFieldList() {
        if (!isTrue(LoadConfigUtils.getGlobalConfig().getDeleteFieldsOrNot())) {
            throw new RuntimeException("deleteFieldsOrNot = false, 不能调用获取字段删除配置方法");
        }
        if (SPLICE_SQL_CONFIG.getDeleteFieldList() != null && SPLICE_SQL_CONFIG.getDeleteFieldList().size() != 0) {
            return SPLICE_SQL_CONFIG.getDeleteFieldList();
        }
        String deleteFieldsConfigPath = LoadConfigUtils.getGlobalConfig().getDeleteFieldsConfigPath();
        String deleteFieldsConfigSheet = LoadConfigUtils.getGlobalConfig().getDeleteFieldsConfigSheet();
        if (StrUtil.isBlank(deleteFieldsConfigPath) || StrUtil.isBlank(deleteFieldsConfigSheet)) {
            throw new RuntimeException("needCalculatedField = true, deleteFieldsConfigPath 和 deleteFieldsConfigSheet 不能为空");
        }
        log.info("字段删除配置 path：{}, sheet: {}", deleteFieldsConfigPath, deleteFieldsConfigSheet);
        EasyExcel.read(deleteFieldsConfigPath, new DeleteFieldListener()).sheet(deleteFieldsConfigSheet).doRead();
        log.info("deleteFieldList = {}", JSONUtil.toJsonStr(SPLICE_SQL_CONFIG.getDeleteFieldList()));
        return SPLICE_SQL_CONFIG.getDeleteFieldList();
    }

}
