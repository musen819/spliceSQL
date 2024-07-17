package com.musen.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.musen.config.GlobalConfig;
import com.musen.config.SpliceSqlConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.listener.FieldsReflectionListener;
import com.musen.utils.listener.GlobalConfigListener;
import com.musen.utils.listener.SqlConfigListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.musen.utils.OtherUtils.isTrue;


/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:05
 * @Description: 读取配置
 */
@Slf4j
public class LoadConfigUtils {

    private static final SpliceSqlConfig SPLICE_SQL_CONFIG;

    static {
        SPLICE_SQL_CONFIG = new SpliceSqlConfig();
    }

    /**
     * 初始化，获取全局配置 和 Sql配置
     */
    public static void init() {

        String fileName = OtherUtils.getFilePath();
        // 1. 获取全局配置
        log.info("开始读取 GlobalConfig");
        EasyExcel.read(fileName, new GlobalConfigListener()).sheet("globalConfig").doRead();
        if (LoadConfigUtils.getGlobalConfig() == null) {
            log.error(JSONUtil.toJsonStr(LoadConfigUtils.getGlobalConfig()));
            throw new RuntimeException("读取 globalConfig 失败");
        }
        log.info("GlobalConfig = {}", JSONUtil.toJsonStr(LoadConfigUtils.getGlobalConfig()));

        //2. 获取SQL配置
        loadSqlConfig();
        log.info("SqlConfig = {}", JSONUtil.toJsonStr(LoadConfigUtils.getSqlConfig()));

    }

    /**
     * 获取Sql配置
     */
    public static void loadSqlConfig () {
        boolean needSqlConfig = isTrue(LoadConfigUtils.getGlobalConfig().getNeedLoadSqlConfig());
        boolean needLoadSql = isTrue(LoadConfigUtils.getGlobalConfig().getNeedLoadSql());
        // 判断是否要读 sqlConfig
        if (needSqlConfig) {
            log.info("开始读取 sqlConfig");
            // 取文件所在位置 和 sheetName
            String sqlConfigPath = LoadConfigUtils.getGlobalConfig().getSqlConfigPath();
            String sqlConfigSheet = LoadConfigUtils.getGlobalConfig().getSqlConfigSheet();
            log.info("SqlConfig path = {}, sheet = {}", sqlConfigPath, sqlConfigSheet);
            EasyExcel.read(sqlConfigPath, new SqlConfigListener()).sheet(sqlConfigSheet).doRead();
            if (LoadConfigUtils.getSqlConfig() == null) {
                log.error(JSONUtil.toJsonStr(LoadConfigUtils.getSqlConfig()));
                throw new RuntimeException("读取 sqlConfig 失败");
            }
            if (StrUtil.isBlank(LoadConfigUtils.getSqlConfig().getSqlType()) || StrUtil.isBlank(LoadConfigUtils.getSqlConfig().getTableName())) {
                throw new RuntimeException(String.format("SQLType = %s ,tableName = %s  不能为空",
                        LoadConfigUtils.getSqlConfig().getSqlType(), LoadConfigUtils.getSqlConfig().getTableName()));
            }
            if (MapUtil.isEmpty(LoadConfigUtils.getSqlConfig().getFieldsValueMap())) {
                log.error(LoadConfigUtils.getSqlConfig().getFieldsValueMap().toString());
                throw new RuntimeException("未获取到需要组装的字段");
            }
        }

        // 判断要不要读取SQL
       if (needLoadSql) {
           String sql = LoadConfigUtils.getGlobalConfig().getSql();
           if (StrUtil.isBlank(sql)) {
               throw new RuntimeException(("当 needLoadSql = true 时, sql 不能为空"));
           }
           String[] sqlArgs = sql.split(" ");
           OtherUtils.analyzeSql(sqlArgs[0], "analyzeSql");
       }

       if (needSqlConfig) {
           // 将sqlConfig 中的内容加载到 statement 中
           OtherUtils.analyzeSql(LoadConfigUtils.getSqlConfig().getSqlType(), "analyzeSqlBySqlConfig");
       }

        throw new RuntimeException(String.format("needLoadSql = %s ,needLoadSqlConfig = %s  必须有一个为true",
                LoadConfigUtils.getGlobalConfig().getNeedLoadSql(), LoadConfigUtils.getSpliceSqlConfig().getSqlConfig()));

    }

    /**
     * 获取字段映射关系
     *
     * @return
     */
    public static Map<String, String> getFieldsReflection() {
        if (SPLICE_SQL_CONFIG.getFieldsReflectionMap() != null) {
            return SPLICE_SQL_CONFIG.getFieldsReflectionMap();
        }

        String fieldsReflectionPath = LoadConfigUtils.getGlobalConfig().getFieldsReflectionPath();
        String fieldsReflectionSheet = LoadConfigUtils.getGlobalConfig().getFieldsReflectionSheet();
        if (!isTrue(LoadConfigUtils.getGlobalConfig().getNeedFieldsReflection())) {
            throw new RuntimeException("needFieldsReflection = false, 不能调用获取字段映射方法");
        }
        if (StrUtil.isBlank(fieldsReflectionPath) || StrUtil.isBlank(fieldsReflectionSheet)) {
            throw new RuntimeException("needFieldsReflection = true, fieldsReflectionPath 和 fieldsReflectionSheet 不能为空");
        }
        log.info("字段映射关系 path：{}, sheet: {}", fieldsReflectionPath, fieldsReflectionSheet);
        EasyExcel.read(fieldsReflectionPath, new FieldsReflectionListener()).sheet(fieldsReflectionSheet).doRead();
        log.info("SqlConfig = {}", JSONUtil.toJsonStr(SPLICE_SQL_CONFIG.getFieldsReflectionMap()));
        return SPLICE_SQL_CONFIG.getFieldsReflectionMap();
    }


    /**
     * 获取配置
     *
     */
    public static SpliceSqlConfig getSpliceSqlConfig () {
        return SPLICE_SQL_CONFIG;
    }

    public static GlobalConfig getGlobalConfig () {
        return SPLICE_SQL_CONFIG.getGlobalConfig();
    }

    public static SqlConfig getSqlConfig () {
        return SPLICE_SQL_CONFIG.getSqlConfig();
    }

}
