package com.musen.utils.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.config.GlobalConfig;
import com.musen.utils.LoadConfigUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:30
 * @Description: 读取全局配置Listener
 */
@Slf4j
public class GlobalConfigListener extends AnalysisEventListener<Map<Integer, String>> {

    private static GlobalConfig globalConfig = new GlobalConfig();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        saveToClass(data.get(1), data.getOrDefault(2, null));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().setGlobalConfig(globalConfig);
        log.info("GlobalConfig 读取完成");
    }

    /**
     * 将数据保存到 globalConfig 中
     *
     * @param key
     * @param value
     */
    private void saveToClass (String key, String value) {
        // 如果碰到空行  或者  value为空 跳出本次循环
        if (StrUtil.isBlank(key) || StrUtil.isBlank(value)) {
            return;
        }
        // 数据源有多个Sheet页 特殊处理
        if ("dataFileSheet".equals(key)) {
            globalConfig.getDataFileSheet().add(value);
            return;
        }
        Field field;
        try {
            // 获取字段
            field = GlobalConfig.class.getDeclaredField(key);
            // 设置该字段可以访问
            field.setAccessible(true);
            field.set(globalConfig, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("将数据保存到 GlobalConfig 中时失败", e);
        }
    }
}
