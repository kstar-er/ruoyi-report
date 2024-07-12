package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum AuditStatusEnum {
    WAIT_START_AUDIT(0,"等待发起审核"),
    WAIT_AUDIT(1, "待审核"),
    PASS_AUDIT(2, "审核通过"),
    REFUSE_AUDIT(3, "审核拒绝");
    private final Integer code;
    private final String message;
    AuditStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
