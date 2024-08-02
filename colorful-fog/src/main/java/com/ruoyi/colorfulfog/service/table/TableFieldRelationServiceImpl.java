package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.colorfulfog.modules.datasource.service.impl.JdbcConstants;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.common.core.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.model.TableFieldRelation;
import com.ruoyi.colorfulfog.mapper.TableFieldRelationMapper;
import com.ruoyi.colorfulfog.service.table.interfaces.TableFieldRelationService;


@Slf4j
@Service
public class TableFieldRelationServiceImpl extends ServiceImpl<TableFieldRelationMapper, TableFieldRelation> implements TableFieldRelationService {

    @Autowired
    private TableFieldRelationMapper tableFieldRelationMapper;
    @Autowired
    DataSourceService dataSourceService;

    @Override
    public List<Map> getTableFile(String tableName,Integer dataSourceId) {
        DataSource dataSource = dataSourceService.getById(dataSourceId);
        List<Map<String,Object>> list = dataSourceService.execute(getSqlByType(dataSource.getSourceType(),tableName),dataSourceId);
        List<Map> mapList = new ArrayList<>();
        for (Map<String,Object> map : list) {
                    Map<String,Object> map1 = new HashMap<>();
                    map1.put("field",map.get("Field"));
                    map1.put("type",map.get("Type"));
                    if(map.get("Comment") != null && !map.get("Comment").equals("")){
                        map1.put("comment",map.get("Comment"));
                    }else {
                        map1.put("comment",map.get("Field"));
                    }
                    mapList.add(map1);
        }
        return mapList;
    }
    private String getSqlByType(String type,String tableName){
        switch (type) {
            case JdbcConstants.ELASTIC_SEARCH_SQL:
                return "";
            case JdbcConstants.MYSQL:
                return "show full fields from " + tableName;
            case JdbcConstants.KUDU_IMAPLA:
            case JdbcConstants.ORACLE:
            case JdbcConstants.SQL_SERVER:
                return "SELECT \n" +
                        "    c.name AS Field,\n" +
                        "    ty.name AS Type,\n" +
                        "    c.is_nullable,\n" +
                        "    ep.value AS Comment\n" +
                        "FROM \n" +
                        "    sys.columns c\n" +
                        "    INNER JOIN sys.types ty ON c.user_type_id = ty.user_type_id\n" +
                        "    INNER JOIN sys.tables t ON c.object_id = t.object_id\n" +
                        "    LEFT JOIN sys.extended_properties ep ON c.object_id = ep.major_id \n" +
                        "                                        AND c.column_id = ep.minor_id \n" +
                        "                                        AND ep.name = 'MS_Description'\n" +
                        "                                        AND ep.class = 1\n" +
                        "WHERE \n" +
                        "    t.name = '" + tableName + "'\n" +
                        "    AND ty.is_user_defined = 0; ";
            case JdbcConstants.JDBC:
            case JdbcConstants.POSTGRESQL:
            case JdbcConstants.HTTP:
            default:
                throw new RuntimeException("不支持该类型");
        }
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
        List<String> tableNameList = addTableFieldDto.getTableNameList();
        List<TableFieldRelation> old = list(new LambdaQueryWrapper<TableFieldRelation>()
                .in(TableFieldRelation::getTableName,tableNameList));
        if (!old.isEmpty()){
            throw new GlobalException("该表"+old.stream().map(TableFieldRelation::getTableName).collect(Collectors.toList())+"已导入过字段，请勿重复导入。如有更新请使用更新功能");
        }
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
    @Override
    public     Map<String,String> getDeleteFlagFieldMap(List<String> tableNameList) {
        List<TableFieldRelation> tableFieldRelationList = list(new LambdaQueryWrapper<TableFieldRelation>()
                .in(TableFieldRelation::getTableName, tableNameList));
        Map<String, String> map = new HashMap<>();
        for (TableFieldRelation tableFieldRelation : tableFieldRelationList) {
            if (tableFieldRelation.getIsDeleteFlag() == 1) {
                map.put(tableFieldRelation.getTableName(), tableFieldRelation.getField());
            }
        }
        return map;
    }
}

