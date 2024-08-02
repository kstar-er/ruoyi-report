package com.ruoyi.colorfulfog.service.table;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.colorfulfog.constant.enums.BillCheckStatusEnum;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.AddDataManualDto;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.UpdateRecord;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportTemplateVO;
import com.ruoyi.colorfulfog.model.vo.ResultNameCodeVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import com.ruoyi.common.helper.LoginHelper;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.BillMainMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BillMainServiceImpl extends ServiceImpl<BillMainMapper, BillMain> implements BillMainService {

    @Autowired
    BillResultService billResultService;

    @Autowired
    SchemeDetailService schemeDetailService;
    @Resource
    SchemeMainService schemeMainService;

    @Autowired
    DependRuleService dependRuleService;
    @Autowired
    DependDataService dependDataService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    CodeService codeService;
    @Resource
    CollectResultMainService collectResultMainService;

    private static final String DETAIL_PREFIX = "detail.";
    @Override
    public BillResultVO list(BillResultDto billResultDto, Integer currentPage, Integer pageSize){

        // 按方案编码，时间，账单编码等参数查询
        Query query = new Query();
        query.addCriteria(Criteria.where("schemeCode").is(billResultDto.getSchemeCode()));
        if (billResultDto.getBelongArchiveName()!=null){
            Pattern pattern = Pattern.compile(billResultDto.getBelongArchiveName(), Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("belongArchiveName").regex(pattern));
        }
        if (billResultDto.getCostTerm()!=null){
            query.addCriteria(Criteria.where("costTerm").is(billResultDto.getCostTerm()));
        }
        if (billResultDto.getBatchCode()!=null){
            query.addCriteria(Criteria.where("batchCode").is(billResultDto.getBatchCode()));
        }
        if (billResultDto.getBillCode()!=null){
            query.addCriteria(Criteria.where("billCode").is(billResultDto.getBillCode()));
        }
        if (billResultDto.getFieldSelectList()!=null){
            for (String key : billResultDto.getFieldSelectList().keySet()) {
                Pattern pattern = Pattern.compile(billResultDto.getFieldSelectList().get(key), Pattern.CASE_INSENSITIVE);
                query.addCriteria(Criteria.where("detail."+key).regex(pattern));
            }
        }
        long count = mongoTemplate.count(query,BillData.class);
        query.with(Sort.by(Sort.Direction.DESC, "id"));
        query.limit(pageSize).skip((long) (currentPage - 1) *pageSize);
        List<BillData> billMainList = mongoTemplate.find(query,BillData.class);
        BillResultVO billResultVO =  buildBillResultVO(billMainList,SelectTypeEnum.CALC);
        billResultVO.getResultDataList().setTotal(count);
        return billResultVO;

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
                    collectResultMainService.buildQueryCriteria(query, filterCriteria, filterCriteria.getFieldName(), filterCriteria.getValue());
                }
                catch (NoSuchFieldException e) {
                    String adjustedFieldName = DETAIL_PREFIX + filterCriteria.getFieldName();
                    collectResultMainService.buildQueryCriteria(query, filterCriteria, adjustedFieldName, filterCriteria.getValue());
                }
            }
        }
        List<BillData> billMainList = mongoTemplate.find(query,BillData.class);
        List<BaseData> baseData = new ArrayList<>(billMainList);
        return getAggregatedDataMap(dataSourceDTO, baseData);
    }

    @NotNull
    @Override
    public Map<String, List<Map<String, Double>>> getAggregatedDataMap(DataSourceDTO dataSourceDTO, List<BaseData> baseDataList) {
        Map<String, List<Map<String, Double>>> aggregatedDataMap = new LinkedHashMap<>();
        String xAxis = dataSourceDTO.getXAxis();
        List<String> yAxis = dataSourceDTO.getYAxis();
        //把billMainList分成时间段，每1天聚合成一条数据 2
        if (dataSourceDTO.getXAxisIsTime())
        {
            if (dataSourceDTO.getTimeUnit().equals("天")){
                //先得到本月的天数
                int daysInMonth = LocalDate.of(dataSourceDTO.getYear(), dataSourceDTO.getMonth(),1).lengthOfMonth();
                for (int i = 1; i <= daysInMonth; i++) {
                    //得到每天开始和结束的时间戳
                    LocalDateTime startTime = LocalDate.of(dataSourceDTO.getYear(), dataSourceDTO.getMonth(), 1).withDayOfMonth(i).atStartOfDay();
                    long startTimeStamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                    LocalDateTime endTime = startTime.plusDays(1);
                    long endTimeStamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
                    List<BaseData> baseDatas = getBillDataList(baseDataList, xAxis, startTimeStamp, endTimeStamp);
                    List<Map<String, Double>> map = new ArrayList<>();
                    //为了前端更好显示，先默认构造一遍
                    for (String yAxisItem : yAxis) {
                        Map<String, Double> mapItem = new HashMap<>();
                        mapItem.put(yAxisItem, 0.0);
                        map.add(mapItem);
                    }
                    aggregatedDataMap.put(String.valueOf(i), map);
                    //循环billDataList
                    disposeBillDataList(yAxis, aggregatedDataMap, String.valueOf(i), baseDatas);
                }
            }else if (dataSourceDTO.getTimeUnit().equals("月")){
                int monthsInYear = 12;
                for (int i = 1; i <= monthsInYear; i++) {
                    LocalDate startDate = LocalDate.of(dataSourceDTO.getYear(), i, 1);
                    long startTimeStamp = startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
                    LocalDate endDate = startDate.plusMonths(1);
                    long endTimeStamp = endDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
                    List<BaseData> baseDatas = getBillDataList(baseDataList, xAxis, startTimeStamp, endTimeStamp);
                    //默认先构造一遍
                    List<Map<String, Double>> map = new ArrayList<>();
                    for (String yAxisItem : yAxis) {
                        Map<String, Double> mapItem = new HashMap<>();
                        mapItem.put(yAxisItem, 0.0);
                        map.add(mapItem);
                    }
                    aggregatedDataMap.put(dataSourceDTO.getYear()+"-"+i,map);
                    disposeBillDataList(yAxis, aggregatedDataMap, dataSourceDTO.getYear()+"-"+i, baseDatas);
                }
            }
        }else{
            for (BaseData data : baseDataList) {
                try {
                    Map<String, Object> dataData = data.getData();
                    String xKey = (String)dataData.get(xAxis);
                    if (xKey == null){
                        Field field = BaseData.class.getDeclaredField(xAxis);
                        field.setAccessible(true); // 允许访问私有字段
                        xKey = (String)field.get(data);
                    }
                    // 遍历y轴字段，累加每个字段的值
                    for (String yField : yAxis) {
                        List<Map<String, Double>> mapList = aggregatedDataMap.computeIfAbsent(xKey, k -> new ArrayList<>());
                        sumYField(data,yField,mapList,dataData );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        aggregatedDataMap.entrySet().removeIf(entry -> entry.getKey() == null);
        return aggregatedDataMap;
    }

    private static List<BaseData> getBillDataList(List<BaseData> baseDataList, String xAxis, long startTime, long endTime) {
        List<BaseData> billDataList=new ArrayList<>();
        for (BaseData arr: baseDataList
        ) {
            //判断X轴字段，是否位于主表上
            Map<String, Object> dataData = arr.getData();
            Object xAxisValue = dataData.get(xAxis);
            long xKey;
            if (ObjectUtil.isNull(xAxisValue)){
                Field field;
                try {
                    field = BaseData.class.getDeclaredField(xAxis);
                    field.setAccessible(true); // 允许访问私有字段
                    xKey = (long)field.get(arr);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }else{
                 xKey = Long.parseLong((String) xAxisValue);
            }
            if (xKey >= startTime&& xKey < endTime){
                billDataList.add(arr);
            }
        }
        return billDataList;
    }

    private void disposeBillDataList(List<String> yAxis, Map<String, List<Map<String, Double>>> aggregatedDataMap, String i, List<BaseData> baseDataList) {
        for (BaseData data : baseDataList) {
            try {
                Map<String, Object> dataData = data.getData();
                // 遍历y轴字段，累加每个字段的值
                for (String yField : yAxis) {
                    List<Map<String, Double>> mapList = aggregatedDataMap.computeIfAbsent(i, k -> new ArrayList<>());
                    sumYField(data,yField,mapList,dataData );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void sumYField(BaseData data, String yField, List<Map<String, Double>> mapList, Map<String, Object> dataData) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Double> aggregatedData= new HashMap<>();
        double currentSum=0.0;
        boolean flag=true;
        if (mapList.isEmpty()){
            aggregatedData.put(yField, 0.0);
            mapList.add(aggregatedData);
            flag=false;
        }
        for (Map<String, Double> map : mapList) {
            if (map.containsKey(yField)) {
                aggregatedData = map;
                currentSum = map.get(yField);
                flag=false;
            }
        }
        String object = (String) dataData.get(yField);
        if (object!=null){
            double value = Double.parseDouble(object);
            currentSum += value;
        }else{
            Field fieldTwo = BaseData.class.getDeclaredField(yField);
            fieldTwo.setAccessible(true); // 允许访问私有字段
            double fieldValue = fieldTwo.getDouble(data);
            currentSum += fieldValue;
        }
        aggregatedData.put(yField, currentSum);
        if (flag){
            mapList.add(aggregatedData);
        }
    }

    @Override
    public BillResultVO buildBillResultVO(List<BillData> billMainList, SelectTypeEnum selectTypeEnum){
        if (billMainList==null || billMainList.isEmpty()){
            throw new RuntimeException("没有数据可以导出或者生成");
        }
        List<String> schemeCodeList = billMainList.stream().map(BillData::getSchemeCode).collect(Collectors.toList());
        List<SchemeDetail> schemeDetails = schemeDetailService.listSchemeDetailBySchemeCode(schemeCodeList,selectTypeEnum );
        List<ResultNameCodeVO> resultNameCodeVOList = schemeDetails.stream()
                .map(billResult -> new ResultNameCodeVO(billResult.getResultName(), billResult.getResultCode(),billResult.getResultType(),billResult.getDisplayOrder(),billResult.getDecimal()))
                .collect(Collectors.toList());
        resultNameCodeVOList = resultNameCodeVOList.stream().sorted(Comparator.comparing(ResultNameCodeVO::getDisplayOrder)).collect(Collectors.toList());;
        BillResultVO billResultVO = new BillResultVO();
        billResultVO.setResultNameList(resultNameCodeVOList);
        billResultVO.setResultDataList(new PageInfo<>(billMainList));
        return billResultVO;
    }

    @Override
    public List<BillData> listByCollectCode(List<String> collectCode){
       return mongoTemplate.find(new Query(Criteria.where("collectResultCode").in(collectCode)),BillData.class);
    }

    @Transactional
    @Override
    public String setInValid(List<String> billCode){
        String updateUser = LoginHelper.getUsername();
        String updateTime = TimeUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        List<BillData> billDataList = billResultService.listByBillCode(billCode);
        long updateCount =0;
        if (billDataList.isEmpty()){
            return "没有数据可以设为无效";
        }
        List<String> needFlashCollectCode = billDataList.stream().map(BillData::getCollectResultCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BillData.class);
        for (BillData billData : billDataList) {
            Map<String,List<UpdateRecord>> updateRecordMap = billData.getLastData();
            if (updateRecordMap==null){
                updateRecordMap = new HashMap<>();
            }
            List<UpdateRecord> updateRecordList = updateRecordMap.get("status");
            if (updateRecordList==null){
                updateRecordList = new ArrayList<>();
            }
            UpdateRecord updateRecord = UpdateRecord.builder()
                    .updateUser(updateUser)
                    .updateTime(updateTime)
                    .originValue(String.valueOf(billData.getStatus()))
                    .afterValue(String.valueOf(BillCheckStatusEnum.HAVE_DELETED))
                    .build();
            updateRecordList.add(updateRecord);
            updateRecordMap.put("status",updateRecordList);
            updateRecordList = updateRecordMap.get("collectResultCode");
            if (updateRecordList==null){
                updateRecordList = new ArrayList<>();
            }
            updateRecord = UpdateRecord.builder()
                    .updateUser(updateUser)
                    .updateTime(updateTime)
                    .originValue(billData.getCollectResultCode())
                    .afterValue(" ")
                    .build();
            updateRecordList.add(updateRecord);
            updateRecordMap.put("collectResultCode",updateRecordList);
            updateCount++;
            Update update = new Update()
                    .set("status", BillCheckStatusEnum.HAVE_DELETED)
                    .set("collectResultCode"," ")
                    .set("lastData",updateRecordMap);
            Query query = new Query(Criteria.where("_id").is(billData.getId()));
            bulkOps.updateOne(query, update);
        }
        bulkOps.execute();
        for (String code : needFlashCollectCode) {
            schemeMainService.flashByCollectCode(BillResultFlashDto.builder().collectResultCode(code).build());
        }
        return updateCount+"条数据设为无效";
    }

    @Override
    public void addDataManual(List<AddDataManualDto> addDataManualDtoList){
        checkManualData(addDataManualDtoList);
        String batchCode = codeService.getCode(IdTypeEnum.RESULT_BATCH_CODE);
        List<String> billCode = codeService.getCode(IdTypeEnum.COST_BILL,addDataManualDtoList.size());
        Integer manualFlag = 1;
        String schemeCode = addDataManualDtoList.get(0).getSchemeCode();
        SchemeMain schemeMain = schemeMainService.getSchemeMainByCode(schemeCode);
        List<BillData> billDataList = new ArrayList<>();
        int i = 0;
        for (AddDataManualDto addDataManualDto : addDataManualDtoList) {
            BillData billData = new BillData();
            billData.setSchemeCode(schemeCode);
            billData.setBatchCode(batchCode);
            billData.setBillCode(billCode.get(i++));
            billData.setManualFlag(manualFlag);
            billData.setStatus(BillCheckStatusEnum.NORMAL);
            billData.setTagTime((Long) addDataManualDto.getData().get(schemeMain.getTimeFieldResultCode()));
            billData.setBillType(schemeMain.getBillType());
            billData.setBelongArchiveCode(addDataManualDto.getBelongArchiveCode());
            billData.setBelongArchiveName(addDataManualDto.getBelongArchiveName());
            billData.setCollectResultCode(addDataManualDto.getCollectResultCode());
            billData.setData(addDataManualDto.getData());
            billDataList.add(billData);
        }
        mongoTemplate.insertAll(billDataList);
    }
    private void checkManualData(List<AddDataManualDto> addDataManualDtoList){
        if (addDataManualDtoList.size()<1){
            throw new GlobalException("请至少导入一条数据");
        }
        List<ExportTemplateVO> exportTemplateVOList = schemeDetailService.exportTemplate(addDataManualDtoList.get(0).getSchemeCode());
        exportTemplateVOList = exportTemplateVOList.stream().filter(s->s.getIsRequired()!=null&& s.getIsRequired()).collect(Collectors.toList());
        List<String> errList = new ArrayList<>();
        for (AddDataManualDto addDataManualDto : addDataManualDtoList) {
            for (ExportTemplateVO exportTemplateVO : exportTemplateVOList) {
                if (addDataManualDto.getData().get(exportTemplateVO.getResultCode()) == null) {
                    errList.add(exportTemplateVO.getResultName());
                }
            }
        }
        errList = errList.stream().distinct().collect(Collectors.toList());
        if (errList.size()>0){
            throw new GlobalException("请检查手动录入数据,", errList+"为必填项");
        }
    }
}



