package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外键关联表
 */
@ApiModel(description="外键关联表")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_foreign_key")
public class ForeignKey extends BaseClass {

    /**
     * 表名
     */
    @TableField(value = "`table_name`")
    @ApiModelProperty(value="表名")
    private String tableName;

    /**
     * 对应表的id
     */
    @TableField(value = "table_id")
    @ApiModelProperty(value="对应表的id")
    private Long tableId;

    /**
     * 外键
     */
    @TableField(value = "foreign_key")
    @ApiModelProperty(value="外键")
    private String foreignKey;

    @TableField(value = "rel_table_foreign_key")
    @ApiModelProperty(value="外键")
    private String relTableForeignKey;


    /**
     * 关联表
     */
    @TableField(value = "foreign_table_name")
    @ApiModelProperty(value="关联表")
    private String foreignTableName;

    /**
     * 外键关联表的id
     */
    @TableField(value = "foreign_table_id")
    @ApiModelProperty(value="外键关联表的id")
    private Long foreignTableId;

}
