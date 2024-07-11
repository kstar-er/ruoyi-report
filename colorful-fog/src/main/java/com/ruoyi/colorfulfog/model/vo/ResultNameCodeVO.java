package com.ruoyi.colorfulfog.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResultNameCodeVO {
    String resultName;
    String resultCode;
    String resultType;
    Integer displayOrder;

    public ResultNameCodeVO(String resultName, String resultCode, String resultType){
        this.resultName = resultName;
        this.resultCode = resultCode;
        this.resultType = resultType;

    }

}
