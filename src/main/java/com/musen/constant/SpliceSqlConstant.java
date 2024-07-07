package com.musen.constant;

/**
 * @Author: musen
 * @CreateTime: 2024-06-28  23:43
 * @Description: 常量
 */
public interface SpliceSqlConstant {

    /**
     * 读sql配置时，字段没有固定值，默认赋值常量
     * 为了方便生成 预组装的SQL 默认值是用 {}
     */
    String DEFAULT_FIELD_VALUE = "{}";
}
