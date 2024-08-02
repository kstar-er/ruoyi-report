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
@TableName(value = "cwu_table_field_relation")
public class TableFieldRelation extends BaseClass {
    /**
     * 表名称
     */
    @TableField(value = "table_name")
    private String tableName;

    /**
     * 字段名称
     */
    @TableField(value = "field")
    private String field;

    /**
     * 字段类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 字段的中文含义
     */
    @TableField(value = "field_name")
    private String fieldName;
    /**
     * 标记当前字段是否未虚拟删除的标记，如果是的话，在进行数据查询的时候需要添加这个字段进行跳过
     */
    @TableField(value = "is_delete_flag")
    private Integer isDeleteFlag;
}
