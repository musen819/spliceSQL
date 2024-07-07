package com.musen.analyze;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  22:19
 * @Description:
 */
@Slf4j
public class AnalyzeInsert implements AnalyzeSql{

    @Override
    public void analyzeSql() {
        String sql = GLOBAL_CONFIG.get("sql");
        Insert insert;
        try {
            insert = (Insert) CCJSqlParserUtil.parse(sql);
            SQL_CONFIG.setStatement(insert);
            log.info("【插入目标表】： {}", insert.getTable());
            log.info("【插入字段】： {}", insert.getColumns());
            log.info("【插入值】： {}", insert.getValues());
            SQL_CONFIG.setSqlType("INSERT");
            SQL_CONFIG.setTableName(insert.getTable().toString());
            for (int i = 0; i < insert.getColumns().size(); i++) {
                String tempField = insert.getColumns().get(i).toString();
                String tempValue = insert.getValues().getExpressions().get(i).toString();
                SQL_CONFIG.getFieldsValueMap().put(tempField, tempValue);
            }
            log.info("sql 解析完成");
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }

        // TODO
        // 当insert语句中 value部分有空值的事后  使用jsqlparser 解析Sql 会报错  需要手工解析

    }
}
