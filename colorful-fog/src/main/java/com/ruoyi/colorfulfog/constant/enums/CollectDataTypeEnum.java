package com.ruoyi.colorfulfog.constant.enums;

public enum CollectDataTypeEnum {

    SCHEME("一般计划"),
    COLLECT("汇总计划"),
    ;
    String msg;
    CollectDataTypeEnum(String msg){
        this.msg = msg;
    }
}
