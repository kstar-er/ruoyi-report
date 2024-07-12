package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.TableFieldRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;

import java.util.List;
import java.util.Map;


public interface TableFieldRelationService extends IService<TableFieldRelation> {

    List<Map> getTableFile(String tableName,Integer dataSourceId);

    /**
     * 根据表名导入
     * @param addTableFieldDto
     */
    void  saveTableFile(AddTableFieldDto addTableFieldDto);

    /**
     * 批量导入
     * @param addTableFieldDto
     */
    void saveTableFileList(AddTableFieldDto addTableFieldDto);

    /**
     * 根据表名更新表关系字段
     *
     * @param addTableFieldDto
     */
    void updateTableField(AddTableFieldDto addTableFieldDto);
}

