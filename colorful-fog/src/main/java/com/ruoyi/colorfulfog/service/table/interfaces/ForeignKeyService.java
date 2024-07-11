package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.ForeignKey;

import java.util.List;
import java.util.Map;

public interface ForeignKeyService extends IService<ForeignKey>{

    Map<String,Map<String,ForeignKey>> getForeignKeyMap(List<Integer> idList);//表名->外键表名->ForeignKey

    boolean save(ForeignKey foreignKey);
    boolean saveBatch2(List<ForeignKey> foreignKey);
}
