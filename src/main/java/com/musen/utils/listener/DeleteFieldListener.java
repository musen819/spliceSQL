package com.musen.utils.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.utils.LoadConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-07-28  22:05
 * @Description:
 */
public class DeleteFieldListener extends AnalysisEventListener<Map<Integer, String>> {

    private static List<String> deleteFields = new ArrayList<>();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        deleteFields.add(data.get(0));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().setDeleteFieldList(deleteFields);
    }
}
