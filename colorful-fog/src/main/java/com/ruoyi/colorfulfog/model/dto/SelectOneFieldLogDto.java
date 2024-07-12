package com.ruoyi.colorfulfog.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectOneFieldLogDto {
    private String billCode;

    /**
     * 绑定字段的名称
     */
    @ApiModelProperty(value="绑定字段的名称")
    private String resultName;

    @ApiModelProperty(value="绑定字段的名称")
    private String resultCode;



}
