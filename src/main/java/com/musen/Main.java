package com.musen;

import com.musen.config.SpliceSqlConfig;
import com.musen.utils.LoadConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: musen
 * @CreateTime: ${YEAR}-${MONTH}-${DAY}  ${HOUR}:${MINUTE}
 * @Description:
 */
@Slf4j
public class Main {
    public static void main(String[] args) {

        log.info("1. 获取配置");
        LoadConfigUtils.init();
        SpliceSqlConfig spliceSqlConfig = LoadConfigUtils.getSpliceSqlConfig();




    }
}