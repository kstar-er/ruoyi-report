package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.ResutlTypeEnum;
import com.ruoyi.common.core.domain.BaseEntity;
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
public class CollectSchemeDetail extends BaseClass {

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
    @ApiModelProperty(value = "需要汇总的方案的字段的编码")
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

    @TableField(value = "hide_when_push")
    private Boolean hideWhenPush;
    /**
     * 表达式
     */
    @TableField(value = "expression")
    private String expression;
    /**
     * 分组字段的编码
     */
    @TableField(value = "group_by_field")
    private String groupByField;

    /**
     * 分组字段的名字
     */
    @TableField(value = "group_by_field_name")
    private String groupByFieldName;

    /**
     * 分组字段的值，多个值用,号分开
     */
    @TableField(value = "group_by_field_value")
    private String groupByFieldValue;

    /**
     * 计算顺序
     */
    @TableField(value = "calculate_order")
    private Integer calculateOrder;

    /**
     * 显示顺序
     */
    @TableField(value = "display_order")
    private Integer displayOrder;
    /**
     * 计算结果的类型，数字，链接，文本，日期
     */
    @TableField(value = "result_type")
    private ResutlTypeEnum resultType;

    @TableField(value = "`decimal`")
    private Integer decimal;
    public enum CollectTypeEnum{
        SUM("汇总"),
        ONE("只取一行"),
        EMPTY("空白字段"),
        /**
         * 根据条件判断，只有满足条件的才会汇总进来
         */
        CONDITION_SUM("条件汇总"),
        EQUATION("自定义条件计算")

        ;
        String msg;
        CollectTypeEnum(String msg){
            this.msg = msg;
        }
    }

}
