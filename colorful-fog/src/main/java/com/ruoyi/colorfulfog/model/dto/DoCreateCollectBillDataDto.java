package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoCreateCollectBillDataDto {
    Map<String, List<BaseData>> baseDataMap;
    CollectSchemeMain collectSchemeMain;
    List<CollectSchemeDetail> collectSchemeDetails;
    Map<String,List<BaseData>> belongBillMainListMap;
    List<BaseData> billMainList;
    // 是否保存，默认保存
    Boolean isSave;


}
