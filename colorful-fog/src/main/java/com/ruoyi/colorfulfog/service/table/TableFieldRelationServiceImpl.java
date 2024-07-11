package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.mapper.TableFieldRelationMapper;
import com.ruoyi.colorfulfog.model.TableFieldRelation;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.colorfulfog.service.table.interfaces.TableFieldRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class TableFieldRelationServiceImpl extends ServiceImpl<TableFieldRelationMapper, TableFieldRelation> implements TableFieldRelationService {

    @Autowired
    private TableFieldRelationMapper tableFieldRelationMapper;
    @Autowired
    DataSourceService dataSourceService;

    @Override
    public List<Map> getTableFile(String tableName,Integer dataSourceId) {
        List<Map<String,Object>> list = dataSourceService.execute("show full fields from " + tableName,dataSourceId);
        List<Map> mapList = new ArrayList<>();
        for (Map<String,Object> map : list) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("field",map.get("Field"));
                    map1.put("type",map.get("Type"));
                    map1.put("comment",map.get("Comment"));
                    mapList.add(map1);
        }
        return mapList;
    }

    /**
     * 根据表名导入
     * @param addTableFieldDto
     */
    @Override
    public void saveTableFile(AddTableFieldDto addTableFieldDto ) {
        String tableName =  addTableFieldDto.getTableNameList().get(0);
        List<Map> mapList = getTableFile(tableName,addTableFieldDto.getDataSourceId());
        List<TableFieldRelation> tableFieldRelationList = new ArrayList<>();
        List<TableFieldRelation> old = list(new LambdaQueryWrapper<TableFieldRelation>()
                .eq(TableFieldRelation::getTableName,tableName));
        if (old.size() > 0){
            throw new GlobalException("该表已导入过字段，请勿重复导入。如有更新请使用更新功能");
        }
        for (Map map : mapList) {
            TableFieldRelation tableFieldRelation = new TableFieldRelation();
            tableFieldRelation.setTableName(tableName);
            tableFieldRelation.setField(map.get("field").toString());
            tableFieldRelation.setType(map.get("type").toString());
            tableFieldRelation.setFieldName(map.get("comment").toString());
            tableFieldRelationList.add(tableFieldRelation);
        }
        saveBatch(tableFieldRelationList);
    }

    /**
     * 批量导入
     *
     * @param addTableFieldDto
     */
    @Override
    public void saveTableFileList(AddTableFieldDto addTableFieldDto) {
        List<TableFieldRelation> tableFieldRelationList = new ArrayList<>();
        for (String tableName : addTableFieldDto.getTableNameList()) {
            List<Map> mapList = getTableFile(tableName,addTableFieldDto.getDataSourceId());
            for (Map map : mapList) {
                TableFieldRelation tableFieldRelation = new TableFieldRelation();
                tableFieldRelation.setTableName(tableName);
                tableFieldRelation.setField(map.get("field").toString());
                tableFieldRelation.setType(map.get("type").toString());
                tableFieldRelation.setFieldName(map.get("comment").toString());
                tableFieldRelationList.add(tableFieldRelation);
            }
        }
        saveBatch(tableFieldRelationList);
    }

    /**
     * 根据表名更新表关系字段
     *
     * @param addTableFieldDto
     */
    @Override
    public void updateTableField(AddTableFieldDto addTableFieldDto) {
        tableFieldRelationMapper.delete(new LambdaQueryWrapper<TableFieldRelation>()
                .eq(TableFieldRelation::getTableName, addTableFieldDto.getTableNameList().get(0)));
        saveTableFile(addTableFieldDto);
    }
}

