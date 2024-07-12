package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;

import com.ruoyi.colorfulfog.constant.enums.GetDataFromType;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_depend_rule")
public class DependRule extends BaseClass {

    /**
     * 所属依赖
     */
    @TableField(value = "depend_code")
    private String dependCode;

    /**
     * 关联订单名称
     */
    @TableField(value = "order_name")
    private String orderName;

    /**
     * 关联表
     */
    @TableField(value = "order_table")
    private String orderTable;

    /**
     * 关联表字段
     */
    @TableField(value = "order_field")
    private String orderField;

    /**
     * 字段名称
     */
    @TableField(value = "order_field_name")
    private String orderFieldName;

    /**
     * 二次依赖表
     */
    @TableField(value = "second_depend_code")
    private String secondDependCode;

    /**
     * 依赖表的类型，范围或者是分类
     */
    @TableField(value = "depend_type")
    private DependTypeEnum dependType;

    /**
     * 从哪里获取数据
     */
    @TableField("get_data_from")
    private GetDataFromType getDataFrom;

    @TableField("scheme_name")
    private String schemeName;

    @TableField("scheme_code")
    private String schemeCode;


}
