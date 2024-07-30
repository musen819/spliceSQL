package com.musen.analyze;

import cn.hutool.core.util.StrUtil;
import com.musen.config.FieldCalculated;
import com.musen.config.GlobalConfig;
import com.musen.config.SqlConfig;
import com.musen.utils.FieldsCalculatedClassUtils;
import com.musen.utils.LoadConfigUtils;
import com.musen.utils.OtherUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.NullValue;
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

import static com.musen.utils.OtherUtils.deleteQuotationMark;
import static com.musen.utils.OtherUtils.isTrue;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  22:19
 * @Description:
 */
@Slf4j
public class AnalyzeInsert implements AnalyzeSql{

    private static final SqlConfig SQL_CONFIG = LoadConfigUtils.getSqlConfig();
    private static final GlobalConfig GLOBAL_CONFIG = LoadConfigUtils.getGlobalConfig();


    @Override
    public Statement analyzeSqlBySqlConfig() {
        // todo 需要删除字段
        return null;
    }

    @Override
    public Statement analyzeSql() {
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
            throw new RuntimeException("jsqlparser 解析 Sql 失败", e);
        }
        // 删除字段
        insert = (Insert) OtherUtils.invokeBySqlType(SQL_CONFIG.getSqlType(), "deleteField", insert, LoadConfigUtils.getDeleteFieldList());


        if (!isTrue(GLOBAL_CONFIG.getNeedLoadSqlConfig())) {
            return insert;
        }

        // 表名有值不一样 报错 为空 正常执行
        // 将 sqlConfig 中的配置 合并到 statement 中
        // 如果 SqlType 有值且和Sql中的不一样  报错
        if (StrUtil.isNotBlank(SQL_CONFIG.getSqlType())) {
            boolean bool1 = AnalyzeSqlEnum.INSERT.getKey().equals(SQL_CONFIG.getSqlType());
            boolean bool2 = AnalyzeSqlEnum.INSERT_MAJUSCULE.getKey().equals(SQL_CONFIG.getSqlType());
            if (!(bool1 || bool2)) {
                throw new RuntimeException(String.format("Insert 解析类 不能解析 %s 的sql", SQL_CONFIG.getSqlType()));
            }
        }
        // 如果 tableName 有值且和Sql中的不一样 覆盖sql
        if (StrUtil.isNotBlank(SQL_CONFIG.getTableName())) {
            if (!insert.getTable().getName().equals(SQL_CONFIG.getTableName())) {
                Table table = new Table(SQL_CONFIG.getTableName());
                insert.setTable(table);
            }
        }
        // 更新值列表
        insert = (Insert) replace(insert, SQL_CONFIG.getFieldsValueMap());
        return insert;

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
                // todo 抽离成方法
                if (StrUtil.isBlank(newValue) || "NULL".equals(newValue) || "null".equals(newValue)){
                    values.set(i, new NullValue());
                } else if ("sysdate".equals(newValue)) {
                    // todo 对时间类型 做更精细的调整
                    Column column = new Column("sysdate");
                    values.set(i, column);
                } else {
                    values.set(i, new StringValue(newValue));
                }
            }
        }
        return insert;
    }

    @Override
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

    @Override
    public Statement calculated(Statement statement, Map<String, FieldCalculated> calculatedMap) {
        Insert insert;
        if (statement instanceof Insert) {
            insert = (Insert) statement;
        } else {
            throw new RuntimeException("字段计算失败");
        }
        ExpressionList<Column> columns = insert.getColumns();
        ExpressionList<Expression> values = (ExpressionList<Expression>) insert.getValues().getExpressions();
        // 遍历所有列
        for (int i = 0; i < columns.size(); i++) {
            String columnName = columns.get(i).getColumnName();
            // 用列名查字段计算map里有没有这一列
            if (calculatedMap.containsKey(columnName) && i < values.size()) {
                FieldCalculated fieldCalculated = calculatedMap.get(columnName);
                // 拿到计算字段配置类
                List<String> argumentFields = fieldCalculated.getParameters();
                // 组装真是的数据
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
                    // values.get(index).toString() 输出的是 '1' 去掉''
                    String tempValue = values.get(index).toString();
                    argumentValues.add(deleteQuotationMark(tempValue));
                }
                String newValue = FieldsCalculatedClassUtils.invokeMethod(fieldCalculated.getMethodName(), argumentValues);
                values.set(i, new StringValue(newValue));
            }
        }
        return insert;
    }

}
