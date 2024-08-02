package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

@Data
public class RemoveKeyDto {
    /**
     * 关联表的外键
     */
    private String relTableForeignKey;
    /**
     * 当前表名
     */
    private String tableName;
}
