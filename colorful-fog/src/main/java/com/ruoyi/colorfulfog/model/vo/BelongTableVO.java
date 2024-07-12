package com.ruoyi.colorfulfog.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 所属用户的表名称
 */
@Data
@Builder
public class BelongTableVO {

    private String tableName;
    private String table;
}
