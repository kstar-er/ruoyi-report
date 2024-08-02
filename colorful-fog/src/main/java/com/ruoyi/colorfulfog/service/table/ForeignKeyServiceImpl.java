package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.model.dto.RemoveKeyDto;
import com.ruoyi.common.core.exception.GlobalException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.ForeignKeyMapper;
import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.service.table.interfaces.ForeignKeyService;
@Service
public class ForeignKeyServiceImpl extends ServiceImpl<ForeignKeyMapper, ForeignKey> implements ForeignKeyService{

    @Override
    public  Map<String, Map<String,ForeignKey>> getForeignKeyMap(List<Integer> idList){
        List<ForeignKey> foreignKeys = list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId,idList));

        // 构建外键关系图
        Map<String, Map<String,ForeignKey>> foreignKeyMap = new HashMap<>();

        for (ForeignKey foreignKey : foreignKeys) {

            String tableName = foreignKey.getTableName();
            String foreignTable = foreignKey.getForeignTableName();

            if (!foreignKeyMap.containsKey(tableName)) {
                foreignKeyMap.put(tableName, new HashMap<>());
            }
            foreignKeyMap.get(tableName).put(foreignTable, foreignKey);
        }
        return foreignKeyMap;
    }
    @Override
    public Map<String, Map<String, List<ForeignKey>>> getForeignKeyListMap(List<Long> idList){
        List<ForeignKey> foreignKeys = list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId,idList));

        // 构建外键关系图
        Map<String, Map<String,List<ForeignKey>>> foreignKeyMap = new HashMap<>();

        for (ForeignKey foreignKey : foreignKeys) {

            String tableName = foreignKey.getTableName();
            String foreignTable = foreignKey.getForeignTableName();

            if (!foreignKeyMap.containsKey(tableName)) {
                foreignKeyMap.put(tableName, new HashMap<>());
            }
            if (!foreignKeyMap.get(tableName).containsKey(foreignTable)) {
                foreignKeyMap.get(tableName).put(foreignTable, new ArrayList<>());
            }
            foreignKeyMap.get(tableName).get(foreignTable).add(foreignKey);
        }
        return foreignKeyMap;
    }

    @Override
    public boolean save(ForeignKey foreignKey){
        ForeignKey foreignKey1 = new ForeignKey();
        BeanUtils.copyProperties(foreignKey,foreignKey1);
        foreignKey1.setForeignTableId(foreignKey.getTableId());
        foreignKey1.setForeignTableName(foreignKey.getTableName());
        foreignKey1.setTableId(foreignKey.getForeignTableId());
        foreignKey1.setTableName(foreignKey.getForeignTableName());
        foreignKey1.setForeignKey(foreignKey.getRelTableForeignKey());
        foreignKey1.setRelTableForeignKey(foreignKey.getForeignKey());
        List<ForeignKey> foreignKeyList = new ArrayList<>();
        foreignKeyList.add(foreignKey);
        foreignKeyList.add(foreignKey1);
//        saveCheck(foreignKeyList);
        saveBatch(foreignKeyList);
        return  true;
    }
    private void saveCheck(List<ForeignKey> foreignKeyList){
        List<String> errList = new ArrayList<>();
        List<ForeignKey> foreignKeys = list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId, foreignKeyList.stream().map(ForeignKey::getTableId)
                        .collect(Collectors.toList())));
        for (ForeignKey foreignKey : foreignKeyList) {
            if(foreignKeys.stream().anyMatch(f -> f.getTableId().equals(foreignKey.getTableId())
                    && f.getForeignTableId().equals(foreignKey.getForeignTableId()))) {
                errList.add("表" + foreignKey.getTableName() + "和表" + foreignKey.getForeignTableName() + "已经存在关联关系");
            }
        }
        if(errList.size() > 0) {
            throw new GlobalException(errList.toString());
        }
    }
    @Override
    public boolean saveBatch2(List<ForeignKey> foreignKey){
        List<ForeignKey> foreignKeyList = new ArrayList<>();
        for (ForeignKey key : foreignKey) {
            ForeignKey foreignKey1 = new ForeignKey();
            BeanUtils.copyProperties(key,foreignKey1);
            foreignKey1.setForeignTableId(key.getTableId());
            foreignKey1.setForeignTableName(key.getTableName());
            foreignKey1.setTableId(key.getForeignTableId());
            foreignKey1.setTableName(key.getForeignTableName());
            foreignKey1.setForeignKey(key.getRelTableForeignKey());
            foreignKey1.setRelTableForeignKey(key.getForeignKey());
            foreignKeyList.add(key);
            foreignKeyList.add(foreignKey1);
        }
//        saveCheck(foreignKeyList);
        saveBatch(foreignKeyList);
        return true;
    }

    @Override
    public void removeKeyPair(RemoveKeyDto removeKeyDto){
        List<ForeignKey> foreignKeys = new ArrayList<>();
        List<ForeignKey> foreignKey1 = list(new LambdaQueryWrapper<ForeignKey>()
                .eq(ForeignKey::getForeignKey,removeKeyDto.getRelTableForeignKey())
                .eq(ForeignKey::getTableName,removeKeyDto.getTableName()));
        if(foreignKey1.size() > 1) {
            throw new GlobalException("外键" + removeKeyDto.getRelTableForeignKey() + "在表" + removeKeyDto.getTableName() + "中存在多个关联关系");
        }

        List<ForeignKey> foreignKey2 = list(new LambdaQueryWrapper<ForeignKey>()
                .eq(ForeignKey::getRelTableForeignKey,removeKeyDto.getRelTableForeignKey())
                .eq(ForeignKey::getForeignTableName,removeKeyDto.getTableName()));
        if(foreignKey1.size() > 1) {
            throw new GlobalException("外键" + removeKeyDto.getRelTableForeignKey() + "在表" + removeKeyDto.getTableName() + "中存在多个关联关系");
        }
        foreignKeys.addAll(foreignKey1);
        foreignKeys.addAll(foreignKey2);
        removeBatchByIds(foreignKeys.stream().map(ForeignKey::getId).collect(Collectors.toList()));
    }
}
