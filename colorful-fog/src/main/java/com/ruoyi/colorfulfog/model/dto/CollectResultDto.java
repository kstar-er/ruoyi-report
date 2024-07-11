package com.ruoyi.colorfulfog.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CollectResultDto {
    Long timeStart;
    Long timeEnd;
    String timeFieldName;
    String schemeCode;
    String resultCode;
    String timeFieldCode;
}
