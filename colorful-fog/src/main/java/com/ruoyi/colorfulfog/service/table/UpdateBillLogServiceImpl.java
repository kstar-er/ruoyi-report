package com.ruoyi.colorfulfog.service.table;

import com.ruoyi.colorfulfog.model.dto.SelectOneFieldLogDto;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.mongodb.UpdateRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.model.UpdateBillLog;
import com.ruoyi.colorfulfog.mapper.UpdateBillLogMapper;
import com.ruoyi.colorfulfog.service.table.interfaces.UpdateBillLogService;


@Service
public class UpdateBillLogServiceImpl extends ServiceImpl<UpdateBillLogMapper, UpdateBillLog> implements UpdateBillLogService{

    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public Map<String, List<UpdateRecord>> selectOneFieldLog(SelectOneFieldLogDto entity){
        if (entity.getBillCode().contains("CDA")) {
            CollectBillData billData = mongoTemplate.findOne(new Query(new Criteria("billCode").is(entity.getBillCode())), CollectBillData.class);
            Map<String, List<UpdateRecord>>  map = billData.getLastData();
            return map;
        }
        BillData billData = mongoTemplate.findOne(new Query(new Criteria("billCode").is(entity.getBillCode())),BillData.class);
        Map<String, List<UpdateRecord>>  map = billData.getLastData();
        return map;
    }
}
