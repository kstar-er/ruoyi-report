package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    /**
     * 通用状态
     */
    SUCCESS(200, "ok"),
    UNKNOWN_ERROR(404, "未知异常"),
    PARAMETER_ERROR(405, "参数错误："),
    VERIFY_ERROR(406,"验证失败"),
    TIMEOUT_ERROR(407,"验证超时"),
    INFO(408, "提示"),
    IP_FREQUENT_VISITS(409,"请求失败,你访问太频繁了~"),
    INFO_TEMP(400, "提示"),

    /**
     * 特殊情况报错
     */
    DATA_NOT_EXIST(4001, "数据不存在")

    ;


    private Integer code;
    private String msg;

    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
