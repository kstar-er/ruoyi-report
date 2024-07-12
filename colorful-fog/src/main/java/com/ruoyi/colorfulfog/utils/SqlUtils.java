package com.ruoyi.colorfulfog.utils;

import java.util.ArrayList;
import java.util.List;

public class SqlUtils {
    /**
     * 将传入的sql语句中带带有[!]号的单词替换为对应的去掉[!]号的单词的小写
     * @param sql
     * @return
     */
    public static String replaceSqlCondition(String sql){
        // 将传入的sql语句中带带有号的单词替换为对应的去掉[!]号的单词的小写
        String[] sqlArray = sql.split(" ");
        for(int i = 0; i < sqlArray.length; i++) {
            if (sqlArray[i].contains("[!]")) {
                sqlArray[i] = sqlArray[i].replace("[!]", "");
            }
            if (sqlArray[i].equals("NotLike")) {
                sqlArray[i] = sqlArray[i].replace("NotLike", "not like");
            }
        }
        String result = "";
        for(int i = 0; i < sqlArray.length; i++) {
            result += sqlArray[i] + " ";
        }
        return result;
    }

    public static String transInCondition(String  filedName, List<String> codeList){
        return String.format("%s in ('%s')",filedName, String.join("','", codeList));
    }
    // 在两个时间之间，大于等于xx,小于xx
    public static String transBetweenCondition(String filedName, long startTime, long endTime){
        return String.format(" and %s >= %d and %s< %d",filedName, startTime, filedName,endTime);
    }
    // 测试
    public static void main(String[] args) {
        System.out.println(transBetweenCondition("XXX", 1234598L,13574985487L));
    }

}
