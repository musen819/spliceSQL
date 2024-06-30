package com.musen.analyze;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  22:13
 * @Description: sql-对应解析类类名
 */
public enum AnalyzeSqlEnum {
    /**
     *
     */
    INSERT("insert", "com.musen.analyze.AnalyzeInsert"),
    INSERT_MAJUSCULE("INSERT", "com.musen.analyze.AnalyzeInsert"),
    UPDATE("update", "com.musen.preassembly.PreassemblyUpdate"),
    DELETE("delete", "com.musen.preassembly.PreassemblyDelete");

    private final String key;
    private final String value;

    AnalyzeSqlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getClassName (String key) {
        for (AnalyzeSqlEnum typeEnum : AnalyzeSqlEnum.values()) {
            if (typeEnum.key.equals(key)) {
                return typeEnum.value;
            }
        }
        return null;
    }
}
