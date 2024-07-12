package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总计划主表
 */
@ApiModel(description = "汇总计划主表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_collect_scheme_main")
public class CollectSchemeMain extends BaseClass {
    /**
     * 汇总计划的名称
     */
    @TableField(value = "collect_scheme_name")
    @ApiModelProperty(value = "汇总计划的名称")
    private String collectSchemeName;

    /**
     * 汇总方案的编码
     */
    @TableField(value = "collect_scheme_code")
    @ApiModelProperty(value = "汇总方案的编码")
    private String collectSchemeCode;

    /**
     * 汇总方案的描述
     */
    @TableField(value = "collect_scheme_desc")
    @ApiModelProperty(value = "汇总方案的描述")
    private String collectSchemeDesc;

    @TableField(value = "time_formula_start")
    @ApiModelProperty(value = "时间公式开始")
    private String timeFormulaStart;

    @TableField(exist = false)
    private Long timeStart;

    /**
     * 数据库中不存在该字段
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "时间公式结束")
    private Long timeEnd;

    @TableField(value = "time_formula_end")
    @ApiModelProperty(value = "时间公式结束")
    private String timeFormulaEnd;

    @TableField(value = "cost_term")
    @ApiModelProperty(value = "账期")
    private String costTermFormula;

    @TableField(exist = false)
    private String costTerm;

    @TableField(value = "collect_object")
    @ApiModelProperty(value = "收集对象：账单或者汇总单")
    private CollectObjectEnum collectObject;

    /**
     * 账单所属，收集对象是汇总表的时候需要手动填
     */
    @TableField(value = "belong_archive_name")
    private String belongArchiveName;

    @TableField(value = "belong_code_field")
    private String belongCodeField;


    @TableField(value = "belong_name_field")
    private String belongNameField;

    @TableField(value = "bill_type")
    @ApiModelProperty(value = "账单类型，应收or应付")
    private String billType;



}
