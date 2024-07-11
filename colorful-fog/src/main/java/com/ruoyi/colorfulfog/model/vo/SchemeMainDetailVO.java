package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.Condition;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder

public class SchemeMainDetailVO {
    SchemeMain schemeMain;
    List<SchemeDetail> schemeDetailList;
    List<Condition> conditionList;
    List<SchemeUserRelation> schemeUserRelations;
}
