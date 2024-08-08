package com.musen.cache;

import cn.hutool.core.map.MapUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段计算方法缓存
 */
public class calculatedCache {

    private static Map<Integer, List<Integer>> calculatedCacheMap = new HashMap<>();

    public static void addCalculatedCache () {

    }

    public static Map<Integer, List<Integer>> getCalculatedCache () {
        if (MapUtil.isEmpty(calculatedCacheMap)) {
            return null;
        }
        return calculatedCacheMap;
    }
}
