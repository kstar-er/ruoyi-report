package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

/**
 * 枚举，年、月、周、天、小时、分钟
 * 记录每个的时间戳
 */
@Getter
public enum TimeUnitEnum {

    YEAR(31560000000L, "年",1),
    MONTH(2593000000L, "月",2),
    DAY(86400000L, "天",5),
    HOUR(3600000L, "小时",11),
    MINUTE(60000L, "分钟",12);
    private Long timeStamp;
    private String name;
    private Integer calendarNum;

    TimeUnitEnum(Long timeStamp, String name, Integer calendarNum) {
        this.timeStamp = timeStamp;
        this.name = name;
        this.calendarNum = calendarNum;
    }
}
