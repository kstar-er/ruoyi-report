package com.ruoyi.mybatis.helper.parser;

import lombok.Getter;

/**
 * @author zwh
 * @date 2023/2/8 10:33
 */
@Getter
public enum ParserNameEnum {
    CREATOR_ONLY("仅创建人可见"),
    ORG_ONLY("仅组织内可见"),
    SELF_WAREHOUSE_ONLY("仅可见自己所属仓库的"),
    SELF_MERCHANT_ONLY("仅可见商家自己的"),
    SELF_MERCHANT_AND_SON("商家及自己儿子可见"),
    BELONG_MERCHANT("可见自己下属商家"),
    FIELD_EQ_USER_ID("某字段等于用户的ID"),
    DISTRIBUTE_ORG_ONLY("仅允许分配的组织可见"),
    MERCHANT_MANAGER_BY_USER_AND_ORG("根据商家分配管理表控制组织或个人可见")
    ;
    private String msg;

    ParserNameEnum(String msg) {
        this.msg = msg;
    }
}
