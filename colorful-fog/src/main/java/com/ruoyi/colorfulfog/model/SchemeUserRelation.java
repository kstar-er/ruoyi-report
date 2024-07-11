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
@TableName(value = "cwu_scheme_user_relation")
public class SchemeUserRelation extends BaseClass {

    @TableField(value = "user_id")
    private Integer userId;
    /**
     * 计划的code
     */
    @TableField(value = "scheme_code")
    private String schemeCode;

    /**
     * 档案中用户的code
     */
    @TableField(value = "archive_user_code")
    private String archiveUserCode;

    /**
     * 档案中的用户名称
     */
    @TableField(value = "archive_user_name")
    private String archiveUserName;
}
