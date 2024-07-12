package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.OrderTableRelation;

import java.util.List;
import java.util.Map;

public interface ForeignKeyService extends IService<ForeignKey>{

    Map<String,Map<String,ForeignKey>> getForeignKeyMap(List<Long> idList);//表名->外键表名->ForeignKey
    void  deleteByOrderTableId(List<OrderTableRelation> orderTableRelations);
    boolean save(ForeignKey foreignKey);
    boolean saveBatch2(List<ForeignKey> foreignKey);
}
