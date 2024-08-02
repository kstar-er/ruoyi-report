package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum BillCheckStatusEnum {

    // 存在错误数据
    HAVE_ERROR(0, "存在错误数据"),
    // 正常账单
    NORMAL(1, "正常账单"),

    HAVE_DELETED(2, "无效账单"),
    ;
    private final int code;
    private final String msg;

    BillCheckStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
