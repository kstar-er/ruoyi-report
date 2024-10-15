package com.ruoyi.colorfulfog.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReCreateByUserCode {
    // 传入用户编码，商家或者车队或者网点
    @NotNull
    List<String> userCodeList;

    @NotNull
    // 传入方案编码
    String schemeCode;
    // 批次号，先调用另外的接口获得批次号。如果用户不选择批次号，表示没有原有账单数据需要删除，不传值。直接生成新的账单
    List<String> batchCodeList;
}
