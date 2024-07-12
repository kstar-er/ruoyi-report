package com.ruoyi.colorfulfog.model.entity;

import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CollectDataTypeEntity{
    List<String> collectCodeList;
    List<String> collecIdList;

    // 存放主表的数据
    List<CollectBillData> collectResultMainList;
    List<BillData> billMainList;
    SchemeMain schemeMain;
    CollectSchemeMain collectSchemeMain;
    //获取数据类型，汇总计划，和一般方案
    CollectObjectEnum dataType;
}
