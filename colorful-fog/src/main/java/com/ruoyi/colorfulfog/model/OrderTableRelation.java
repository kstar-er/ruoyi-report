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
@TableName(value = "cwu_order_table_relation")
public class OrderTableRelation extends BaseClass {


    /**
     * 单据的名称,标识当前表属于哪个单据，一个单据可能有多个数据库表
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

    @TableField(value = "type")
    private String type;
    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 数据库id
     */
    @TableField(value = "data_source_id")
    private Integer dataSourceId;

    /**
     * 数据库
     */
    @TableField(value = "data_source")
    private String dataSource;
    /**
     * 数据库中文名
     */
    @TableField(value = "data_source_Name")
    private String dataSourceName;
}
