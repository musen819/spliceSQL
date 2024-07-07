package com.musen.assemble;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;
import com.musen.utils.OtherUtils;
import com.musen.utils.listener.LoadDataListener;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  12:03
 * @Description: 组装SQL
 */
@Slf4j
public class Splice {

    private final SqlConfig sqlConfig = LoadConfigUtils.getSpliceSqlConfig().getSqlConfig();
    private final Map<String, String> globalConfig = LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap();
    private final Statement statement = sqlConfig.getStatement();

    /**
     * 组装SQL
     */
    public void spliceSql(){

        // 1. 判断是否需要预组装SQL
        if (OtherUtils.isTrue(globalConfig.get("needPreassembly"))) {
            System.out.println("预组装SQL：" + statement.toString());
            log.info("预组装SQL：{}", statement);
        }

        // 2. 使用Statement类进行数据组装， 边读数据  边组装
        String filePath= globalConfig.get("dataFilePath");
        if (StrUtil.isBlank(filePath)) {
            log.error("存放数据的文件（{}）找不到", filePath);
            return;
        }
        log.info("开始读取数据，生成SQL");
        // 使用模板方法  因为读数据的时候 每读一条数据 都会执行一次 invoke方法
        EasyExcel.read(filePath, new LoadDataListener()).sheet("globalConfig").doRead();

    }



}
