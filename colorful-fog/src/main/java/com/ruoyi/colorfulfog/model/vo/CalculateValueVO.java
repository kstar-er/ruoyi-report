package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillResult;
import lombok.Builder;
import lombok.Data;

/**
 * 计算数据值的中间返回类
 */
@Data
@Builder
public class CalculateValueVO {

    String value;
    BillResult.CalculateStatusEnum calculateStatusEnum;
}
