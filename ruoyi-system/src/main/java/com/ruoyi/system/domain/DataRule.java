package com.ruoyi.system.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.system.domain.enums.ExpressionEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("sys_data_rule")
@ExcelIgnoreUnannotated
public class DataRule extends BaseEntity {
    /**
     * 所属职位编码
     */
    @TableField(value = "job_id")
    private Long jobId;

    /**
     * 表名
     */
    @TableField(value = "rule_table")
    private String ruleTable;

    /**
     * 组别编码标识
     */
    @TableField(value = "group_code")
    private String groupCode;

    /**
     * 表的字段名
     */
    @TableField(value = "rule_field")
    private String ruleField;

    /**
     * 规则表达式
     */
    @TableField(value = "expression")
    private ExpressionEnum expression;

    /**
     * 规则表达式
     */
    @TableField(value = "expression_value")
    private String expressionValue;

    //需要进行权限过滤的表枚举

}
