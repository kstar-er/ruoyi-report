package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.ExecutionTimeUnit;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_scheme_main")
public class SchemeMain extends BaseClass {
    @TableField(value = "name")
    private String name;

    @TableField(value = "scheme_code")
    private String schemeCode;

    /**
     * 粒度，具体到哪个表中的字段为最小生成账单单位
     */
    @TableField(value = "granularity")
    private String granularity;

    /**
     * 应付还是应收
     */
    @TableField(value = "bill_type")
    private String billType;

    /**
     * 账单归属用户关联表
     */
    @TableField(value = "belong_rel_table_name")
    private String belongRelTableName;

    /**
     * 账单所属用户绑定字段
     */
    @TableField(value = "belong_rel_table_field")
    private String belongRelTableField;

    @TableField(value = "data_source_id")
    private Integer dataSourceId;

    @TableField(value = "data_source_name")
    private String dataSourceName;
    /**
     * 分类
     */
    @TableField(value = "sort")
    private String sort;


    @TableField(value = "user_data_table")
    private String userDataTable;

    @TableField(value = "user_data_code_field")
    private String userDataCodeField;

    @TableField(value = "user_data_name_field")
    private String userDataNameField;

    /**
     * 执行时间单位，每天执行，还是每月执行一次
     */
    @TableField(value = "execution_unit")
    private ExecutionTimeUnit executionUnit;
    /**
     * 每日执行的话是0-24，每月执行的话是0-31
     */
    @TableField(value = "execution_time")
    private Integer executionTime;

    @TableField(value = "time_formula_start")
    @ApiModelProperty(value = "时间公式开始")
    private String timeFormulaStart;

    @TableField(value = "time_formula_end")
    @ApiModelProperty(value = "时间公式结束")
    private String timeFormulaEnd;

    @TableField(value = "time_field_table")
    @ApiModelProperty(value = "时间字段所在表")
    private String timeFieldTable;

    @TableField(value = "time_field")
    @ApiModelProperty(value = "时间字段")
    private String timeField;

    @TableField(value = "time_field_result_code")
    @ApiModelProperty(value = "时间字段对应编码")
    private String timeFieldResultCode;

    @TableField(value = "time_field_name")
    @ApiModelProperty(value = "时间字段名称")
    private String timeFieldName;



}
