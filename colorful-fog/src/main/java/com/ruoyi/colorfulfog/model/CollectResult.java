package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总数据明细表
 */
@ApiModel(description = "汇总数据明细表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_collect_result")
public class CollectResult {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 绑定的字段的id
     */
    @TableField(value = "result_code")
    @ApiModelProperty(value = "绑定的字段的id")
    private String resultCode;

    /**
     * 结果值
     */
    @TableField(value = "`value`")
    @ApiModelProperty(value = "结果值")
    private String value;

    /**
     * 所属哪条记录
     */
    @TableField(value = "bill_code")
    @ApiModelProperty(value = "所属哪条记录")
    private String billCode;

    /**
     * 绑定字段的名称
     */
    @TableField(value = "result_name")
    @ApiModelProperty(value = "绑定字段的名称")
    private String resultName;

    /**
     * 所属计划的id
     */
    @TableField(value = "scheme_code")
    @ApiModelProperty(value = "所属计划的id")
    private String schemeCode;

    /**
     * 字段的类型
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value = "汇总字段的类型")
    private CollectSchemeDetail.CollectTypeEnum type;

    /**
     * 字段结果类型
     */
    @TableField(value = "result_type")
    private String resultType;

}