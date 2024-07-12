package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.*;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_depend_main")
public class DependMain  extends BaseClass {
    @TableField(value = "depend_code")
    private String dependCode;

    @TableField(value = "name", condition = SqlCondition.LIKE)
    private String name;

    /**
     * 依赖表的类型，范围或者是分类
     */
    @TableField(value = "depend_type")
    private DependTypeEnum dependType;

    /**
     * 是否失效0: 生效 1: 失效
     */
    @TableField(value = "is_lose_efficacy")
    private Boolean isLoseEfficacy;

}
