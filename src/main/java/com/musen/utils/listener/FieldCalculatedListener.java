package com.musen.utils.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.config.FieldCalculated;
import com.musen.utils.LoadConfigUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-07-28  15:12
 * @Description:
 */
public class FieldCalculatedListener extends AnalysisEventListener<Map<Integer, String>> {

    private static Map<String, FieldCalculated> fieldCalculatedMap = new HashMap<>();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        if (StrUtil.isBlank(data.get(0)) || StrUtil.isBlank(data.get(1))) {
            return;
        }
        FieldCalculated fieldCalculated = new FieldCalculated();
        fieldCalculated.setFieldName(data.get(0));
        fieldCalculated.setMethodName(data.get(1));
        List<String> parametersList = new ArrayList<>();
        for (int i = 2; StrUtil.isNotBlank(data.getOrDefault(i, null)); i++) {
            parametersList.add(data.get(i));
        }
        fieldCalculated.setParameters(parametersList);
        fieldCalculatedMap.put(data.get(0), fieldCalculated);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().setFieldCalculatedMap(fieldCalculatedMap);
    }
}
