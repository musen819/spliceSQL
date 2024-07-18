package com.musen.utils.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.utils.LoadConfigUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-07-06  23:14
 * @Description: 获取字段映射 map
 */
public class FieldsReflectionListener extends AnalysisEventListener<Map<Integer, String>>{

    Map<String, String> fieldsReflectionMap = new HashMap<>();
    int index = 0;

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        index++;
        if (StrUtil.isBlank(data.get(0)) && StrUtil.isBlank(data.getOrDefault(1, null))) {
            throw new RuntimeException(String.format("数据表中字段名 和 数据库中字段名 必须同时有值, 报错行 %s", index));
        }
        fieldsReflectionMap.put(data.get(0), data.get(1));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().setFieldsReflectionMap(fieldsReflectionMap);
    }
}
