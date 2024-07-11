package com.ruoyi.colorfulfog.constant.enums;

/**
 * @author zwh
 * @date 4/21/2023 10:53 AM
 */
public enum IdPrefixEnum {

    COB("商家最终结算账单"),
    RS("结算字段编码"),
    SHE("计划表编码"),
    DEP("依赖表编码"),

    CRS("汇总数据字段编码"),
    CDA("汇总数据结果编码"),

    CSH("汇总计划编码"),
    RBS("账单批次号")
    ;


    private String msg;

    IdPrefixEnum(String msg) {
        this.msg = msg;
    }
}
