package com.ruoyi.colorfulfog.constant.enums;

public enum SelectTypeEnum {

    CALC("计算模式"),
    PUSH("推送模式"),
    EXPORT("导出模式"),
    ;

    private String msg;

    SelectTypeEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

