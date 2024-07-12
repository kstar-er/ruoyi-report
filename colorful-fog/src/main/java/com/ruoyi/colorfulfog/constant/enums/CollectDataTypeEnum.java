package com.ruoyi.colorfulfog.constant.enums;

import lombok.Data;

public enum CollectDataTypeEnum {

    SCHEME("一般计划"),
    COLLECT("汇总计划"),
    ;
    String msg;
    CollectDataTypeEnum(String msg){
        this.msg = msg;
    }
}
