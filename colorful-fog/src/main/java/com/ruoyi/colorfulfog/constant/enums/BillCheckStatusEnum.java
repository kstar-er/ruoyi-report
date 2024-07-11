package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum BillCheckStatusEnum {
    // 不可生成账单
    CAN_NOT_GENERATE(0, "不可生成账单"),

    // 等待生成账单
    WAIT_GENERATE(1, "等待生成账单"),

    // 已生成
    HAVE_GENERATED(2, "已生成账单"),

    // 等待修改账单
    WAIT_MODIFY(3, "等待修改账单"),

    // 等待删除账单
    WAIT_DELETE(4, "等待删除账单"),

    // 已删除账单
    HAVE_DELETED(5, "已删除账单"),
    ;
    private final int code;
    private final String msg;

    BillCheckStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
