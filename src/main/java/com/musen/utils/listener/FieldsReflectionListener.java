package com.musen.utils.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.utils.LoadConfigUtils;

import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-07-06  23:14
 * @Description:
 */
public class FieldsReflectionListener extends AnalysisEventListener<Map<Integer, String>>{
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().getFieldsReflectionMap().put(data.get(1), data.getOrDefault(2, null));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
