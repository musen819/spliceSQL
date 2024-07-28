package com.musen.config;

import lombok.Data;

import java.util.List;

/**
 * @Author: musen
 * @CreateTime: 2024-07-27  19:38
 * @Description: 字段计算配置类
 */
@Data
public class FieldCalculated {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数列表
     */
    private List<String> parameters;


}
