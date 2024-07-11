package com.ruoyi.colorfulfog.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手动更新账单的日志
 */
@ApiModel(description="手动更新账单的日志")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_update_bill_log")
public class UpdateBillLog extends BaseClass{

    /**
     * 原值
     */
    @TableField(value = "origin_value")
    @ApiModelProperty(value="原值")
    private String originValue;

    /**
     * 更新后的值
     */
    @TableField(value = "after_value")
    @ApiModelProperty(value="更新后的值")
    private String afterValue;

    /**
     * 所属哪条记录
     */
    @TableField(value = "bill_code")
    @ApiModelProperty(value="所属哪条记录")
    private String billCode;

    /**
     * 绑定字段的名称
     */
    @TableField(value = "result_name")
    @ApiModelProperty(value="绑定字段的名称")
    private String resultName;

    /**
     * 操作的类型：collect和bill
     */
    @TableField(value = "result_type")
    @ApiModelProperty(value="操作的类型：collect和bill")
    private CollectObjectEnum resultType;


}
