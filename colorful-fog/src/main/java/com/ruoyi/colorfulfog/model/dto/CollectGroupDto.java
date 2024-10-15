package com.ruoyi.colorfulfog.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.colorfulfog.constant.enums.GroupTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.TimeUnitEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectGroupDto {


    /**
     * 以哪个字段作为分组的字段的编码
     */
    @TableField(value = "group_code")
    @ApiModelProperty(value="以哪个字段作为分组的字段的编码")
    private String groupCode;

    /**
     * 以哪个字段作为分组的字段的名词
     */
    @TableField(value = "group_name")
    @ApiModelProperty(value="以哪个字段作为分组的字段的名称")
    private String groupName;

    /**
     * 分组顺序，多次分组的时候使用
     */
    @TableField(value = "sort")
    @ApiModelProperty(value="分组顺序，多次分组的时候使用")
    private Integer sort;

    /**
     * 启用分组的类型，时间范围分组，字段分组
     */
    @TableField(value = "group_type")
    @ApiModelProperty(value="启用分组的类型，时间范围分组，字段分组")
    private GroupTypeEnum groupType;

    /**
     * 时间单位
     */
    @TableField(value = "time_unit")
    @ApiModelProperty(value="时间单位，年、月、日")
    private TimeUnitEnum timeUnit;

    /**
     * 时间范围开始
     */
    @TableField(value = "time_start")
    @ApiModelProperty(value="时间范围开始公式")
    private Integer timeStart;
    /**
     * 时间范围结束
     */
    @TableField(value = "time_end")
    @ApiModelProperty(value="时间范围结束公式")
    private Integer timeEnd;
    /**
     * 时间分组范围步长
     */
    @TableField(value = "time_step")
    @ApiModelProperty(value="时间分组范围步长")
    private Integer timeStep;

}
