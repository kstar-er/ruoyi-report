package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.GetDataFromType;
import com.ruoyi.colorfulfog.constant.enums.ResutlTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.SchemeDetailParamEnum;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_scheme_detail")
public class SchemeDetail extends BaseClass {

    /**
     * 跟随主表外键
     */
    @TableField(value = "scheme_code")
    private String schemeCode;

    /**
     * 系数类型
     */
    @TableField(value = "type")
    private SchemeDetailParamEnum type;

    /**
     * 固定系数
     */
    @TableField(value = "fixed_coefficient")
    private String fixedCoefficient;
    /**
     * 表达式
     */
    @TableField(value = "expression")
    private String expression;

    /**
     * 依赖表的code
     */
    @TableField(value = "depend_code")
    private String dependCode;

    /**
     * 绑定的表的名称
     */
    @TableField(value = "order_name")
    private String orderName;

    /**
     * 绑定表的字段名称
     */
    @TableField(value = "order_field")
    private String orderField;

    /**
     * 单据里面表的名称
     */
    @TableField(value = "order_table")
    private String orderTable;

    /**
     * 计算结果的编码
     */
    @TableField(value = "result_code")
    private String resultCode;

    /**
     * 计算结果的名字
     */
    @TableField(value = "result_name")
    private String resultName;

    /**
     * 计算结果的类型，数字，小数，文本，日期
     */
    @TableField(value = "result_type")
    private ResutlTypeEnum resultType;

    /**
     * 汇总依据的字段
     */
    @TableField(value = "group_by_field")
    private String groupByField;

    /**
     * 汇总依据字段的名称
     */
    @TableField(value = "group_by_field_name")
    private String groupByFieldName;

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
     * 获取数据的来源
     */
    @TableField(value = "get_data_from")
    private GetDataFromType getDataFrom;

    @TableField(value = "group_by_table")
    private String groupByTable;

    /**
     * 只标记一行，当这个值为true时，生成的值在同一组中，只
     */
    @TableField(value = "one_row_tag")
    private Boolean oneRowTag;

    /**
     * 带有这个标记，说明这个字段需要参与到后续的计算中
     * 如果这个标记的字段没有拿到值的话， 视为计算失败，否则也视为计算成功
     */
    @TableField(value = "calculated_tag")
    private Boolean calculatedTag;

    @TableField(value = "hide_when_push")
    private Boolean hideWhenPush;

    @TableField(value = "hide_when_export")
    private  Boolean hideWhenExport;
    /**
     * 单行标记时，其他行的值
     */
    @TableField(value = "other_row_value")
    private String otherRowValue;
    /**
     * 小数点位数
     */
    @TableField(value = "`decimal`")
    private Integer decimal;
}
