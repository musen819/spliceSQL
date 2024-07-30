package com.musen;

import com.musen.assemble.Splice;
import com.musen.utils.LoadConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: musen
 * @CreateTime:
 * @Description:
 */
@Slf4j
public class Main {
    public static void main(String[] args) {

        // 1. 获取全局配置 和 sql config
        LoadConfigUtils.init();

        // 2. 组装SQL
        new Splice().spliceSql();

        // 3. 整理保存日志

    }
}