package com.ruoyi.colorfulfog.model.dto;

import com.ruoyi.colorfulfog.model.SchemeUserRelation;
import lombok.Data;

import java.util.List;

/**
 * 新建一个dto,无论怎样需要传入主表的code，做清空操作的时候不会找不到数据
 */
@Data
public class SchemeUserRelationSaveDto {
    String schemeCode;
    List<SchemeUserRelation> entityList;
}
