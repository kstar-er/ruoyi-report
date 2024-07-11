package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import java.util.List;

@Data

public class CollectResultMainDto {
    List<FieldSelectDto> fieldSelectList;
    String status;
    String collectSchemeCode;
    String costTerm;
    String billType;
    String updateUserName;
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
}
