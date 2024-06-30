package com.musen.preassembly;

import lombok.Getter;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  12:19
 * @Description:
 */
@Getter
public enum PreassemblySqlEnum {

    /**
     *
     */
    INSERT("insert", "com.musen.preassembly.PreassemblyInsert"),
    UPDATE("update", "com.musen.preassembly.PreassemblyUpdate"),
    DELETE("delete", "com.musen.preassembly.PreassemblyDelete");

    private final String key;
    private final String value;

    PreassemblySqlEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getClassName (String key) {
        for (PreassemblySqlEnum typeEnum : PreassemblySqlEnum.values()) {
            if (typeEnum.key.equals(key)) {
                return typeEnum.value;
            }
        }
        return null;
    }

}
