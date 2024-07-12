package com.ruoyi.colorfulfog.service.table.interfaces;

import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;

import java.util.List;
import java.util.Map;

public interface OrderTableRelationService extends IService<OrderTableRelation> {

    Map<String, List<OrderTableRelation>> getByOrderName(List<String> orderName);

    List<OrderTableRelation> listByOrderName(List<String> orderTable);
    PageInfo<OrderTableRelationVO> list(OrderTableRelation orderTableRelation);

    /**
     * 批量传入所选表名，保存表名，同时根据数据库id自动导入字段。
     * @param orderTableRelations
     */
    void saveAndAddField(List<OrderTableRelation> orderTableRelations);
    List<OrderTableRelation> getTableNameByDataSourceId(Integer dataSourceIds);

}

