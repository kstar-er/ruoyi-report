package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class AddDataManualDto {
    /**
     * 所属计划code
     */
    @NotNull
    private String schemeCode;
    /**
     * 用户编码
     */
    @NotNull
    private String belongArchiveCode;
    /**
     * 用户名称
     */
    @NotNull
    private String belongArchiveName;

    /**
     * 汇总账单编码(选填）
     */
    private String collectResultCode;
    /**
     * 数据
     */
    @NotNull
    private Map<String,Object> data;
}
