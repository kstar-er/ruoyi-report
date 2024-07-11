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
    * 账单无法生成原因汇总
    */
@ApiModel(value="com-example-colorfulfog-model-ErrReason")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "cwu_err_reason")
public class ErrReason extends BaseClass {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Integer id;

    /**
     * 依赖的主表code
     */
    @TableField(value = "depend_code")
    @ApiModelProperty(value="依赖的主表code")
    private String dependCode;

    /**
     * 联合的key
     */
    @TableField(value = "`key`")
    @ApiModelProperty(value="联合的key")
    private String key;

    /**
     * 最终确定的值
     */
    @TableField(value = "value")
    @ApiModelProperty(value="最终确定的值")
    private String value;

    /**
     * 是否已经处理
     */
    @TableField(value = "deal_flag")
    @ApiModelProperty(value="是否已经处理")
    private Integer dealFlag;

    /**
     * 错误原因:1、不在分类依赖表2、超出范围
     */
    @TableField(value = "reason")
    @ApiModelProperty(value="错误原因:1、不在分类依赖表2、超出范围")
    private String reason;


    @TableField(value = "version")
    @ApiModelProperty(value="")
    private Integer version;
}
