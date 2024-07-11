package com.ruoyi.colorfulfog.model.vo;

import lombok.Builder;

@Builder
public class BillResultValueVO {
    /**
     * 字段类型
     */
    private String type;
    /**
     * 结果值
     */
    private String value;
    /**
     * 绑定字段的名称
     */
    private String resultName;
}
