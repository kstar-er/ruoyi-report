package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.constant.enums.ResutlTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
public class ResultNameCodeVO {
    String resultName;
    String resultCode;
    ResutlTypeEnum resultType;
    Integer displayOrder;
    Integer decimal;

    public ResultNameCodeVO(String resultName, String resultCode, ResutlTypeEnum resultType,Integer displayOrder,Integer decimal){
        this.resultName = resultName;
        this.resultCode = resultCode;
        this.resultType = resultType;
        this.displayOrder = displayOrder;
        if (decimal == null){
            decimal = 2;
        }
        this.decimal = decimal;
    }

}
