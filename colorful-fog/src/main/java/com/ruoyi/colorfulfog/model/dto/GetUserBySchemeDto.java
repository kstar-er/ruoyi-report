package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GetUserBySchemeDto {
    @NotNull
    String schemeCode;
    @NotNull
    CollectDataTypeEnum collectDataTypeEnum;
}
