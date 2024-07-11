package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.mapper.OrderTableRelationMapper;
import com.ruoyi.colorfulfog.model.ForeignKey;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;
import com.ruoyi.colorfulfog.service.table.interfaces.ForeignKeyService;
import com.ruoyi.colorfulfog.service.table.interfaces.OrderTableRelationService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderTableRelationServiceImpl extends ServiceImpl<OrderTableRelationMapper, OrderTableRelation> implements OrderTableRelationService {

    @Resource
    ForeignKeyService foreignKeyService;
    @Override
    public Map<String, List<OrderTableRelation>> getByOrderName(List<String> orderName) {
        return list(new LambdaQueryWrapper<OrderTableRelation>().in(
                OrderTableRelation::getOrderName, orderName)).stream().collect(Collectors.groupingBy(OrderTableRelation::getOrderTable));
    }

    @Override
    public List<OrderTableRelation> listByOrderName(List<String> orderName) {
        return list(new LambdaQueryWrapper<OrderTableRelation>().in(
                OrderTableRelation::getOrderName, orderName));
    }

    @Override
    public PageInfo<OrderTableRelationVO> list(OrderTableRelation orderTableRelation){
        List<OrderTableRelation> orderTableRelations = list(new LambdaQueryWrapper<>(orderTableRelation)
                .orderByDesc(OrderTableRelation::getId));
        List<OrderTableRelationVO> orderTableRelationVOS = new ArrayList<>();
        List<Integer> idList = orderTableRelations.stream().map(OrderTableRelation::getId).collect(Collectors.toList());

        List<ForeignKey> foreignKeys = foreignKeyService.list(new LambdaQueryWrapper<ForeignKey>()
                .in(ForeignKey::getTableId, idList));
        Map<Integer,List<ForeignKey>> foreignKeyMap = foreignKeys.stream().collect(Collectors.groupingBy(ForeignKey::getTableId));

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
}

