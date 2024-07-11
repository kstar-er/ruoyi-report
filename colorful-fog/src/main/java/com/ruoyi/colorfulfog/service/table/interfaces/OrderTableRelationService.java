package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.model.OrderTableRelation;
import com.ruoyi.colorfulfog.model.vo.OrderTableRelationVO;

import java.util.List;
import java.util.Map;

public interface OrderTableRelationService extends IService<OrderTableRelation> {

    Map<String, List<OrderTableRelation>> getByOrderName(List<String> orderName);

    List<OrderTableRelation> listByOrderName(List<String> orderName);
    PageInfo<OrderTableRelationVO> list(OrderTableRelation orderTableRelation);
}

