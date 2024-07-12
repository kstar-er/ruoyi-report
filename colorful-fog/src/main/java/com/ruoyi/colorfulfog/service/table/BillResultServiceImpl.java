package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.BillCheckStatusEnum;
import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.BillResultMapper;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.service.table.interfaces.BillResultService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillResultServiceImpl extends ServiceImpl<BillResultMapper, BillResult> implements BillResultService {

    @Autowired
    BillMainService billMainService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Override
    public Map<String, List<BillResult>> listBillCodeResultMapByBillCode(List<String> billCode){
        if (CollectionUtils.isEmpty(billCode)){
            return null;
        }
        return list(new LambdaQueryWrapper<BillResult>().in(BillResult::getBillCode, billCode))
                .stream().collect(Collectors.groupingBy(BillResult::getBillCode));
    }

    /**
     * 根据时间范围得到账单code，然后根据code得到需要汇总的字段
     *
     * @param collectResultDto
     * @return
     */
    @Override
    public List<BaseData> listBillResultMapByTime(CollectResultDto collectResultDto, CollectObjectEnum collectDataTypeEnum){
        Query query = new Query();
        query.addCriteria(Criteria.where("tagTime").gte(collectResultDto.getTimeStart()).lte(collectResultDto.getTimeEnd())
                .and("schemeCode").is(collectResultDto.getSchemeCode()));
        if (collectResultDto.getBelongArchiveCode()!=null){
            query.addCriteria(Criteria.where("belongArchiveCode").is(collectResultDto.getBelongArchiveCode()));
            Query oldQuery = new Query();
            oldQuery.addCriteria(Criteria.where("schemeCode").is(collectResultDto.getCollectSchemeCode()));
            oldQuery.addCriteria(Criteria.where("costTerm").is(collectResultDto.getCostTerm()));
            oldQuery.addCriteria(Criteria.where("belongArchiveCode").is(collectResultDto.getBelongArchiveCode()));
            List<CollectBillData> collectBillData = mongoTemplate.find(oldQuery,CollectBillData.class);
            if (!collectBillData.isEmpty()){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"该时间段内该商家已经存在汇总数据。请勿重复生成");
            }
        }else{
            Query oldQuery = new Query();
            oldQuery.addCriteria(Criteria.where("schemeCode").is(collectResultDto.getCollectSchemeCode()));
            oldQuery.addCriteria(Criteria.where("costTerm").is(collectResultDto.getCostTerm()));
            List<CollectBillData> collectBillData = mongoTemplate.find(oldQuery,CollectBillData.class);
            if (!collectBillData.isEmpty()){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"该时间段内已经存在汇总数据。若需要请对单独商家进行生成汇总数据");
            }
            //查询全部,如果在时间范围内有已经生成的汇总数据，则不再生成
        }
        if (collectDataTypeEnum.equals(CollectObjectEnum.BILL)){
            List<BillData> billResultList = mongoTemplate.find(query,BillData.class);
            return new ArrayList<>(billResultList);
        }else if (collectDataTypeEnum.equals(CollectObjectEnum.COLLECT)){
            List<CollectBillData> billResultList = mongoTemplate.find(query,CollectBillData.class);
            return new ArrayList<>(billResultList);
        }
      throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"汇总类型不存在");
    }
    @Override
    public List<BillData> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList){
        return mongoTemplate.find(new Query(Criteria.where("billCode")
                .in(manualUpdateDtoList.stream().map(ManualUpdateDto::getBillCode).collect(Collectors.toList())))
                ,BillData.class);
    }
    @Override
    public  List<BillData> listByBillCode(List<String> billCode){
        return mongoTemplate.find(new Query(Criteria.where("billCode").in(billCode)),BillData.class);
    }
    @Override
    public List<BillData> listBySchemeAndBatch(BillResultFlashDto billResultDto){
        Query query = new Query();
        if (billResultDto.getBatchCode()!=null){
            query.addCriteria(Criteria.where("batchCode").is(billResultDto.getBatchCode()));
        }
        if (billResultDto.getCollectResultCode()!=null){
            query.addCriteria(Criteria.where("collectResultCode").is(billResultDto.getCollectResultCode()));
        }

        return mongoTemplate.find(query, BillData.class);

    }


    @Override
    public List<String> testSave(){
        BillData result = new BillData();
        result.setId("123L");
        result.setData(new HashMap<>());

        mongoTemplate.save(result);
        return null;
    }
}


