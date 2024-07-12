package com.ruoyi.system.domain.enums;

import lombok.Getter;

/**
 * @author zwh
 * @date 2023/2/8 10:33
 */
@Getter
public enum ExpressionEnum {
    ALL("所有权限"),
    NONE("没有权限"),
    EQUAL("等于="),
    IN("sql的in表达式"),
    SQL_PLAIN("sql片段，普通语句。可实现任意复杂的sql。"),
    PARSER("运行时变量解析器"),

    ;
    private String msg;

    ExpressionEnum(String msg) {
        this.msg = msg;
    }
}
