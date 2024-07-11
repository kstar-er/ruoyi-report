package com.ruoyi.colorfulfog.model.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.model.BaseClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_key_value")
@Accessors(chain = true)
public class KeyValue extends BaseClass {
    /**
     * 值
     */
    @TableField(value = "`key`")
    private String key;

    @TableField(value = "`value`")
    private String value;

    /**
     * 备注
     */
    @TableField(value = "comment")
    private String comment;



    public static KeyValueBuilder builder() {
        return new KeyValueBuilder();
    }
}