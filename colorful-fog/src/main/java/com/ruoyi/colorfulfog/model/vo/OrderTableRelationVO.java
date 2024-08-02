package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class OrderTableRelationVO extends OrderTableRelation {
    List<ForeignKey> foreignKeyList;
    /**
     * 自己下面有哪些外键的list
     */
    List<String> fieldList;
}