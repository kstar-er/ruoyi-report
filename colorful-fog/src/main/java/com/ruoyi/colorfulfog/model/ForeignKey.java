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
public class ForeignKey {
    @TableId(value = "id", type = IdType.INPUT)
    @ApiModelProperty(value="")
    private Integer id;

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
    private Integer tableId;

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
    private Integer foreignTableId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private Long updateTime;

    /**
     * 更新者id
     */
    @TableField(value = "update_user")
    @ApiModelProperty(value="更新者id")
    private Integer updateUser;

    /**
     * 创建者id
     */
    @TableField(value = "create_user")
    @ApiModelProperty(value="创建者id")
    private Integer createUser;

    /**
     * 创建的组织id
     */
    @TableField(value = "create_org")
    @ApiModelProperty(value="创建的组织id")
    private Integer createOrg;

    /**
     * 更新的组织id
     */
    @TableField(value = "update_org")
    @ApiModelProperty(value="更新的组织id")
    private Integer updateOrg;

    /**
     * 1删除0有效
     */
    @TableField(value = "is_delete")
    @ApiModelProperty(value="1删除0有效")
    private Integer isDelete;
}