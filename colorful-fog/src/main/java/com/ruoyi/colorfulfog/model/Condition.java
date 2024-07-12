package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_condition")
public class Condition extends BaseClass {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    /**
     * 对应表名
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 生效条件(先生成完再存入,使用时直接读取拼接)
     */
    @TableField(value = "condition_field")
    private String conditionField;

    /**
     * 依赖的规则的CODE
     */
    @TableField(value = "scheme_code")
    private String schemeCode;

    /**
     * 关联主体
     */
    @TableField(value = "public_connect")
    private String publicConnect;

    /**
     * 生效条件名称
     */
    @TableField(value = "condition_name")
    private String conditionName;
}
