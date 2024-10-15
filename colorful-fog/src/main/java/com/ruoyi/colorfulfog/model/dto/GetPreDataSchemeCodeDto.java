package com.ruoyi.colorfulfog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetPreDataSchemeCodeDto {
    private String costTerm;
    private String belongArchiveCode;
}
