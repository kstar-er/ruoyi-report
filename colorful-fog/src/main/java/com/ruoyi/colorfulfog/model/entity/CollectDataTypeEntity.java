package com.ruoyi.colorfulfog.model.entity;

import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.ruoyi.colorfulfog.model.SchemeMain;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CollectDataTypeEntity{
    List<String> collectCodeList;

    // 存放主表的数据
    List<CollectResultMain> collectResultMainList;
    List<BillMain> billMainList;
    SchemeMain schemeMain;
    CollectSchemeMain collectSchemeMain;
    //获取数据类型，汇总计划，和一般方案
    CollectObjectEnum dataType;
}
