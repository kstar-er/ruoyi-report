package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

@Data
public class SelectUserDataDto {
    /**
     * 查询用户数据的表
     */
    private String userDataTable;
    /**
     * 查询用户数据的字段
     */
    private String userDataCodeField;
    /**
     * 查询用户数据的展示的名称
     */
    private String userDataNameField;
    /**
     * 选择哪个数据库
     */
    private Integer dataSourceId;
    /**
     * 查询条件
     */
    private String query;


}
