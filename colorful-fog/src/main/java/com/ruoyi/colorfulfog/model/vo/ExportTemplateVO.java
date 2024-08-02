package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.constant.enums.ResutlTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportTemplateVO {
    private String resultCode;
    private String resultName;
    private ResutlTypeEnum resultType;
    /**
     * 是否必填，默认false不用填
     */
    private Boolean isRequired;

}
