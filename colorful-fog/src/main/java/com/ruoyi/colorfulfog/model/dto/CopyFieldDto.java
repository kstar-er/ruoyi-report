package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

@Data
public class CopyFieldDto {
    /**
     * 来源表，选择的表字段
     */
    String sourceSchemeCode;
    /**
     * 目标表，当前的表字段
     */
    String targetSchemeCode;
}
