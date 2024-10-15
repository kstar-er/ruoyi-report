package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GetCostTermDto {
    @NotNull
    String collectSchemeCode;
    @NotNull
    List<String> userCodeList;
}
