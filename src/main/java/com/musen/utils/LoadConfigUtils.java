package com.musen.utils;

import com.alibaba.excel.EasyExcel;
import com.musen.config.SpliceSqlConfig;
import com.musen.utils.listener.GlobalConfigListener;
import com.musen.utils.listener.SqlConfigListener;
import lombok.extern.slf4j.Slf4j;

import static com.musen.utils.OtherUtils.isTrue;


/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:05
 * @Description: 读取配置
 */
@Slf4j
public class LoadConfigUtils {

    private static final SpliceSqlConfig SPLICE_SQL_CONFIG = new SpliceSqlConfig();


    /**
     * 获取配置
     *
     * @return
     */
    public static SpliceSqlConfig getSpliceSqlConfig () {
        return SPLICE_SQL_CONFIG;
    }


    /**
     * 初始化，获取全局配置 和 Sql配置
     */
    public static void init() {

        String fileName = OtherUtils.getFilePath();
        // 1. 获取全局配置
        log.info("开始读取 GlobalConfig");
        EasyExcel.read(fileName, new GlobalConfigListener()).sheet("globalConfig").doRead();
        log.info("GlobalConfig = {}", LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap());

        //2. 获取SQL配置
        getSqlConfig(fileName);
        log.info("SqlConfig = {}", LoadConfigUtils.getSpliceSqlConfig().getSqlConfig());

    }

    /**
     * 获取Sql配置
     *
     * @param fileName
     */
    public static void getSqlConfig (String fileName) {
        // 判断读配置 还是 解析Sql
        if (isTrue(LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap().get("needLoadSqlConfig"))) {
            log.info("开始读取 sqlConfig");
            EasyExcel.read(fileName, new SqlConfigListener()).sheet("sqlConfig").doRead();
            return;
        }
        log.info("开始解析 sql");
        String sql = LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap().get("sql");
        String[] sqlArgs = sql.split(" ");
        OtherUtils.analyzeSql(sqlArgs[0]);
    }




}
