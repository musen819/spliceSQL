package com.musen.utils.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.musen.analyze.AnalyzeSqlEnum;
import com.musen.config.GlobalConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;
import com.musen.utils.OtherUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statement;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final List<String> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

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
    private static final SqlConfig SQL_CONFIG = LoadConfigUtils.getSqlConfig();
    private static final GlobalConfig GLOBAL_CONFIG = LoadConfigUtils.getGlobalConfig();
    private static final Map<String,String> FIELDS_REFLECTION_MAP = LoadConfigUtils.getFieldsReflection();

    private static Statement statement;
    private static FileWriter fileWriter;
    private static final BufferedWriter BUFFERED_WRITER;


    static {
        // 为了避免影响sqlConfig 中的配置  使用深拷贝  序列化 + 反序列化 实现
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        try {
            oos = new ObjectOutputStream(bos);
            // 从流中读取对象
            oos.writeObject(SQL_CONFIG.getStatement());
            ObjectInputStream ois = new ObjectInputStream(bis);
            statement = (Statement) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("深拷贝失败", e);
        }

        // 提前打开要写的文件，整个文件读完 在关闭
        try {
            // true表示追加模式，如果希望覆盖则改为 false
            fileWriter = new FileWriter(GLOBAL_CONFIG.getResultFilePath(), true);
        } catch (IOException e) {
            log.error("打开文件失败", e);
        }
        BUFFERED_WRITER = new BufferedWriter(fileWriter);
    }

    /**
     * 读取Data Excel 的表头
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        headerForExcelData = (List<String>) headMap.values();
    }



    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        Map<String, String> datamap = new HashMap<>();
        // 1. 通过表头 替换掉原statement 中的部分value
        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            String columnName = headerForExcelData.get(entry.getKey());
            if (FIELDS_REFLECTION_MAP.containsKey(columnName)) {
                columnName = FIELDS_REFLECTION_MAP.get(columnName);
            }
            datamap.put(columnName, entry.getValue());
        }
        // 获取类型
        String className = AnalyzeSqlEnum.getClassName(SQL_CONFIG.getSqlType());
        statement = OtherUtils.callMethod(className, "replace", statement, datamap);
        cachedDataList.add(statement.toString());
        if (cachedDataList.size() == BATCH_COUNT) {
            saveData();
        }
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
        cachedDataList.clear();
        // TODO 增加插入数据库
    }

}
