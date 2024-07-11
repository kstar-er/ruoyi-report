package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddTableFieldDto {
    List<String> tableNameList;
    Integer dataSourceId;
}
