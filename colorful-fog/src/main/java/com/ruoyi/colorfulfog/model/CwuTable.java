package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 业务表 gen_table
 *
 * @author Lion Li
 */

@Data
public class CwuTable {

    /**
     * 编号
     */
    @TableId(value = "table_id")
    private Long tableId;

    /**
     * 表名称
     */
    @NotBlank(message = "表名称不能为空")
    private String tableName;

    /**
     * 表描述
     */
    @NotBlank(message = "表描述不能为空")
    private String tableComment;

    /**
     * 主键信息
     */
    @TableField(exist = false)
    private CwuTableColumn pkColumn;

    /**
     * 子表信息
     */
    @TableField(exist = false)
    private CwuTable subTable;

    /**
     * 表列信息
     */
    @Valid
    @TableField(exist = false)
    private List<CwuTableColumn> columns;


}
