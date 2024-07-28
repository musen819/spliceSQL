package com.musen.utils.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.musen.config.SqlConfig;
import com.musen.constant.SpliceSqlConstant;
import com.musen.utils.LoadConfigUtils;
import com.musen.utils.OtherUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  01:41
 * @Description: 读取sql配置的Listener
 */
@Slf4j
public class SqlConfigListener implements ReadListener<Map<Integer, String>> {

    static SqlConfig sqlConfig = new SqlConfig();

    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        /*log.info("从 {} 的 {} 中解析到一条数据：{}",
                context.readWorkbookHolder().getFile(),
                context.readSheetHolder().getSheetName(),
                JSONUtil.toJsonStr(data));*/

        //todo 添加常量
        if (data.containsKey(0) && data.get(0) != null) {
            sqlConfig.setSqlType(data.get(0));
            sqlConfig.setTableName(data.get(1));
        }

        // 判断是否为固定值
        boolean isFixed = OtherUtils.isTrue(data.get(4));
        if (isFixed) {
            sqlConfig.getFieldsValueMap().put(data.get(2), data.getOrDefault(3, null));
        } else {
            sqlConfig.getFieldsValueMap().put(data.get(2), SpliceSqlConstant.DEFAULT_FIELD_VALUE);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LoadConfigUtils.getSpliceSqlConfig().setSqlConfig(sqlConfig);
    }
}
