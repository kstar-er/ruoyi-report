package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
public class BillResultDto {
    /**
     * 传resultCode和value一对参数做为查询的依据
     * 查询自定义字段的数据
     */
    Map<String,String> fieldSelectList;


    /**
     * 其他的作为主表的固定字段，直接查询
     */
    String belongArchiveName;
    String schemeCode;
    String belongArchiveCode;
    String status;
    String collectSchemeCode;
    String costTerm;
    String billType;
    String batchCode;
    String billCode;
    String updateUserName;
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
    Boolean havePreData;
}
