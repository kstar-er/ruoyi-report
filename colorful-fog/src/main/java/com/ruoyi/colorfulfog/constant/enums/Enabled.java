package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

@Getter
public enum Enabled {
    YES(1),
    NO(0);
    private final Integer value;
    Enabled(Integer value) {
        this.value = value;
    }

    public Boolean exist(Integer value) {
        return this.getValue().equals(value);
    }
}
