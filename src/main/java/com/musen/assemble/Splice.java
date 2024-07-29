package com.musen.assemble;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.musen.config.GlobalConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;
import com.musen.utils.OtherUtils;
import com.musen.utils.listener.LoadDataListener;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.util.List;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  12:03
 * @Description: 组装SQL
 */
@Slf4j
public class Splice {

    private final SqlConfig sqlConfig = LoadConfigUtils.getSqlConfig();
    private final GlobalConfig globalConfig = LoadConfigUtils.getGlobalConfig();
    private final Statement statement = sqlConfig.getStatement();


    public void spliceSql(){

        // 1. 判断是否需要展示或者保存未加载的SQL
        if (OtherUtils.isTrue(globalConfig.getNeedPreassembly())) {
            log.info("未加载数据的SQL：{}", statement);
        }
        // todo
        // 1.2 保存未加载数据的SQL
        if (OtherUtils.isTrue(globalConfig.getSavePreassembly())) {
            String savePreassemblyPath = globalConfig.getSavePreassemblyPath();
            // todo 根据文件类型  决定保存 方式  和要不要获取sheet名称
        }

        // 2. 使用Statement类进行数据组装， 边读数据  边组装
        String dataFilePath= globalConfig.getDataFilePath();
        List<String> dataFileSheetList = globalConfig.getDataFileSheet();
        if (StrUtil.isBlank(dataFilePath) || dataFileSheetList == null) {
            throw new RuntimeException(String.format("dataFilePath = %s, dataFileSheetList = %s, dataFilePath 或 dataFileSheetList 不能为空",
                    dataFilePath, JSONUtil.toJsonStr(dataFileSheetList)));
        }
        for (String dataFileSheet : dataFileSheetList) {
            try {
                EasyExcel.read(dataFilePath, new LoadDataListener()).sheet(dataFileSheet).doRead();
            } catch (RuntimeException e) {
                throw new RuntimeException(String.format("dataFilePath = %s, dataFileSheet = %s 文件路径不正确", dataFilePath, dataFileSheet), e);
            }
        }
        log.info("开始读取数据，生成SQL");
        // 使用模板方法  因为读数据的时候 每读一条数据 都会执行一次 invoke方法


    }



}
