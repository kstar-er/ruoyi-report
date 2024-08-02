package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.dto.AddTableFieldDto;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.colorfulfog.modules.datasource.service.impl.JdbcConstants;
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

        List<ForeignKey> foreignKeys = foreignKeyService.list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId, idList));
        Map<Long,List<ForeignKey>> foreignKeyMap = foreignKeys.stream().collect(Collectors.groupingBy(ForeignKey::getTableId));

        for (OrderTableRelation tableRelation : orderTableRelations) {
            OrderTableRelationVO orderTableRelationVO = new OrderTableRelationVO();
            BeanUtils.copyProperties(tableRelation, orderTableRelationVO);
            orderTableRelationVO.setForeignKeyList(foreignKeyMap.get(tableRelation.getId()));
            if (foreignKeyMap.get(tableRelation.getId()) != null) {
                orderTableRelationVO.setFieldList(foreignKeyMap.get(tableRelation.getId()).stream().map(ForeignKey::getRelTableForeignKey).distinct().collect(Collectors.toList()));
            }
            orderTableRelationVOS.add(orderTableRelationVO);
        }
        PageInfo<OrderTableRelationVO> pageInfo = new PageInfo<>(orderTableRelationVOS);
        pageInfo.setTotal(PageInfo.of(orderTableRelations).getTotal());
        return pageInfo;
    }

    public List<OrderTableRelation> getTableNameByDataSourceId(Integer dataSourceId){
        DataSource dataSource = dataSourceService.getById(dataSourceId);
        List<Map<String,Object>> list = dataSourceService.execute(getSqlByType(dataSource.getSourceType()) , dataSourceId);


        List<OrderTableRelation> orderTableRelations = new ArrayList<>();
        for (Map<String,Object> map : list) {
            Object comment = map.get("TABLE_COMMENT");
            if (comment == null) {
                comment = map.get("TABLE_NAME");
            }
            orderTableRelations.add(OrderTableRelation.builder()
                    .orderTableName(comment.toString())
                    .orderTable(map.get("TABLE_NAME").toString())
                    .comment(comment.toString())
                    .build());
        }
        return orderTableRelations;
    }
    private String getSqlByType(String type){
        switch (type) {
            case JdbcConstants.ELASTIC_SEARCH_SQL:
                return "";
            case JdbcConstants.MYSQL:
                return "SELECT TABLE_NAME,TABLE_COMMENT \n" +
                        "FROM information_schema.tables \n" +
                        "WHERE table_schema = DATABASE() \n" +
                        "  AND table_type = 'BASE TABLE';";
            case JdbcConstants.KUDU_IMAPLA:
            case JdbcConstants.ORACLE:
            case JdbcConstants.SQL_SERVER:
                return "SELECT \n" +
                        "    t.name AS TABLE_NAME,\n" +
                        "    ep.value AS TABLE_COMMENT\n" +
                        "FROM \n" +
                        "    sys.tables t\n" +
                        "    LEFT JOIN sys.extended_properties ep ON t.object_id = ep.major_id\n" +
                        "                                        AND ep.minor_id = 0 \n" +
                        "                                        AND ep.name = 'MS_Description'\n" +
                        "                                        AND ep.class = 1 -- 1 代表对象级别\n" +
                        "WHERE \n" +
                        "    t.type = 'U' -- 'U' 代表用户表";
            case JdbcConstants.JDBC:
            case JdbcConstants.POSTGRESQL:
            case JdbcConstants.HTTP:
            default:
                throw new RuntimeException("不支持该类型");
        }
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

