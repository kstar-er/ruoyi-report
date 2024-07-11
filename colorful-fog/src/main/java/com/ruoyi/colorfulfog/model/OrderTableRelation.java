package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_order_table_relation")
public class OrderTableRelation extends BaseClass {


    /**
     * 单据的名称
     */
    @TableField(value = "order_name")
    private String orderName;

    /**
     * 单据关联的表单
     */
    @TableField(value = "order_table")
    private String orderTable;
    /**
     * 单据关联的表单名称
     */
    @TableField(value = "order_table_name")
    private String orderTableName;
    /**
     * 外键
     */
    @TableField(value = "order_foreign")
    private String orderForeign;

    /**
     * 外键名称
     */
    @TableField(value = "order_foreign_name")
    private String orderForeignName;

    @TableField(value = "type")
    private String type;

    /**
     * 关联的主表
     */
    @TableField(value = "order_main_table")
    private String orderMainTable;

    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;
}
