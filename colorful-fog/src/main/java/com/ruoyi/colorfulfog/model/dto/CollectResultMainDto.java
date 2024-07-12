package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data

public class CollectResultMainDto {
    Map<String,String> fieldSelectList;
    String belongArchiveName;

    String belongArchiveCode;
    String status;
    String collectSchemeCode;
    String costTerm;
    String billType;
    String auditStatus;
    String updateUserName;
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
}
