package com.ruoyi.colorfulfog.model.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class ExpressionMatchEntity {
    MatchType matchType;

    List<String> matchData;

    String successResponse;

    String failResponse;

    /**
     * 解析表达式
     * @param expression
     */
    public ExpressionMatchEntity(String expression){
        // 对表达式进行解析
        // 正则表达式，获取"("之前的所有内容
        String type = expression.split("\\(")[0];
        // 将type转换为matchType
        matchType = MatchType.valueOf(type.toUpperCase());
        // 正则表达式获取()之间的内容
        String matchDataStr = expression.split("\\(")[1].split("\\)")[0];
        matchData = Arrays.asList(matchDataStr.split(","));
        // 正则表达式获取"?"和：之间的内容之后的内容
        successResponse = expression.split("\\?")[1].split(":")[0];
        failResponse = expression.split(":")[1];
    }

    /**
     * 传入源数据进行匹配解析，将解析的结果返回
     */

    public String match(List<String> sourceData){
        switch (matchType) {
            case ANYMATCH:
                // 所有源数据的元素，任意一条落在匹配元素中的任意一条
                if ( sourceData.stream().anyMatch(originData-> matchData.stream().anyMatch(originData::equals))){
                    return successResponse;
                }else {
                    return failResponse;
                }
            case ALLMATCH:
                // 所有源数据的元素，都落在匹配元素中的任意一条
                if ( sourceData.stream().allMatch(originData-> matchData.stream().anyMatch(originData::equals))){
                    return successResponse;
                }else {
                    return failResponse;
                }
            case NONEMATCH:
                // 所有源数据元素，都不落在匹配元素中的任意一条
                if ( sourceData.stream().noneMatch(originData-> matchData.stream().anyMatch(originData::equals))){
                    return successResponse;
                }else {
                    return failResponse;
                }
        }
        return null;
    }

    public enum MatchType {
        ANYMATCH,
        NONEMATCH,
        ALLMATCH;
    }

    // 测试

    public static void main(String[] args) {

        ExpressionMatchEntity entity = new ExpressionMatchEntity("AllMatch(26GW,35GW,50GW,50LW,50TW)?是:否");
        System.out.println(entity.getMatchType());
        System.out.println(entity.getMatchData());
        List<String> strings = new ArrayList<>();
        strings.add("26GWl");
        strings.add("35LGW");

    }
}
