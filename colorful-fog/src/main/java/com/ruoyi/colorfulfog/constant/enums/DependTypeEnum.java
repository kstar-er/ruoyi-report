package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum DependTypeEnum {

    SORT("分类依赖"),
    RANGE("范围依赖"),
    DEPEND("二次依赖"),
    OTHER("其他")
            ;

    private String msg;

    DependTypeEnum(String msg) {
        this.msg = msg;
    }
}
