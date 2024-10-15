package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;
@Getter
public enum GroupTypeEnum {

    DYNAMIC_TIME("动态时间分组"),
    FIXED_TIME("固定时间分组"),
    STRING("字符串分组")
    ;

    private String name;

    GroupTypeEnum(String name) {
        this.name = name;
    }
}
