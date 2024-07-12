package com.ruoyi.colorfulfog.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 汇总计划向下查询源数据dto
 */
@Data
@Builder
public class CollectResultDto {
    Long timeStart;
    Long timeEnd;
    String timeFieldName;
    String schemeCode;
    String resultCode;
    String timeFieldCode;
    String belongArchiveCode;
    String costTerm;
    String collectSchemeCode;
}
