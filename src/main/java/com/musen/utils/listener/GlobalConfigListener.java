package com.musen.utils.listener;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.utils.LoadConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:30
 * @Description: 读取全局配置Listener
 */
@Slf4j
public class GlobalConfigListener extends AnalysisEventListener<Map<Integer, String>> {

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        log.info("从 {} 的 {} 中解析到一条数据：{}",
                context.readWorkbookHolder().getFile(),
                context.readSheetHolder().getSheetName(),
                JSONUtil.toJsonStr(data));

        LoadConfigUtils.getSpliceSqlConfig().getGlobalConfigMap().put(data.get(1), data.getOrDefault(2, null));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("globalConfig 读取完成");
    }
}
