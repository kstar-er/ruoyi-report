package com.ruoyi.colorfulfog.constant.enums;

import lombok.Getter;

import static com.ruoyi.colorfulfog.constant.SysConstant.KeySet.*;
import static com.ruoyi.colorfulfog.constant.enums.IdPrefixEnum.*;

@Getter
public enum IdTypeEnum {

    COST_BILL(COST_BILL_NUM, COB),//商家最终结算账单
    RESULT_FIELD(RESULT_FIELD_NUM,RS), // 账单结果的字段编码
    SCHEME_CODE(SCHEME_CODE_NUM,SHE) ,// 账单计划编码
    DEPEND_CODE(DEPEND_CODE_NUM,DEP), // 依赖表编码
    COLLECT_RESULT_CODE(COLLECT_RESULT_CODE_NUM, CRS),// 汇总字段编码
    COLLECT_DATA_CODE(COLLECT_DATA_CODE_NUM, CDA),// 汇总数据结果编码
    COLLECT_SCHEME_CODE(COLLECT_SCHEME_CODE_NUM,CSH),// 汇总计划编码
    RESULT_BATCH_CODE(RESULT_BATCH_CODE_NUM,RBS) //账单批次号
    ;


    //存放于key-value数据库的key
    private String key;
    //生成编码时的前缀
    private IdPrefixEnum prefix;

    IdTypeEnum(String key, IdPrefixEnum prefix) {
        this.prefix = prefix;
        this.key = key;
    }
}
