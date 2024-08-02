package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum ResutlTypeEnum {
    NUMBER("数字"),
    STRING("字符串"),
    DATE("日期"),
    NUM("数字"),
    URL("链接");

    private String msg;
    ResutlTypeEnum(String msg) {
        this.msg = msg;
    }
}
