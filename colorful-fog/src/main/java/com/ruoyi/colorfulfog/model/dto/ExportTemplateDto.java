package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ExportTemplateDto {
    /**
     * 方案编码
     */
    @NotNull(message = "schemeCode不能为空")
    private String schemeCode;

}
