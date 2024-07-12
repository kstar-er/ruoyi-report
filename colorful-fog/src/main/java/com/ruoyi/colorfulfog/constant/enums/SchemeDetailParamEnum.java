package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

/**
 * 计划明细枚举
 */
@Getter
public enum SchemeDetailParamEnum {
    FIXED("固定参数"),
    ORDER_DATA("单据数据"),
    DEPEND("依赖表"),
    EQUATION("公式"),
    /**
     * 得到结果的方法和循环标记的相同，但是单独标记的话使用一次之后，会将值消耗掉，重设为0
     */
    SINGLE_TAG("单独标记"),
    /**
     * 循环判断标记：1、选择某相同的值作为循环的范围，
     * 2、 循环的匹配方式有三种anyMatch,noneMatch,allMatch
     * 3、 选择某列的值作为循环判断的值
     */
    FOR_MUL_TAG("循环判断标记"),
    /**
     * 使用正则表达式自己获得对应的值
     */
    REGEX("正则表达"),
    OTHER("其他"),
    /**
     * sum类型的计算方式：1、建立临时的map<key,value>,key的值是对应schemeDetail的code+groupBy的值,
     * 在第一行遇到的时候，对所有数据进行遍历，并按照对应的
     */
    SUM("根据某值汇总同类项"),
    /**
     * 与SUM的计算方式相同，
     * 区别在于SUM是针对某列的值进行汇总，而COUNT是针对某列的值进行计数
     */
    COUNT("计数"),

    /**
     * 与上面的两个计算方式相同。
     * 区别在于，MAX和MIN是针对某列的值进行比较，并返回最大值和最小值
     */
    MAX("取最大值"),
    MIN("取最小值"),
    ;

    private String msg;

    SchemeDetailParamEnum(String msg) {
        this.msg = msg;
    }
}
