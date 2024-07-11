package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import lombok.Data;

import java.util.List;

@Data
public class OrderTableRelationVO extends OrderTableRelation {
    List<ForeignKey> foreignKeyList;

}
