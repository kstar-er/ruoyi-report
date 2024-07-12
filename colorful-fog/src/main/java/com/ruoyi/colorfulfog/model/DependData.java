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
@TableName(value = "cwu_depend_data")
public class DependData extends BaseClass {
    /**
     * 依赖的主表code
     */
    @TableField(value = "depend_code")
    private String dependCode;

    /**
     * 范围的开始
     */
    @TableField(value = "range_start")
    private String rangeStart;

    /**
     * 范围的结束
     */
    @TableField(value = "range_end")
    private String rangeEnd;

    /**
     * 联合的key
     */
    @TableField(value = "`key`")
    private String key;

    /**
     * 最终确定的值
     */
    @TableField(value = "`value`")
    private String value;
}
