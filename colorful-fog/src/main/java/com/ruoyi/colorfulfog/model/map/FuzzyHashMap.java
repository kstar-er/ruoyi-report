package com.ruoyi.colorfulfog.model.map;

import java.util.HashMap;
import java.util.Map;

public class FuzzyHashMap {
    private Map<String, String> map = new HashMap<>();
    private static final String WILDCARD_KEY = "**";
    private static final String WILDCARD_OTHER_KEY = "其他！";
    private static final String VALUE_EQUAL_KEY = "$key";
    // 添加键值对的方法
    public void put(String key, String value) {
        map.put(key, value);
    }

    // 获取值的方法，支持模糊匹配
    public String get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        else {
            String value = map.get(WILDCARD_OTHER_KEY);
            if (value != null){
               if (value.equals(VALUE_EQUAL_KEY)){
                   return key;
               }else {
                   return value;
               }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        FuzzyHashMap fuzzyMap = new FuzzyHashMap();
        fuzzyMap.put(WILDCARD_KEY, "value for key*");

        System.out.println(fuzzyMap.get("key1"));  // 输出 "value for key*"
        System.out.println(fuzzyMap.get("key2"));  // 输出 "value for key*"
        System.out.println(fuzzyMap.get("key3"));  // 输出 "value for key*"
        System.out.println(fuzzyMap.get("keyX"));  // 输出 null
    }
}
