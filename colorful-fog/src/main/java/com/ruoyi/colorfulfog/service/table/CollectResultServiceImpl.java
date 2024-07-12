package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectResultMapper;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultService;
@Service
public class CollectResultServiceImpl extends ServiceImpl<CollectResultMapper, CollectResult> implements CollectResultService{

    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public Map<String, List<CollectResult>> listBillCodeResultMapByBillCode(List<String> billCode){
            if (CollectionUtils.isEmpty(billCode)){
                return null;
            }
            return list(new LambdaQueryWrapper<CollectResult>().in(CollectResult::getBillCode, billCode))
                    .stream().collect(Collectors.groupingBy(CollectResult::getBillCode));
    }
    @Override
    public List<CollectBillData> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList){
        return mongoTemplate.find(new Query(Criteria.where("billCode")
                        .in(manualUpdateDtoList.stream().map(ManualUpdateDto::getBillCode).collect(Collectors.toList())))
                , CollectBillData.class);
    }
}
