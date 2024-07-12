package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DataSourceService;
import com.ruoyi.colorfulfog.service.table.interfaces.ForeignKeyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.OrderTableRelationMapper;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.service.table.interfaces.OrderTableRelationService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderTableRelationServiceImpl extends ServiceImpl<OrderTableRelationMapper, OrderTableRelation> implements OrderTableRelationService {

    @Resource
    ForeignKeyService foreignKeyService;

    @Autowired
    DataSourceService dataSourceService;
    @Autowired
    TableFieldRelationServiceImpl tableFieldRelationService;

    @Override
    public Map<String, List<OrderTableRelation>> getByOrderName(List<String> orderName) {
        return list(new LambdaQueryWrapper<OrderTableRelation>().in(
                OrderTableRelation::getOrderName, orderName)).stream().collect(Collectors.groupingBy(OrderTableRelation::getOrderTable));
    }

    @Override
    public List<OrderTableRelation> listByOrderName(List<String> orderTable) {
        return list(new LambdaQueryWrapper<OrderTableRelation>().in(
                OrderTableRelation::getOrderTable, orderTable));
    }

    @Override
    public PageInfo<OrderTableRelationVO> list(OrderTableRelation orderTableRelation){
        List<OrderTableRelation> orderTableRelations = list(new LambdaQueryWrapper<>(orderTableRelation)
                .orderByDesc(OrderTableRelation::getId));
        List<OrderTableRelationVO> orderTableRelationVOS = new ArrayList<>();
        List<Long> idList = orderTableRelations.stream().map(OrderTableRelation::getId).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return PageInfo.of(orderTableRelationVOS);
        }
        List<ForeignKey> foreignKeys = foreignKeyService.list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId, idList));
        Map<Long,List<ForeignKey>> foreignKeyMap = foreignKeys.stream().collect(Collectors.groupingBy(ForeignKey::getTableId));

        for (OrderTableRelation tableRelation : orderTableRelations) {
            OrderTableRelationVO orderTableRelationVO = new OrderTableRelationVO();
            BeanUtils.copyProperties(tableRelation, orderTableRelationVO);
            orderTableRelationVO.setForeignKeyList(foreignKeyMap.get(tableRelation.getId()));
            orderTableRelationVOS.add(orderTableRelationVO);
        }
        PageInfo<OrderTableRelationVO> pageInfo = new PageInfo<>(orderTableRelationVOS);
        pageInfo.setTotal(PageInfo.of(orderTableRelations).getTotal());
        return pageInfo;
    }

    public List<OrderTableRelation> getTableNameByDataSourceId(Integer dataSourceId){
        List<Map<String,Object>> list = dataSourceService.execute("SELECT TABLE_NAME,TABLE_COMMENT \n" +
                "FROM information_schema.tables \n" +
                "WHERE table_schema = DATABASE() \n" +
                "  AND table_type = 'BASE TABLE';" , dataSourceId);
        List<OrderTableRelation> orderTableRelations = new ArrayList<>();
        for (Map<String,Object> map : list) {
            orderTableRelations.add(OrderTableRelation.builder()
                    .orderTableName(map.get("TABLE_COMMENT").toString())
                    .orderTable(map.get("TABLE_NAME").toString())
                    .comment(map.get("TABLE_COMMENT").toString())
                    .build());
        }
        return orderTableRelations;
    }
    @Transactional
    public void saveAndAddField(List<OrderTableRelation> orderTableRelations){
        Integer dataSourceId = orderTableRelations.get(0).getDataSourceId();

        List<String> tableNameList= orderTableRelations.stream().map(OrderTableRelation::getOrderTable).collect(Collectors.toList());
        List<OrderTableRelation> orderTableRelationList = list(new LambdaQueryWrapper<OrderTableRelation>()
                        .in(OrderTableRelation::getOrderTableName, tableNameList));
        if (!orderTableRelationList.isEmpty()) {
            throw new RuntimeException("表已存在,请勿重复导入："+tableNameList);
        }
        tableFieldRelationService.saveTableFile(AddTableFieldDto.builder()
                        .dataSourceId(dataSourceId)
                        .tableNameList(tableNameList)
                .build());

        saveBatch(orderTableRelations);
    }
}

