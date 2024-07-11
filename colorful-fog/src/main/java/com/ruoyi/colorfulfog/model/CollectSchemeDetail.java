package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 汇总计划明细表
 */
@ApiModel(description = "汇总计划明细表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_collect_scheme_detail")
public class CollectSchemeDetail extends BaseClass{

    /**
     * 汇总方案的编码
     */
    @TableField(value = "collect_scheme_code")
    @ApiModelProperty(value = "汇总方案的编码")
    private String collectSchemeCode;

    /**
     * 汇总方式类型,sum,或者count、first(所有都是一样的，取第一个)
     */
    @TableField(value = "collect_type")
    @ApiModelProperty(value = "汇总方式类型,sum,或者count、first(所有都是一样的，取第一个)")
    private CollectTypeEnum collectType;

    /**
     * 方案的编码
     */
    @TableField(value = "scheme_code")
    @ApiModelProperty(value = "方案的编码")
    private String schemeCode;

    /**
     * 方案的名称
     */
    @TableField(value = "scheme_name")
    @ApiModelProperty(value = "方案的名称")
    private String schemeName;

    /**
     * 需要汇总的方案的编码
     */
    @TableField(value = "scheme_result_code")
    @ApiModelProperty(value = "需要汇总的方案的编码")
    private String schemeResultCode;

    /**
     * 需要汇总的方案的名称
     */
    @TableField(value = "scheme_result_name")
    @ApiModelProperty(value = "需要汇总的方案的名称")
    private String schemeResultName;

    /**
     * 汇总后的字段名称
     */
    @TableField(value = "collect_result_name")
    @ApiModelProperty(value = "汇总后的字段名称")
    private String collectResultName;

    /**
     * 外键，绑定汇总结果
     */
    @TableField(value = "collect_result_code")
    @ApiModelProperty(value = "外键，绑定汇总结果")
    private String collectResultCode;

    /**
     * 时间字段
     */
    @TableField(value = "time_field")
    @ApiModelProperty(value = "时间字段")
    private String timeField;
    /**
     * 时间字段名称
     */
    @TableField(value = "time_field_name")
    private String timeFieldName;

    public enum CollectTypeEnum{
        SUM,
        ONE,
    }

}
