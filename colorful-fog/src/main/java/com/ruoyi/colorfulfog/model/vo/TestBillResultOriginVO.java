package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

// 测试账单数据的原始VO数据
@Data
@NoArgsConstructor
public class TestBillResultOriginVO {
    List<BillMain> billMainList;
    List<BillResult> billResultList;
    List<BillData> billDataList;
    List<Map<String, Map<String, Object>>> originData;
}
