package com.ruoyi.colorfulfog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BindUserDto {
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
     * 批量传入用户的名称，根据userDataNameField字段获得userDataCode;
     */
    List<String> userNameList;
    /**
     * 绑定的计划的code
     */
    private String schemeCode;

}
