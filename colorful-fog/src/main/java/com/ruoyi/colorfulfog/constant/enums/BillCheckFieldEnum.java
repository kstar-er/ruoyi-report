package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum BillCheckFieldEnum {

    BILL_PAY_STATUS("bill_pay_status","应付账单状态字段"),

    BILL_COLLECT_STATUS("bill_collect_status","应收账单状态字段"),
    ;
    private final String field;
    private final String msg;

    BillCheckFieldEnum(String field, String msg) {
        this.field = field;
        this.msg = msg;
    }
}
