package com.ruoyi.colorfulfog.service.table;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.*;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.entity.CollectDataTypeEntity;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.mongodb.UpdateRecord;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.model.vo.ResultNameCodeVO;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import com.ruoyi.common.helper.LoginHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectResultMainMapper;

import javax.annotation.Resource;

@Slf4j
@Service
public class CollectResultMainServiceImpl extends ServiceImpl<CollectResultMainMapper, CollectResultMain> implements CollectResultMainService{

    @Resource
    CollectResultService collectResultService;
    @Resource
    CollectSchemeMainService collectSchemeMainService;

    @Resource
    BillMainService billMainService;

    @Resource
    BillResultService billResultService;

    @Resource
    UpdateBillLogService updateBillLogService;
    @Resource
    SchemeMainService schemeMainService;

    @Autowired
    MongoTemplate mongoTemplate;
    @Resource
    CollectSchemeDetailService collectSchemeDetailService;

    private static final String DETAIL_PREFIX = "detail.";


    @Override
    public BillResultVO list(CollectResultMainDto billResultDto, Integer currentPage, Integer pageSize){
        // 按方案编码，时间，账单编码等参数查询
        Query query = new Query();
        if (billResultDto.getCollectSchemeCode()==null){
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"collectSchemeCode不能为空");
        }
        query.addCriteria(Criteria.where("schemeCode").is(billResultDto.getCollectSchemeCode()));
        if (billResultDto.getBelongArchiveName()!=null){
            query.addCriteria(Criteria.where("belongArchiveName").regex(".*?\\" +billResultDto.getBelongArchiveName()+ ".*"));
        }
        if (billResultDto.getCostTerm()!=null){
            query.addCriteria(Criteria.where("costTerm").is(billResultDto.getCostTerm()));
        }
        if (billResultDto.getAuditStatus()!=null){
            query.addCriteria(Criteria.where("auditStatus").is(billResultDto.getAuditStatus()));
        }
        if (billResultDto.getStatus()!=null){
            query.addCriteria(Criteria.where("status").is(billResultDto.getStatus()));
        }
        if (billResultDto.getFieldSelectList()!=null){
            for (String key : billResultDto.getFieldSelectList().keySet()) {
                query.addCriteria(Criteria.where("detail."+key).regex(".*?\\" +billResultDto.getFieldSelectList().get(key)+ ".*"));
            }
        }
        long count = mongoTemplate.count(query,CollectBillData.class);
        query.limit(pageSize).skip((long) (currentPage - 1) *pageSize);
        List<CollectBillData> billMainList = mongoTemplate.find(query,CollectBillData.class);
        BillResultVO billResultVO =  buildBillResultVO(billMainList,SelectTypeEnum.CALC);
        billResultVO.getResultDataList().setTotal(count);
        return billResultVO;
    }

    //构建查询参数
    @Override
    public void buildQueryCriteria(Query query, FilterCriteria filterCriteria, String fieldName, List<String> value) {
        switch (filterCriteria.getOperator()) {
            case "等于":
                query.addCriteria(Criteria.where(fieldName).in(value));
                break;
            case "处于":
                    query.addCriteria(Criteria.where(fieldName).gte(value.get(0)).lte(value.get(1)));
                break;
            case "大于":
                    query.addCriteria(Criteria.where(fieldName).gt(value.get(0)));
                break;
            case "小于":
                    query.addCriteria(Criteria.where(fieldName).lt(value.get(0)));
                break;
            default: break;
        }
    }
    @Override
    public Map<String, List<Map<String, Double>>> list(DataSourceDTO dataSourceDTO, List<FilterCriteria> filterCriteriaList){
        // 按方案编码，时间，账单编码等参数查询
        Query query = new Query();
        if (dataSourceDTO.getSourceValue().isEmpty()){
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"sourceName不能为空");
        }
        query.addCriteria(Criteria.where("schemeCode").is(dataSourceDTO.getSourceValue()));
        //循环过滤条件
        if (null!=filterCriteriaList){
            for (FilterCriteria filterCriteria : filterCriteriaList
            ){
                // 检查是否有足够的元素，对于"处于"操作符，需要至少两个元素
                if ("处于".equals(filterCriteria.getOperator()) && filterCriteria.getValue().size()!=2) {
                    throw new IllegalArgumentException("用处于查询时，查询值应该为两个");
                }
                BaseData baseData = new BaseData();
                Class<? extends BaseData> aClass = baseData.getClass();
                try {
                    Field field = aClass.getDeclaredField(filterCriteria.getFieldName());
                    buildQueryCriteria(query, filterCriteria, filterCriteria.getFieldName(), filterCriteria.getValue());
                }
                catch (NoSuchFieldException e) {
                    String adjustedFieldName = DETAIL_PREFIX + filterCriteria.getFieldName();
                    buildQueryCriteria(query, filterCriteria, adjustedFieldName, filterCriteria.getValue());
                }
            }
        }
        List<CollectBillData> billMainList = mongoTemplate.find(query,CollectBillData.class);
        List<BaseData> baseData = new ArrayList<>(billMainList);
        return billMainService.getAggregatedDataMap(dataSourceDTO, baseData);
    }

    @Override
    public BillResultVO listByUserCode(CollectResultMainDto billResultDto, Integer currentPage, Integer pageSize, SelectTypeEnum selectTypeEnum){
        // 按方案编码，时间，账单编码等参数查询
        Query query = new Query();
        query.addCriteria(Criteria.where("belongArchiveCode").is(billResultDto.getBelongArchiveCode()));
        query.addCriteria(Criteria.where("auditStatus").ne(AuditStatusEnum.WAIT_START_AUDIT));

        query.limit(pageSize).skip((long) (currentPage - 1) *pageSize);
        List<CollectBillData> billMainList = mongoTemplate.find(query,CollectBillData.class);
        return buildBillResultVO(billMainList,selectTypeEnum);
    }

    @Override
    public BillResultVO buildBillResultVO(List<CollectBillData> billMainList,SelectTypeEnum selectTypeEnum) {
        List<String> schemeCodeList = billMainList.stream().map(CollectBillData::getSchemeCode).collect(Collectors.toList());
        List<CollectSchemeDetail> schemeDetails = collectSchemeDetailService.listSchemeDetailBySchemeCode(schemeCodeList,selectTypeEnum );
        List<ResultNameCodeVO> resultNameCodeVOList = schemeDetails.stream()
                .map(billResult -> new ResultNameCodeVO(billResult.getCollectResultName(), billResult.getCollectResultCode(),billResult.getResultType(),billResult.getDisplayOrder(),billResult.getDecimal()))
                .distinct()
                .collect(Collectors.toList());
        resultNameCodeVOList = resultNameCodeVOList.stream().sorted(Comparator.comparing(ResultNameCodeVO::getDisplayOrder)).collect(Collectors.toList());;
        BillResultVO billResultVO = new BillResultVO();
        billResultVO.setResultNameList(resultNameCodeVOList);
        billResultVO.setResultDataList(new PageInfo<>(billMainList));
        return billResultVO;

    }

    @Override
    public  List<CollectResultMain> listByBillCode(List<String> billCodes){
        return list(new LambdaQueryWrapper<CollectResultMain>()
                .in(CollectResultMain::getBillCode,billCodes));
    }
    @Override
    public List<CollectBillData> listByBillCode(String billCodes){
        return mongoTemplate.find(new Query(Criteria.where("billCode").in(billCodes)),CollectBillData.class);


    }
    @Override
    public List<CollectBillData> listByCollectCode(List<String> collectCode){
        return mongoTemplate.find(new Query(Criteria.where("collectResultCode").in(collectCode)),CollectBillData.class);

    }
    @Override
    public List<CollectBillData> listById(String id){
        return mongoTemplate.find(new Query(Criteria.where("_id").is(id)),CollectBillData.class);

    }
    @Override
    public List<CollectBillData> listByIdList(List<String> ids){
        ids = ids.stream().distinct().collect(Collectors.toList());
        if (ids.isEmpty()){
            throw new GlobalException("id不能为空");
        }
        Query query = new Query(Criteria.where("_id").in(ids));
        return mongoTemplate.find(query,CollectBillData.class);

    }

    @Override
    public List<ExportExcelVO> exportExcelBatch(List<String> idList,SelectTypeEnum selectTypeEnum){
        List<CollectDataTypeEntity> collectDataCodeList = new ArrayList<>();
        List<ExportExcelVO> exportExcelVOList = new ArrayList<>();
        List<CollectBillData> collectResultMainList = listByIdList(idList);
        if (CollectionUtils.isEmpty(collectResultMainList)){
            throw new GlobalException(ErrorCodeEnum.DATA_NOT_EXIST, String.valueOf(idList));
        }
        CollectSchemeMain collectSchemeMain = collectSchemeMainService.getOneByCode(collectResultMainList.get(0).getSchemeCode());
        CollectDataTypeEntity collectDataTypeEntity = CollectDataTypeEntity.builder()
                .collectCodeList(collectResultMainList.stream().map(CollectBillData::getBillCode).collect(Collectors.toList()))
                .collecIdList(idList)
                .collectSchemeMain(collectSchemeMain)
                .dataType(collectSchemeMain.getCollectObject())
                .collectResultMainList(collectResultMainList)
                .build();
        collectDataCodeList.add(collectDataTypeEntity);
        getAllCode(collectDataCodeList.get(0),collectDataCodeList);
        // 现在拿到了全部主表的数据，以及对应的方案的名称
        for (CollectDataTypeEntity dataTypeEntity : collectDataCodeList) {
            if (dataTypeEntity.getCollectSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getCollectSchemeMain().getCollectSchemeName())
                        .billResultVO(buildBillResultVO(dataTypeEntity.getCollectResultMainList(),selectTypeEnum))
                        .build());
            }else if (dataTypeEntity.getSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getSchemeMain().getName())
                        .billResultVO(billMainService.buildBillResultVO(dataTypeEntity.getBillMainList(), selectTypeEnum))
                        .build());
            }
        }
        return exportExcelVOList;
    }
    @Override
    public List<ExportExcelVO> exportExcel(ExportExcelDto exportExcelDto,SelectTypeEnum selectTypeEnum){
        // 获取所有的账单编号数据
        List<CollectDataTypeEntity> collectDataCodeList = new ArrayList<>();
        List<ExportExcelVO> exportExcelVOList = new ArrayList<>();
        List<CollectBillData> collectResultMainList = listById(exportExcelDto.getId());
        if (CollectionUtils.isEmpty(collectResultMainList)){
            throw new GlobalException(ErrorCodeEnum.DATA_NOT_EXIST, exportExcelDto.getCode());
        }
        CollectSchemeMain collectSchemeMain = collectSchemeMainService.getOneByCode(collectResultMainList.get(0).getSchemeCode());
        CollectDataTypeEntity collectDataTypeEntity = CollectDataTypeEntity.builder()
                .collectCodeList(Collections.singletonList(exportExcelDto.getCode()))
                .collecIdList(Collections.singletonList(exportExcelDto.getId()))
                .collectSchemeMain(collectSchemeMain)
                .dataType(collectSchemeMain.getCollectObject())
                .collectResultMainList(collectResultMainList)
                .build();
        collectDataCodeList.add(collectDataTypeEntity);
        getAllCode(collectDataCodeList.get(0),collectDataCodeList);
        // 现在拿到了全部主表的数据，以及对应的方案的名称
        for (CollectDataTypeEntity dataTypeEntity : collectDataCodeList) {
            if (dataTypeEntity.getCollectSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getCollectSchemeMain().getCollectSchemeName())
                        .billResultVO(buildBillResultVO(dataTypeEntity.getCollectResultMainList(),selectTypeEnum))
                        .build());
            }else if (dataTypeEntity.getSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getSchemeMain().getName())
                        .billResultVO(billMainService.buildBillResultVO(dataTypeEntity.getBillMainList(), selectTypeEnum))
                        .build());
            }
        }
        return exportExcelVOList;
    }
    // 递归获取所有的编码，以及区分一下不同编码是哪个表的
    private void getAllCode(CollectDataTypeEntity collectDataTypeEntity, List<CollectDataTypeEntity> collectDataCodeList){
        if (collectDataTypeEntity.getDataType().equals(CollectObjectEnum.COLLECT)){
            List<CollectBillData> collectResultMainList = listByCollectCode(collectDataTypeEntity.getCollectCodeList());
            Map<String,List<CollectBillData>> collectResultMainMap = collectResultMainList.stream()
                    .collect(Collectors.groupingBy(CollectBillData::getSchemeCode));
            // 需要收集的账单判断下一层收集的是什么类型
            List<CollectSchemeMain> collectSchemeMainList = collectSchemeMainService.listBySchemeCodeList((collectResultMainMap.keySet()));
            for (CollectSchemeMain collectSchemeMain : collectSchemeMainList) {
                String schemeCode = collectSchemeMain.getCollectSchemeCode();
                CollectDataTypeEntity collectDataTypeEntity1 = CollectDataTypeEntity.builder()
                        .collectCodeList(collectResultMainMap.get(schemeCode).stream().map(CollectBillData::getBillCode).collect(Collectors.toList()))
                        .collecIdList(collectResultMainMap.get(schemeCode).stream().map(CollectBillData::getId).collect(Collectors.toList()))
                        .collectSchemeMain(collectSchemeMain)
                        .dataType(collectSchemeMain.getCollectObject())
                        .collectResultMainList(collectResultMainMap.get(schemeCode))
                        .build();
                collectDataCodeList.add(collectDataTypeEntity1);
                getAllCode(collectDataTypeEntity1,collectDataCodeList);
            }
        }else if (collectDataTypeEntity.getDataType().equals(CollectObjectEnum.BILL)){
            // 账单类型的话，递归在这里终止
            List<BillData> billMainList = billMainService.listByCollectCode(collectDataTypeEntity.getCollectCodeList());
            Map<String,List<BillData>> billMainMap = billMainList.stream().collect(Collectors.groupingBy(BillData::getSchemeCode));
            List<SchemeMain> schemeMainList = schemeMainService.listByCode(billMainMap.keySet());

            for (SchemeMain schemeMain : schemeMainList) {
                String schemeCode = schemeMain.getSchemeCode();
                collectDataCodeList.add(CollectDataTypeEntity.builder()
                        .collectCodeList(billMainMap.get(schemeCode).stream().map(BillData::getBillCode).collect(Collectors.toList()))
                         .billMainList(billMainMap.get(schemeCode))
                        .schemeMain(schemeMain)
                        .build());
            }
        }
    }

    @Override
    public String manualUpdate(List<ManualUpdateDto> manualUpdateDto){
        String updateUser;
        if (manualUpdateDto.get(0).getUpdateUser()!=null){
            updateUser = manualUpdateDto.get(0).getUpdateUser();
        }else {
            updateUser= LoginHelper.getUsername();
        }
        String updateTime = TimeUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        long updateCount =0;
        // 按类型进行分组
        Map<CollectObjectEnum,List<ManualUpdateDto>> collectObjectEnumListMap =
                manualUpdateDto.stream().collect(Collectors.groupingBy(ManualUpdateDto::getBillType));

        Map<String,CollectResult> collectBillResultMap;
        // 按类型进行处理
        List<UpdateBillLog> updateBillLogs = new ArrayList<>();
        List<ManualUpdateDto> billManualUpdateDtoList = collectObjectEnumListMap.get(CollectObjectEnum.BILL);
        if (billManualUpdateDtoList!= null){
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BillData.class);
            List<BillData> billResultList = billResultService.listByBillCodeAndName(billManualUpdateDtoList);
            Map<String,BillData> billResultMap = billResultList.stream().collect(Collectors.toMap(BillData::getBillCode, s->s));
            if (CollectionUtils.isEmpty(billResultList)){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"找不到对应的需要修改的字段");
            }
            // 使用code和name两个字段联合对billResultList分组

            for (ManualUpdateDto updateDto : billManualUpdateDtoList) {
                BillData billResult = billResultMap.get(updateDto.getBillCode());
                Map<String,List<UpdateRecord>> updateRecordMap = billResult.getLastData();
                if (updateRecordMap==null){
                    updateRecordMap = new HashMap<>();
                }
                if (!billResult.getData().get(updateDto.getFieldCode()).equals(updateDto.getUpdateValue())){
                    List<UpdateRecord> updateRecordList = updateRecordMap.get(updateDto.getFieldCode());
                    if (updateRecordList==null){
                        updateRecordList = new ArrayList<>();
                    }
                    UpdateRecord updateRecord = UpdateRecord.builder()
                            .updateUser(updateUser)
                            .updateTime(updateTime)
                            .originValue(billResult.getData().get(updateDto.getFieldCode()).toString())
                            .afterValue(updateDto.getUpdateValue().toString())
                            .build();
                    updateRecordList.add(updateRecord);
                    updateCount++;
                    updateRecordMap.put(updateDto.getFieldCode(),updateRecordList);
                    billResult.getData().put(updateDto.getFieldCode(),updateDto.getUpdateValue());
                    // 不等的时候才更新，相等就不更新了
                    Query query = new Query(Criteria.where("_id").is(billResult.getId()));
                    Update update = new Update()
                            .set("data", billResult.getData())
                            .set("lastData", updateRecordMap);
                    bulkOps.updateOne(query, update);
                }
            }
            if(updateCount==0){
                return "本次更新成功条数："+0;
            }
            bulkOps.execute();
        }

        List<ManualUpdateDto> collectManualUpdateDtoList = collectObjectEnumListMap.get(CollectObjectEnum.COLLECT);
        if (collectManualUpdateDtoList!= null){
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, CollectBillData.class);
            List<CollectBillData> collectResultList = collectResultService.listByBillCodeAndName(collectManualUpdateDtoList);
            if (CollectionUtils.isEmpty(collectResultList)){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"找不到对应的需要修改的字段");
            }
            Map<String,CollectBillData> billResultMap = collectResultList.stream().collect(Collectors.toMap(CollectBillData::getBillCode, s->s));
            // 使用code和name两个字段联合对billResultList分组
                for (ManualUpdateDto updateDto : collectManualUpdateDtoList) {
                    CollectBillData billResult = billResultMap.get(updateDto.getBillCode());
                    Map<String,List<UpdateRecord>> updateRecordMap = billResult.getLastData();
                    if (updateRecordMap==null){
                        updateRecordMap = new HashMap<>();
                    }
                    if (billResult.getData().get(updateDto.getFieldCode())==null) {
                        billResult.getData().put(updateDto.getFieldCode(),"");
                    }
                    if (!billResult.getData().get(updateDto.getFieldCode()).equals(updateDto.getUpdateValue())){
                        List<UpdateRecord> updateRecordList = updateRecordMap.get(updateDto.getFieldCode());
                        if (updateRecordList==null){
                            updateRecordList = new ArrayList<>();
                        }
                        UpdateRecord updateRecord = UpdateRecord.builder()
                                .updateUser(updateUser)
                                .updateTime(updateTime)
                                .originValue(billResult.getData().get(updateDto.getFieldCode()).toString())
                                .afterValue(updateDto.getUpdateValue().toString())
                                .build();
                        updateRecordList.add(updateRecord);
                        updateCount++;
                        updateRecordMap.put(updateDto.getFieldCode(),updateRecordList);
                        billResult.getData().put(updateDto.getFieldCode(),updateDto.getUpdateValue());
                        // 不等的时候才更新，相等就不更新了
                        Query query = new Query(Criteria.where("_id").is(billResult.getId()));
                        Update update = new Update()
                                .set("data", billResult.getData())
                                .set("lastData", updateRecordMap);
                        bulkOps.updateOne(query, update);
                    }
                }
                if(updateCount==0){
                    return "本次更新成功条数："+0;
                }
                bulkOps.execute();
        }
        return "本次更新成功条数："+updateCount;
    }
    @Override
    public String audit(AuditDto auditDto){
       Update update = new Update();
       update.set("auditStatus", auditDto.getAuditStatus());
       update.set("auditUser", auditDto.getAuditUser());
       if (auditDto.getAuditStatus().equals(AuditStatusEnum.PASS_AUDIT)||
       auditDto.getAuditStatus().equals(AuditStatusEnum.REFUSE_AUDIT)){
           update.set("auditTime", new Date());
       }
       if (auditDto.getAuditStatus().equals(AuditStatusEnum.WAIT_AUDIT)){
//           if (auditDto.getAuditStatus().equals(AuditStatusEnum.PASS_AUDIT)){
//                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"审核通过后不能修改状态");
//           }
           update.set("autoAuditTime",auditDto.getAutoAuditTime());
       }
       update.set("auditComment", auditDto.getAuditComment());
       mongoTemplate.updateMulti(new Query(Criteria.where("_id").is(auditDto.getId())), update, CollectBillData.class);
        return "已审核";
    }
    @Override
    public  List<CollectBillData> listBillResultMapByTime(CollectResultDto collectResultDto){
        Query query = new Query();
        query.addCriteria(Criteria.where("tagTime").gte(collectResultDto.getTimeStart()).lte(collectResultDto.getTimeEnd())
                .and("schemeCode").is(collectResultDto.getSchemeCode()));
        if (collectResultDto.getBelongArchiveCode()!=null){
            query.addCriteria(Criteria.where("belongArchiveCode").is(collectResultDto.getBelongArchiveCode()));
        }
        List<CollectBillData> billResultList = mongoTemplate.find(query,CollectBillData.class);
//        List<String> billCodeList = billResultList.stream().map(BillData::getBillCode).collect(Collectors.toList());
//        if (billCodeList.isEmpty()){
//            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"对应时间账单数据未生成");
//        }
        return billResultList;
    }
    @XxlJob("autoAudit")
    public void autoAudit(String param){
        Long nowTime = System.currentTimeMillis();
        List<CollectBillData> collectBillDataList =
                mongoTemplate.find(new Query(Criteria.where("auditStatus")
                        .is(AuditStatusEnum.WAIT_AUDIT).and("autoAuditTime").lte(nowTime)),CollectBillData.class);
        if (collectBillDataList.isEmpty()){
            log.info("没有需要自动审核的任务,完毕");
            return;
        }
        for (CollectBillData collectBillData : collectBillDataList) {
            if (collectBillData.getAutoAuditTime()<=nowTime){
                audit(AuditDto.builder()
                        .auditComment("自动审核，审核通过")
                        .auditStatus(AuditStatusEnum.PASS_AUDIT)
                        .auditUser("AI")
                        .id(collectBillData.getId())
                        .build());
            }
        }
    }
    @Override
    public void updateBillData(CollectBillData collectBillData){
        Update update = new Update();
        for (String key : collectBillData.getData().keySet()) {
            update.set("detail."+key,collectBillData.getData().get(key));
        }
        mongoTemplate.updateFirst(new Query(Criteria.where("_id").is(collectBillData.getId())), update, CollectBillData.class);

    }
}
