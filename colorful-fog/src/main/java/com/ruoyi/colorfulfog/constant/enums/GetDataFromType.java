package com.ruoyi.colorfulfog.constant.enums;

public enum GetDataFromType {
    SCHEME("计划中获取"),
    TABLE("源数据表格中获取"),
;
    private String msg;

    GetDataFromType(String msg) {
        this.msg = msg;
    }
}
