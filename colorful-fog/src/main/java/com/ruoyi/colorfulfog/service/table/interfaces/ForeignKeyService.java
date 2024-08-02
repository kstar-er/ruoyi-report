package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.ForeignKey;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.RemoveKeyDto;

import java.util.List;
import java.util.Map;

public interface ForeignKeyService extends IService<ForeignKey>{

    /**
     * 传入table的id，获取该表的所有外键
     * @param idList
     * @return 返回外键的map，key为表名，第二个key为对应表名，value为ForeignKey
     */

    Map<String,Map<String,ForeignKey>> getForeignKeyMap(List<Integer> idList);//表名->外键表名->ForeignKey
    Map<String,Map<String,List<ForeignKey>>> getForeignKeyListMap(List<Long> idList);//表名->外键表名->当前表key->关联表key

    boolean save(ForeignKey foreignKey);
    boolean saveBatch2(List<ForeignKey> foreignKey);
    void removeKeyPair(RemoveKeyDto removeKeyDto);
}
