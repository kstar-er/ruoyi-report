package com.ruoyi.colorfulfog.model.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class BillResultDto {
    /**
     * 传resultCode和value一对参数做为查询的依据
     * 查询自定义字段的数据
     */
    Map<String,String> fieldSelectList;
    /**
     * 其他的作为主表的固定字段，直接查询
     */
    String status;
    String schemeCode;
    String costTerm;
    String billType;
    String batchCode;
    String updateUserName;
    Long createStartTime;
    Long createEndTime;
    Long updateStartTime;
    Long updateEndTime;
}
