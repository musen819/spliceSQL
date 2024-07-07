package com.musen.utils.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-07-03  23:23
 * @Description:
 */
@Slf4j
public class LoadDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 单次缓存的数据量
     */
    public static final int BATCH_COUNT = 100;

    /**
     *临时存储
     */
    private List<String> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    /**
     * 存放数据表头
     */
    private List<String> headerForExcelData = new ArrayList<>();
    /**
     * 记录数据总数
     */
    private int nums = 0;

    /**
     * 获取sql配置 和 全局配置
     */
    private SqlConfig sqlConfig = LoadConfigUtils.getSpliceSqlConfig().getSqlConfig();
    private static final Map<String, String> GLOBAL_CONFIG = LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap();

    private static FileWriter fileWriter;
    private static final BufferedWriter BUFFERED_WRITER;

    /**
     * 读取Data Excel 的表头
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headerForExcelData = (List<String>) headMap.values();
    }

    // 提前打开要写的文件，整个文件读完 在关闭
    static {
        try {
            // true表示追加模式，如果希望覆盖则改为 false
            fileWriter = new FileWriter(GLOBAL_CONFIG.get("resultFilePath"), true);
        } catch (IOException e) {
            log.error("打开文件失败", e);
        }
        BUFFERED_WRITER = new BufferedWriter(fileWriter);
    }

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        // 1. 通过表头 替换掉原statement 中的部分value
        Statement statement = sqlConfig.getStatement();

        // 1.1 替换的时候  注意查看 是否有映射字段
        // 1.2 同时计算有特殊逻辑的
        // 2. 检查sql是否还有没有赋值的
    }



    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        try {
            BUFFERED_WRITER.flush();
        } catch (IOException e) {
            log.error("关闭文件失败", e);
        }
        log.info("sql 生成完成, 共生成 {} 条数据", nums);
    }


    private void saveData(){
        for (String sql : cachedDataList) {
            try {
                BUFFERED_WRITER.write(sql + "\n");
                nums++;
            } catch (IOException e) {
                log.error("写文件失败", e);
            }
        }
        // TODO 增加插入数据库
    }

}
