package com.musen.utils.listener;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.musen.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  00:30
 * @Description: 读取全局配置Listener
 */
@Slf4j
public class GlobalConfigListener extends AnalysisEventListener<Map<Integer, String>> {

    static Map<String, Object> globalConfigMap = new HashMap<>();
    static GlobalConfig globalConfig = new GlobalConfig();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        saveToClass(data.get(1), data.getOrDefault(2, null));
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("globalConfig 读取完成");
    }

    private void saveToClass (String key, Object value) {
        if (StrUtil.isBlank(key) || ObjUtil.isEmpty(value)) {
            // 如果碰到空行  或者  value为空 跳出本次循环
            return;
        }
        // todo 增加数据源sheet页 配置
        Field field;
        try {
            // 获取字段
            field = GlobalConfig.class.getDeclaredField(key);
            // 设置该字段可以访问
            field.setAccessible(true);
            field.set(globalConfig, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
