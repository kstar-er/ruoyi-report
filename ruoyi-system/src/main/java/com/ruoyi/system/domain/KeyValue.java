package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_key_value")
@Accessors(chain = true)
public class KeyValue {
    /**
     * 值
     */
    @TableId(value = "to_key",type = IdType.INPUT)
    private String toKey;

    @TableField(value = "to_value")
    private String toValue;

    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
