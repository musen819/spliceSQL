package com.musen.analyze;

import cn.hutool.core.util.StrUtil;
import com.musen.config.FieldCalculated;
import com.musen.utils.FieldsCalculatedClassUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.musen.utils.OtherUtils.isTrue;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  22:19
 * @Description:
 */
@Slf4j
public class AnalyzeInsert implements AnalyzeSql{

    @Override
    public void analyzeSqlBySqlConfig() {

    }

    @Override
    public void analyzeSql() {
        String sql = GLOBAL_CONFIG.getSql();
        // 当sql中 value部分有空值的时候  使用jsqlparser 解析Sql 会报错 先遍历一次sql 防止这种情况
        String[] sqlArgs = sql.split(",");
        for (int i = 0; i < sqlArgs.length; i++) {
            if (StrUtil.isBlank(sqlArgs[i])) {
                sqlArgs[i] = " NULL";
            }
        }
        sql = StrUtil.join(",", (Object) sqlArgs);

        Insert insert;
        try {
            insert = (Insert) CCJSqlParserUtil.parse(sql);
        } catch (JSQLParserException e) {
            throw new RuntimeException("jsqlparser 解析 sql 失败", e);
        }

        if (!isTrue(GLOBAL_CONFIG.getNeedLoadSqlConfig())) {
            SQL_CONFIG.setStatement(insert);
            return;
        }

        // 将 sqlConfig 中的配置 合并到 statement 中
        /*if (!AnalyzeSqlEnum.INSERT.name().equals(SQL_CONFIG.getSqlType()) || !AnalyzeSqlEnum.INSERT_MAJUSCULE.name().equals(SQL_CONFIG.getSqlType())  ) {
            throw new RuntimeException(String.format("insert解析类 不能解析 %s 的sql", SQL_CONFIG.getSqlType()));
        }*/
        if (!insert.getTable().getName().equals(SQL_CONFIG.getTableName())) {
            Table table = new Table(SQL_CONFIG.getTableName());
            insert.setTable(table);
        }
        // 更新值列表
        insert = (Insert) replace(insert, SQL_CONFIG.getFieldsValueMap());
        SQL_CONFIG.setStatement(insert);


    }

    @Override
    public Statement replace(Statement statement, Map<String, String> map) {
        Insert insert;
        if (statement instanceof Insert) {
            insert = (Insert) statement;
        } else {
            throw new RuntimeException("字段替换失败");
        }
        ExpressionList<Column> columns = insert.getColumns();
        ExpressionList<Expression> values = (ExpressionList<Expression>) insert.getValues().getExpressions();
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getColumnName();
            if (map.containsKey(columnName) && i < values.size()) {
                String newValue = map.get(columnName);
                values.set(i, new StringValue(newValue));
            }
        }
        return insert;
    }

    public Statement calculated(Statement statement, Map<String, FieldCalculated> calculatedMap) {
        Insert insert;
        if (statement instanceof Insert) {
            insert = (Insert) statement;
        } else {
            throw new RuntimeException("字段计算失败");
        }
        ExpressionList<Column> columns = insert.getColumns();
        ExpressionList<Expression> values = (ExpressionList<Expression>) insert.getValues().getExpressions();
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getColumnName();
            if (calculatedMap.containsKey(columnName) && i < values.size()) {
                FieldCalculated fieldCalculated = calculatedMap.get(columnName);
                List<String> argumentFields = fieldCalculated.getParameters();
                List<String> argumentValues = new ArrayList<String>();
                for (String argument : argumentFields) {
                    int index = -1;
                    for (int j = 0; j < columns.size(); j++) {
                        if (columns.get(j).getColumnName().equals(argument)) {
                            index = j;
                            break;
                        }
                    }
                    if (index == -1) {
                        throw new RuntimeException("index == -1");
                    }
                    argumentValues.add(values.get(index).toString());
                }
                String newValue = FieldsCalculatedClassUtils.invokeMethod(fieldCalculated.getMethodName(), argumentValues);
                values.set(i, new StringValue(newValue));
            }
        }
        return insert;
    }

    public Statement deleteField(Statement statement, List<String> deleteFieldList) {
        Insert insert;
        if (statement instanceof Insert) {
            insert = (Insert) statement;
        } else {
            throw new RuntimeException("字段删除失败");
        }
        ExpressionList<Column> columns = insert.getColumns();
        ExpressionList<Expression> values = (ExpressionList<Expression>) insert.getValues().getExpressions();
        for (int i = 0; i < columns.size(); i++) {
            if (deleteFieldList.contains(columns.get(i).getColumnName())) {
                columns.remove(i);
                values.remove(i);
            }
        }
        return insert;
    }

}
