package com.musen.preassembly;

import com.musen.config.SqlConfig;
import com.musen.utils.LoadConfigUtils;

/**
 * @Author: musen
 * @CreateTime: 2024-06-29  12:02
 * @Description:
 */
public interface Preassembly {

     SqlConfig preassemblyConfig = LoadConfigUtils.getSpliceSqlConfig().getSqlConfig();

     String getPreassemblySql();


}
