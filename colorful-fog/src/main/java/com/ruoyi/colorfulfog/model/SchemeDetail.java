package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.GetDataFromType;
import com.ruoyi.colorfulfog.constant.enums.SchemeDetailParamEnum;
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
    private String resultType;

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

}
