package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.SysConstant;
import com.ruoyi.colorfulfog.constant.enums.*;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.entity.ExpressionMatchEntity;
import com.ruoyi.colorfulfog.model.entity.QueueTableEntity;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.vo.*;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.ruoyi.colorfulfog.utils.JEPUtils;
import com.ruoyi.colorfulfog.utils.SqlUtils;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Range;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.SchemeMainMapper;

@Service
@Slf4j
public class SchemeMainServiceImpl extends ServiceImpl<SchemeMainMapper, SchemeMain> implements SchemeMainService {

    @Resource
    SchemeDetailService schemeDetailService;

    @Autowired
    private DataSourceService dataSourceService;

    @Resource
    OrderTableRelationService orderTableRelationService;


    @Resource
    BillResultService billResultService;
    @Resource
    DependRuleService dependRuleService;
    @Resource
    DependMainService dependMainService;
    @Resource
    DependDataService dependDataService;
    @Resource
    BillMainService billMainService;
    @Resource
    ConditionService conditionService;
    @Resource
    SchemeUserRelationService schemeUserRelationService;
    @Resource
    CodeService codeService;
    @Resource
    ErrReasonService errReasonService;

    @Resource
    TableFieldRelationService tableFieldRelationService;

    @Resource
    ForeignKeyService foreignKeyService;
    @Autowired
    MongoTemplate mongoTemplate;

    @Resource
    CollectSchemeMainService collectSchemeMainService;
    @Override
    public TestBillResultOriginVO startCreateBill(List<String> schemeMainList, Integer testFlag){
        return startCreateBill(schemeMainList,testFlag,null);
    }
    @Override
    public TestBillResultOriginVO startCreateBill(List<String> schemeMainList, Integer testFlag,TimeDto timeDto) {
        List<String> batchCodeList = codeService.getCode(IdTypeEnum.RESULT_BATCH_CODE, schemeMainList.size());
        int batchCodeIndex = 0;
        // 获取对应的计划的明细表
        List<SchemeDetail> schemeDetailList = schemeDetailService.listSchemeDetailBySchemeCode(schemeMainList, SelectTypeEnum.CALC);
        // 按照计划的code进行分组
        Map<String, List<SchemeDetail>> schemeDetailMap = schemeDetailList.
                stream().collect(Collectors.groupingBy(SchemeDetail::getSchemeCode));
        // 获得所有单据的名称
        List<String> orderTable = schemeDetailList.stream().distinct().map(SchemeDetail::getOrderTable).collect(Collectors.toList());
        // 拿到所有单据的关系表
        List<OrderTableRelation> orderTableRelationList = orderTableRelationService.listByOrderName(orderTable);
        // 获得所有表的id，通过id去拿外键关系表
        List<Long> tableIdList = orderTableRelationList.stream().map(OrderTableRelation::getId).collect(Collectors.toList());
        Map<String,Map<String,ForeignKey>> foreignKeyMap = foreignKeyService.getForeignKeyMap(tableIdList);
        Map<String, List<OrderTableRelation>> orderTableRelationMap = orderTableRelationList
                .stream().collect(Collectors.groupingBy(OrderTableRelation::getOrderTable));
        // 构建外键Map，Key是tableName,value是外键
        Map<String, SchemeMain> stringSchemeMainMap = getSchemeMainMap(schemeMainList);

        Map<String, Map<String, Object>> billResultMap = new HashMap<>();
        // 获得哪些用户使用当前的规则
        List<SchemeUserRelation> schemeUserRelationList = schemeUserRelationService.listBySchemeCode(schemeMainList);
        Map<String, List<SchemeUserRelation>> schemeUserRelationMap = schemeUserRelationList.stream().collect(Collectors.groupingBy(SchemeUserRelation::getSchemeCode));
        // 将schemeUserRelationMap重新分组，以schemeCode-archiveUserCode为Key
        Map<String, Map<String, List<SchemeUserRelation>>> schemeUserRelationMap2 = schemeUserRelationList
                .stream().collect(Collectors.groupingBy(SchemeUserRelation::getSchemeCode,
                        Collectors.groupingBy(SchemeUserRelation::getArchiveUserCode)));
        TestBillResultOriginVO testBillResultVO = new TestBillResultOriginVO();
        List<ErrReason> dependErrReasonList = new ArrayList<>();
        List<BillData> billDataList = new ArrayList<>();
        for (String schemeCode : schemeMainList) {
            SchemeMain schemeMain = stringSchemeMainMap.get(schemeCode);
            List<String> firstDependCode = schemeDetailMap.get(schemeCode).stream().map(SchemeDetail::getDependCode).collect(Collectors.toList());
            Map<String,List<SchemeDetail>> tableSchemeDetailMap = schemeDetailMap.get(schemeCode).stream().filter(s->s.getOrderTable()!=null).collect(Collectors.groupingBy(SchemeDetail::getOrderTable));
            // 通过第一层的code深度搜索获得所有的表对应的规则
            List<DependRule> dependRuleList = dependRuleService.getDependRuleByCode(firstDependCode);
            if (CollectionUtils.isEmpty(dependRuleList)) {
                dependRuleList = new ArrayList<>();
            }
            Map<String, DependMain> dependMainMap = dependMainService.getDependMainMap(dependRuleList.stream()
                    .map(DependRule::getDependCode).distinct().collect(Collectors.toList()));
            Map<String, List<DependData>> dependDataMap = dependDataService.getDependDataMap(dependRuleList.stream()
                    .map(DependRule::getDependCode).collect(Collectors.toList()));
            // 将分类依赖里面的key再map一遍
            Map<String, Map<String, DependData>> dependDataMapMap = getDependDateMapMap(dependDataMap);
            Map<String, Condition> tableConditionMap =
                    conditionService.getConditionMap(schemeCode);
            List<SchemeUserRelation> schemeUserRelations = schemeUserRelationMap.get(schemeCode);
            // 获得当前已有的数据中，是否已有商家的数据，如果已有的商家，去掉不生成
            if (CollectionUtils.isEmpty(schemeUserRelations)) {
                log.info("没有用户使用该规则，应该暂停该规则的调用");
                continue;
            }
            // 将dependRuleList按照depend_code进行分组
            Map<String, List<DependRule>> dependRuleMap = dependRuleList.stream().collect(Collectors.groupingBy(DependRule::getDependCode));

            // 获得所有单据的数据
            Map<String, List<Map<String, Object>>> orderDataList = getAllOrderData(schemeDetailMap.get(schemeCode), orderTableRelationMap,foreignKeyMap,
                    dependRuleList, tableConditionMap, schemeUserRelations, schemeMain, testFlag,timeDto);
            String granularity = schemeMain.getGranularity();
            // 将数据按照粒度重新拆分为list，一个list中的元素生成一条账单
            List<Map<String, Map<String, Object>>> granularityDataList = getValueFromMap(orderDataList, granularity,
                    orderTableRelationMap,foreignKeyMap,tableSchemeDetailMap);

            if (testFlag>0) {
                testBillResultVO.setOriginData(granularityDataList);
            }

            List<String> mainCodeList = codeService.getCode(IdTypeEnum.COST_BILL, granularityDataList.size());
            // 将expSchemeDetailList按照SchemeDetail 中的id进行升序排序
            List<SchemeDetail> sequenceSchemeDetailList =
                    schemeDetailMap.get(schemeCode).stream()
                            .sorted(Comparator.comparing(SchemeDetail::getCalculateOrder))
                            .collect(Collectors.toList());
            // 循环所有的计划明细，按字段生成数据,将单个字段进行分组，以字段的code为依据
            int j = 0;// 控制生成主表的数量的计数
            BillCheckStatusEnum billMainStatus ;
            Map<String,BillData> billDataHashMap = new HashMap<>();
            for (SchemeDetail schemeDetail : sequenceSchemeDetailList) {
                billMainStatus = BillCheckStatusEnum.WAIT_GENERATE;
                int i = 0;
                // 进入到需要预处理数据的schemeDetail时，在这一层做数据预处理
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.FOR_MUL_TAG)) {
                    calculateAndAddTagFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.SUM)) {
                    calculateAndAddSumFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.COUNT)){
                    calculateCountFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.MAX)||
                       schemeDetail.getType().equals(SchemeDetailParamEnum.MIN)){
                    calculateMaxOrMinFiled(schemeDetail, billResultMap);
                }
                for (Map<String, Map<String, Object>> granularityDataMap : granularityDataList) {
                    // 生成一个唯一且按顺序的值作为billCode
                    String billCode = mainCodeList.get(i++);
                    billResultMap.computeIfAbsent(billCode, k -> new HashMap<>());
                    billResultMap.get(billCode).put("billCode",billCode);
                    // 如果之前没有塞过这个code的话，注入一个空的map进去
                    billDataHashMap.computeIfAbsent(billCode, k -> BillData.builder().data(new HashMap<>()).build());
                    BillResult billResult = calculateResultBySchemeDetail(schemeMain,schemeDetail, granularityDataMap,
                            dependRuleMap, dependDataMap, dependDataMapMap, dependMainMap, billResultMap.get(billCode), dependErrReasonList);
                    billResult.setBillCode(billCode);
                    billResult.setBatchCode(batchCodeList.get(batchCodeIndex));
                    if (billResult.getStatus().equals(BillResult.CalculateStatusEnum.FAIL.getStatus())){
                        billMainStatus= BillCheckStatusEnum.CAN_NOT_GENERATE;
                    }
                    billDataHashMap.get(billCode).getData().put(billResult.getResultCode(),billResult.getValue());
                    // 判空并添加新的map进去的方法

                    billResultMap.get(billCode).put(billResult.getResultCode(), billResult.getValue());

                    if (j == 0) { // 第一次的时候才创建主表
                        String belongArchiveCode = granularityDataMap.get(schemeMain.getBelongRelTableName()).get(schemeMain.getBelongRelTableField()).toString();
                        SchemeUserRelation schemeUserRelation = schemeUserRelationMap2.get(schemeCode).get(belongArchiveCode).get(0);
                        BillMain billMain = BillMain.builder()
                                .billCode(billCode)
                                .schemeCode(schemeCode)
                                .batchCode(batchCodeList.get(batchCodeIndex))
                                .status(billMainStatus)
                                .billType(schemeMain.getBillType())
                                .belongArchiveCode(belongArchiveCode)
                                .belongUserId(schemeUserRelation.getUserId())
                                .belongArchiveName(schemeUserRelation.getArchiveUserName())
                                .build();
                        BeanUtils.copyProperties(billMain,billDataHashMap.get(billCode));
                        billDataList.add(billDataHashMap.get(billCode));
                    }
                }
                j++;
            }
            int i =0 ;
            for (BillData billData : billDataList) {
                String billCode = mainCodeList.get(i++);
                billDataHashMap.get(billCode).setTagTime(Long.parseLong(billData.getData().get(schemeMain.getTimeFieldResultCode()).toString()));
            }
            batchCodeIndex++;
        }


        if (!dependErrReasonList.isEmpty()) {
            if (testFlag==0) {
                // 还需要和现有的表的错误数据做去重处理
                errReasonService.saveErrReasonBatch(dependErrReasonList);
            }
        }
        if (testFlag>0) {
            testBillResultVO.setBillDataList(billDataList);
            return testBillResultVO;
        }
        mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,BillData.class).insert(billDataList).execute();
        return null;
    }

    /**
     * 判断用户是否已经生成数据
     * @param schemeUserRelationList
     */
    @Override
    public List<SchemeUserRelation> checkUserData(List<SchemeUserRelation> schemeUserRelationList, TimeDto timeDto,String schemeCode, CollectDataTypeEnum collectDataTypeEnum,Integer testFlag){
        if (testFlag>0){
            return schemeUserRelationList;
        }
        List<Query> queryList = new ArrayList<>();
        for (SchemeUserRelation schemeUserRelation : schemeUserRelationList) {
            Query query = new Query();
            query.addCriteria(Criteria.where("schemeCode").is(schemeCode));
            query.addCriteria(Criteria.where("tagTime").gte(timeDto.getStartTime()).lte(timeDto.getEndTime()));
            query.addCriteria(Criteria.where("belongArchiveCode").is(schemeUserRelation.getArchiveUserCode()    ));
            query.limit(1);
            queryList.add(query);
        }


        List<BaseData> baseData =new ArrayList<>() ;
        if (collectDataTypeEnum.equals(CollectDataTypeEnum.SCHEME)){
            for (Query query : queryList) {
                List<BillData> billDataList = mongoTemplate.find(query,BillData.class);
                if (billDataList.isEmpty()){
                    continue;
                }
                baseData.add(billDataList.get(0));
            }
        }
        else if (collectDataTypeEnum.equals(CollectDataTypeEnum.COLLECT)){
            for (Query query : queryList) {
                List<CollectBillData> billDataList = mongoTemplate.find(query,CollectBillData.class);
                if (billDataList.isEmpty()){
                    continue;
                }
                baseData.add(billDataList.get(0));
            }
        }
        if (baseData.isEmpty()){
            return schemeUserRelationList;
        }
        List<String> archiveCodeList = baseData.stream().map(BaseData::getBelongArchiveCode).collect(Collectors.toList());
        return schemeUserRelationList.stream().filter(schemeUserRelation -> !archiveCodeList.contains(schemeUserRelation.getArchiveUserCode())).collect(Collectors.toList());
    }

    /**
     * 根据计划的明细，对数据进行预汇总处理，在对应的表数据中，添加汇总的字段的值
     *
     * @param schemeDetail
     * @param billResultAndBillCodeMap
     */
    private void calculateAndAddSumFiled(SchemeDetail schemeDetail, Map<String, Map<String, Object>> billResultAndBillCodeMap) {
        Map<String, BigDecimal> sumMap = new HashMap<>();// 汇总的临时map，key是resultCode+groupByField;
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField());
            BigDecimal value = sumMap.get(key);
            if (value == null) {
                value = new BigDecimal(0);
            }
            String tmp = billResultMap.get(schemeDetail.getOrderField()).toString();
            // 将String类型转换为Big
            try {
                value = value.add(BigDecimal.valueOf(Double.parseDouble(tmp)));
            }catch (Exception e){
                // 转换失败的话
                value = null;
            }
            sumMap.put(key, value);
        }
        // 将sumMap的value转换为String格式
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            if (sumMap.get(key)==null){
                billResultMap.put(key,null);
                continue;
            }
            billResultMap.put(key, sumMap.get(key).toString());
            if (schemeDetail.getOneRowTag()){
                sumMap.put(key,new BigDecimal(schemeDetail.getOtherRowValue()));
            }
        }
    }
    private void calculateMaxOrMinFiled(SchemeDetail schemeDetail, Map<String, Map<String, Object>> billResultAndBillCodeMap) {
        Map<String, BigDecimal> sumMap = new HashMap<>();// 汇总的临时map，key是resultCode+groupByField;
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            BigDecimal value = sumMap.get(key);
            if (value == null) {
                value = new BigDecimal(0);
            }
            String tmp = billResultMap.get(schemeDetail.getOrderField()).toString();
            // 将String类型转换为Big
            try {
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.MIN)){
                    value = value.min(BigDecimal.valueOf(Double.parseDouble(tmp)));
                }
                else if (schemeDetail.getType().equals(SchemeDetailParamEnum.MAX)){
                    value = value.max(BigDecimal.valueOf(Double.parseDouble(tmp)));
                }
            }catch (Exception e){
                log.info("转换失败");
                value=null;
            }

            sumMap.put(key, value);
        }
        // 将sumMap的value转换为String格式
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            if (sumMap.get(key)==null){
                billResultMap.put(key,null);
                continue;
            }
            billResultMap.put(key, sumMap.get(key).toString());
            if (schemeDetail.getOneRowTag()){
                sumMap.put(key,new BigDecimal(schemeDetail.getOtherRowValue()));
            }
        }
    }

    private void calculateCountFiled(SchemeDetail schemeDetail, Map<String, Map<String, Object>> billResultAndBillCodeMap) {
        Map<String, Set<String>> sumMap = new HashMap<>();// 汇总的临时map，key是resultCode+groupByField;
        Map<String,Integer> countMap=new HashMap<>();
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            Set<String> lastSet = sumMap.get(key);
            if (lastSet == null) {
                lastSet = new HashSet<>();
            }
            String tmp = billResultMap.get(schemeDetail.getOrderField()).toString();
            lastSet.add(tmp);
            // 将String类型转换为Big
            sumMap.put(key, lastSet);
            countMap.put(key,lastSet.size());
        }
        // 将sumMap的value转换为String格式
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            billResultMap.put(key,countMap.get(key));
            if (schemeDetail.getOneRowTag()){
                countMap.put(key, Integer.valueOf(schemeDetail.getOtherRowValue()));
            }
        }
    }

    private void calculateAndAddTagFiled(SchemeDetail schemeDetail,
                                         Map<String, Map<String, Object>> billResultAndBillCodeMap) {
        Map<String, List<String>> listMap = new HashMap<>();// 汇总的临时map，key是resultCode+groupByField;
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            List<String> value = listMap.get(key);
            if (value == null) {
                value = new ArrayList<>();
            }
            value.add(billResultMap.get(schemeDetail.getOrderField()).toString());
            listMap.put(key, value);
        }
        String type = schemeDetail.getExpression(); // 获取匹配的方式
        ExpressionMatchEntity expressionMatchEntity = new ExpressionMatchEntity(type);
        for (String billCode : billResultAndBillCodeMap.keySet()) {
            Map<String, Object> billResultMap = billResultAndBillCodeMap.get(billCode);
            String key = schemeDetail.getResultCode() + '-' +
                    billResultMap.get(schemeDetail.getGroupByField()).toString();
            List<String> dataList = listMap.get(key);
            if (schemeDetail.getOneRowTag()){
                listMap.put(key, null);
            }
            if (dataList==null){
                billResultMap.put(key,schemeDetail.getOtherRowValue());
            }else {
                billResultMap.put(key, expressionMatchEntity.match(dataList));
            }
        }
    }

    /**
     * 根据每个结果字段对应的规则列表计算单个字段，计算完成之后再赋值同一个规则的code，合并成一条账单记录
     *
     * @param schemeDetail
     * @param orderData
     * @return
     */
    private BillResult calculateResultBySchemeDetail(SchemeMain schemeMain,SchemeDetail schemeDetail,
                                                     Map<String, Map<String, Object>> orderData,
                                                     Map<String, List<DependRule>> dependRuleMap,
                                                     Map<String, List<DependData>> dependDataMap,
                                                     Map<String, Map<String, DependData>> dependDataMapMap,
                                                     Map<String, DependMain> dependMainMap,
                                                     Map<String, Object> billResultMap,
                                                     List<ErrReason> dependErrReasonList) {
        BigDecimal resultValue = BigDecimal.ZERO;
        BillResult.CalculateStatusEnum calculateStatusEnum = BillResult.CalculateStatusEnum.SUCCESS;
        // 只有一个参数的话，不需要计算，直接赋值
        String value = null;
        String key = "";
        CalculateValueVO calculateValueVO;
        Object object;
        switch (schemeDetail.getType()) {
            case DEPEND:
                calculateValueVO = calculateDependValue(schemeMain,schemeDetail, orderData, dependDataMap, dependDataMapMap,
                        dependRuleMap, dependMainMap, schemeDetail.getDependCode(), billResultMap, dependErrReasonList);
                value = calculateValueVO.getValue();
                calculateStatusEnum = calculateValueVO.getCalculateStatusEnum();
                break;
            case FIXED:
                value = schemeDetail.getFixedCoefficient();
                break;
            case EQUATION:
                calculateValueVO = JEPUtils.calculate(schemeDetail.getExpression(), billResultMap);
                value = calculateValueVO.getValue();
                calculateStatusEnum = calculateValueVO.getCalculateStatusEnum();

                break;
            case SUM:
            case FOR_MUL_TAG:
            case COUNT:
            case MAX:
            case MIN:
            case SINGLE_TAG:
                key = schemeDetail.getResultCode() + '-' + billResultMap.get(schemeDetail.getGroupByField()).toString();
                if (billResultMap.get(key)==null){
                    value="["+schemeDetail.getOrderField()+"]计算失败";
                }else {
                    value = billResultMap.get(key).toString();
                }
                break;
            case ORDER_DATA:
                try{
                     object = orderData.get(schemeDetail.getOrderTable()).get(schemeDetail.getOrderField());
                }catch (Exception e){
                    throw new GlobalException("计算失败，无法获取数据: " + schemeDetail.getOrderTable()+"."+schemeDetail.getOrderField()+",无法获取");
                }
                if (object == null) {
                    value = "NULL";
                    // 如果是null的话检查资格字段是否参与后续的计算，如果参与了后续的计算，才设为FAIL
                    if (schemeDetail.getCalculatedTag()){
                        calculateStatusEnum = BillResult.CalculateStatusEnum.FAIL;
                    }
                    break;
                }
                value = object.toString();
                break;
            case REGEX:
                // todo 这里还需要更新取数的判断，可以从前面的字段中取数
                calculateValueVO = calculateRegex(billResultMap.get(schemeDetail.getOrderField())
                        .toString(), schemeDetail.getExpression());
                value = calculateValueVO.getValue();
                calculateStatusEnum = calculateValueVO.getCalculateStatusEnum();
                break;
            default:
                throw new GlobalException("未知的参数类型");
        }
        return BillResult.builder()
                .type(schemeDetail.getResultType())
                .schemeCode(schemeDetail.getSchemeCode())
                .resultName(schemeDetail.getResultName())
                .resultCode(schemeDetail.getResultCode())
                .status(calculateStatusEnum.getStatus())
                .value(value)
                .displayOrder(schemeDetail.getDisplayOrder())
                .build();
    }

    /**
     * 计算正则表达返回的值
     *
     * @return
     */
    private CalculateValueVO calculateRegex(String originValue, String expression) {
        Matcher matcher = Pattern.compile(expression).matcher(originValue);
        // 判断是否可以找到匹配正则表达式的字符
        if (matcher.find()) {
            // 将匹配当前正则表达式的字符串即文件名称进行赋值
            return CalculateValueVO.builder()
                    .value(matcher.group())
                    .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                    .build();

        }
        return CalculateValueVO.builder()
                .value(String.format("解析失败：%s", originValue))
                .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                .build();

    }

    /**
     * 计算根据依赖表获得的数值，依赖表有两种，一种是根据范围，一种是根据分类，还有一种二次依赖
     *
     * @param orderData
     * @return
     */
    private CalculateValueVO calculateDependValue(SchemeMain schemeMain,SchemeDetail schemeDetail, Map<String, Map<String, Object>> orderData,
                                                  Map<String, List<DependData>> dependDataMap,
                                                  Map<String, Map<String, DependData>> dependDataMapMap,
                                                  Map<String, List<DependRule>> dependRuleMap,
                                                  Map<String, DependMain> dependMainMap, String dependCode,
                                                  Map<String, Object> billResultMap,
                                                  List<ErrReason> dependErrReasonList) {
        // 判断需要计算的规则是按分类来进行还是范围进行
        DependMain dependMain = dependMainMap.get(dependCode);
        if (dependMain.getDependType().equals(DependTypeEnum.SORT)) {
            return categoryDependValue(schemeMain,schemeDetail, orderData, dependDataMap, dependDataMapMap, dependRuleMap, dependMainMap, dependCode, billResultMap, dependErrReasonList);
        } else if (dependMain.getDependType().equals(DependTypeEnum.RANGE)) {
            return rangeDependValue(schemeMain,schemeDetail, orderData, dependDataMap, dependDataMapMap, dependRuleMap, dependMainMap, dependCode, billResultMap, dependErrReasonList);
        }
        throw new RuntimeException(dependMain.getDependType() + "未知的依赖类型");
    }

    private CalculateValueVO rangeDependValue(SchemeMain schemeMain,SchemeDetail
                                                      schemeDetail, Map<String, Map<String, Object>> orderData,
                                              Map<String, List<DependData>> dependDataMap,
                                              Map<String, Map<String, DependData>> dependDataMapMap,
                                              Map<String, List<DependRule>> dependRuleMap,
                                              Map<String, DependMain> dependMainMap, String dependCode,
                                              Map<String, Object> billResultMap,
                                              List<ErrReason> dependErrReasonList) {
        // 根据schemeCode进行分类，
        List<DependRule> dependRuleList = dependRuleMap.get(dependCode);
        DependRule dependRule = new DependRule();
        for (DependRule dependRule1 : dependRuleList) {
            if (dependRule1.getSchemeCode().equals(schemeDetail.getSchemeCode())) {
                dependRule = dependRule1;
                break;
            }
        }

        List<DependData> dependDataList = dependDataMap.get(dependRule.getDependCode());
        String value = null;
        if (dependRule.getDependType().equals(DependTypeEnum.DEPEND)) {
            // 存在二次依赖的话要递归调用依赖计算对应的值
            value = calculateDependValue(schemeMain,schemeDetail, orderData, dependDataMap, dependDataMapMap,
                    dependRuleMap, dependMainMap, dependCode, billResultMap, dependErrReasonList).getValue();
        } else {
            // 如果数据来源是scheme的话，从resultMap中取数
            if (dependRule.getGetDataFrom().equals(GetDataFromType.SCHEME)) {
                value = billResultMap.get(dependRule.getOrderField()).toString();
            } else {// 否则，直接从orderData中获取数据
                value = orderData.get(dependRule.getOrderTable()).get(dependRule.getOrderField()).toString();
            }
        }
        BigDecimal bigValue = new BigDecimal(value);
        BigDecimal startValue = null;
        BigDecimal endValue = null;
        // 在dependDataList中找到符合的bigValue在rangeStart和rangeEnd之间的value
        for (DependData dependData : dependDataList) {
            int closeOpenNum = 0;//判断开闭区间的数字，00,01,10,11,四个值对应四种类型
            if (dependData.getRangeStart().charAt(0) == '[') {
                closeOpenNum += 2;
            }
            if (dependData.getRangeEnd().charAt(dependData.getRangeEnd().length() - 1) == ']') {
                closeOpenNum += 1;
            }
            // 取出dependData.getRangeStart()中数字以及小数点的部分
            startValue = new BigDecimal(dependData.getRangeStart().replaceAll("[^0-9.]", ""));
            endValue = new BigDecimal(dependData.getRangeEnd().replaceAll("[^0-9.]", ""));
            Range<BigDecimal> range = null;
            switch (closeOpenNum) {
                case 0:
                    range = Range.open(startValue, endValue);
                    break;
                case 1:
                    range = Range.openClosed(startValue, endValue);
                    break;
                case 2:
                    range = Range.closedOpen(startValue, endValue);
                    break;
                case 3:
                    range = Range.closed(startValue, endValue);
                    break;
            }
            if (range.contains(bigValue)) {
                return CalculateValueVO.builder()
                        .value(dependData.getValue())
                        .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                        .build();
            }
        }
        dependErrReasonList.add(ErrReason.builder()
                .dependCode(dependCode)
                        .sort(schemeMain.getSort())
                        .billType(schemeMain.getBillType())
                        .schemeCode(schemeMain.getSchemeCode())
                        .schemeName(schemeMain.getName())
                        .billCode(billResultMap.get("billCode").toString())
                        .dependName(dependMainMap.get(dependCode).getName())
                .key(value)
                .reason(value + "不在范围").build());
        // 抛出错误，对应的值不在范围依赖表内
        return CalculateValueVO.builder()
                .value(String.format("%s不在范围表%s中", value, dependCode))
                .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                .build();
    }

    /**
     * 计算分类依赖的值
     *
     * @param orderData
     * @return
     */
    private CalculateValueVO categoryDependValue(SchemeMain schemeMain,SchemeDetail
                                                         schemeDetail, Map<String, Map<String, Object>> orderData,
                                                 Map<String, List<DependData>> dependDataMap,
                                                 Map<String, Map<String, DependData>> dependDataMapMap,
                                                 Map<String, List<DependRule>> dependRuleMap,
                                                 Map<String, DependMain> dependMainMap, String dependCode,
                                                 Map<String, Object> billResultMap,
                                                 List<ErrReason> dependErrReasonList) {
        List<String> keyList = new ArrayList<>();
        List<DependRule> dependRuleList1 = dependRuleMap.get(dependCode);
        Map<String, List<DependRule>> schemeDependRuleMap = dependRuleList1.stream()
                .collect(Collectors.groupingBy(DependRule::getSchemeCode));
        for (DependRule dependRule : schemeDependRuleMap.get(schemeDetail.getSchemeCode())) {
            if (dependRule.getDependType().equals(DependTypeEnum.DEPEND)) {
                if (dependCode.equals(dependRule.getSecondDependCode())) {
                    throw new RuntimeException("不允许二次依赖引用自身，存在栈溢出");
                }
                // 存在二次依赖的话要递归调用依赖计算对应的值
                String tmp = calculateDependValue(schemeMain,schemeDetail, orderData, dependDataMap, dependDataMapMap, dependRuleMap, dependMainMap,
                        dependRule.getSecondDependCode(), billResultMap, dependErrReasonList).getValue();
                if (tmp == null) {  // 如果二次依赖的时候存在空返回，说明在上一层依赖缺失，直接返回上一层的结果
                    return null;
                }
                keyList.add(tmp);

            } else {
                if (dependRule.getGetDataFrom().equals(GetDataFromType.SCHEME)) {
                    if (billResultMap.get(dependRule.getOrderField())==null){
                        throw new GlobalException("计算顺序错误，需要先计算："+dependRule.getOrderField()+",当前字段是："+schemeDetail.getResultName(),",解决：重新保存一下这个字段即可刷新计算顺序");
                    }
                    // 如果计算失败的话，直接返回上层数据计算失败
                    if (billResultMap.get(dependRule.getOrderField()).toString().contains("计算失败")) {
                        return CalculateValueVO.builder()
//                                .value("["+billResultMap.get(dependRule.getOrderField()).getResultName() + "]计算失败，无法进行后续计算")
                                .value("["+ dependRule.getOrderField() + "]计算失败，无法进行后续计算")
                                .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                                .build();
                    }
                    keyList.add(billResultMap.get(dependRule.getOrderField()).toString());
                } else if (dependRule.getGetDataFrom().equals(GetDataFromType.TABLE)) {
                    Object tempKey = orderData.get(dependRule.getOrderTable()).get(dependRule.getOrderField());
                    if (tempKey == null) {
                        keyList.add("null");
                    } else {
                        keyList.add(tempKey.toString());
                    }
                }
            }
        }
        // 将valueList拼接成一个字符串，中间使用符号[-]分割
        String key = StringUtils.join(keyList, "[-]");
        // 从dependDataList中找到符合的value
        Map<String, DependData> dependDataMap1 = dependDataMapMap.get(dependCode);
        if (dependDataMap1 == null) {
            dependErrReasonList.add(ErrReason.builder()
                    .dependCode(dependCode)
                    .key(key)
                    .sort(schemeMain.getSort())
                    .billType(schemeMain.getBillType())
                    .schemeCode(schemeMain.getSchemeCode())
                    .schemeName(schemeMain.getName())
                    .dependName(dependMainMap.get(dependCode).getName())
                    .billCode(billResultMap.get("billCode").toString())
                    .reason(key + "不在依赖数据表中")
                    .build());
            return CalculateValueVO.builder()
                    .value(String.format("%s不在依赖表%s中", key, dependCode))
                    .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                    .build();
        }
        DependData dependData = dependDataMapMap.get(dependCode).get(key);
        if (dependData == null) {
            // 如果没有的时候，判断数据表中有没有“其他！”这一特殊值，如果有的话，则根据其他来进行转换
            dependData = dependDataMapMap.get(dependCode).get(SysConstant.SPECIAL_VALUE.OTHER);
            if (dependData==null){
                dependErrReasonList.add(ErrReason.builder()
                        .dependCode(dependCode)
                        .key(key)
                        .sort(schemeMain.getSort())
                        .billType(schemeMain.getBillType())
                        .schemeCode(schemeMain.getSchemeCode())
                        .schemeName(schemeMain.getName())
                        .dependName(dependMainMap.get(dependCode).getName())
                        .billCode(billResultMap.get("billCode").toString())
                        .reason(key + "不在依赖数据表中")
                        .build());
                return CalculateValueVO.builder()
                        .value(String.format("%s不在依赖表%s中", key, dependCode))
                        .calculateStatusEnum(BillResult.CalculateStatusEnum.FAIL)
                        .build();
            }else {
                // 进入到其他里面，判断其他里面的key值是什么
                if (dependData.getValue().equals(SysConstant.SPECIAL_VALUE.VALUE_EQUAL_KEY)){
                    return CalculateValueVO.builder()
                            .value(key)
                            .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                            .build();
                }
                return CalculateValueVO.builder()
                        .value(dependData.getValue())
                        .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                        .build();
            }
        } else {
            return CalculateValueVO.builder()
                    .value(dependData.getValue())
                    .calculateStatusEnum(BillResult.CalculateStatusEnum.SUCCESS)
                    .build();
        }
    }

    /**
     * 根据计划的粒度，将表的数据划分为行的数据，
     *
     * @param orderDataList
     * @param granularityTable
     * @return
     */
    private List<Map<String, Map<String, Object>>> getValueFromMap
    (Map<String, List<Map<String, Object>>> orderDataList,
     String granularityTable, Map<String, List<OrderTableRelation>> orderTableRelationMap,
     Map<String,Map<String,ForeignKey>> foreignKeyMap,
     Map<String,List<SchemeDetail>> tableSchemeDetailMap) {
        List<Map<String, Map<String, Object>>> resultList = new ArrayList<>();
        if (orderDataList == null) {
            return resultList;
        }
        // 获得orderDataList的key值列表并赋值给tableList        // 对粒度的数据进行循环，重新构建Map结构，这个for循环的item是粒度表格一行的数据
        if (orderDataList.get(granularityTable) == null) {
            throw new RuntimeException("没有找到粒度" + granularityTable + "对应表的数据，配置方案错误");
        }
        /**
         * 这里的循环，最后结果将一行粒度的数据存储在resultList中，
         * 每一个resultList 的元素，包含和这里粒度相关联的其他表的数据
         */
        for (Map<String, Object> orderData : orderDataList.get(granularityTable)) {
            Map<String, Map<String, Object>> resultMap = new HashMap<>();
            // 拿到外键
            resultMap.put(granularityTable, orderData);
            // 获得主表的名字，根据主表名字获取主表的数据，主表的数据应该是唯一的

            // 根据外键的关系，从粒度出发，向外层次遍历获取其他表的数据
            // 这里需要一个层次遍历的算法
            List<QueueTableEntity> queueTableEntityList = QueueTableEntity.queueTableEntityList(granularityTable,foreignKeyMap,orderDataList.keySet());
            // 先拿和粒度有直接关联的表的数据
            // 拿完之后没有直接关联的表，根据新的表来做关联。这里有一个层次遍历
            for (QueueTableEntity queueTable : queueTableEntityList) {
                // 当前使用的数据为上一次表的数据
                Map<String, Object> tableData = resultMap.get(queueTable.getLastTable());
                if (foreignKeyMap.get(queueTable.getLastTable()).get(queueTable.getThisTable())==null){
                    throw new GlobalException("没有找到粒度"+ queueTable.getLastTable() + "对应"+ queueTable.getThisTable() +"的数据，配置方案错误","解决:配置这两个表的外键关联");
                }
                String foreignKey = foreignKeyMap.get(queueTable.getLastTable()).get(queueTable.getThisTable()).getForeignKey();
                if (tableData.get(foreignKey)==null){
                    throw new GlobalException("没有找到外键"+foreignKey+"对应的值，配置方案错误","");
                }
                // 通过外键拿到外键的值
                String foreignKeyValue = tableData.get(foreignKey).toString();
                String thisTableForeignKey = foreignKeyMap.get(queueTable.getLastTable()).get(queueTable.getThisTable()).getRelTableForeignKey();
                // 获取表数据中外键值和当前外键值相等的数据，该数据应该是唯一的
                List<Map<String, Object>> mainDataList = orderDataList.get(queueTable.getThisTable()).stream().filter(
                        item -> item.get(thisTableForeignKey).toString().equals(foreignKeyValue)
                ).collect(Collectors.toList());
                if (mainDataList.isEmpty()){
                    // 如果这里没有值，需要构造特殊处理，把这个表有的字段都设置为null
                    Map<String, Object> nullMap = new HashMap<>();
                    for (SchemeDetail schemeDetail : tableSchemeDetailMap.get(queueTable.getThisTable())) {
                        nullMap.put(schemeDetail.getOrderField(),"NULL");
                        resultMap.put(queueTable.getThisTable(), nullMap);
                    }
                    continue;
                }
                Map<String, Object> resultValue = mainDataList.get(0);
                resultMap.put(queueTable.getThisTable(), resultValue);
            }
            resultList.add(resultMap);
        }
        return resultList;
    }



    /**
     * 获得所有关联的单据数据,
     *
     * @param schemeDetailList
     * @param schemeUserRelations
     * @param schemeMain
     * @return Key = 表名
     * value = 该表对应的数据list,主表明细表之间通过外键关联
     */
    public Map<String, List<Map<String, Object>>> getAllOrderData(List<SchemeDetail> schemeDetailList,
                                                                  Map<String, List<OrderTableRelation>> orderTableRelationMap,
                                                                  Map<String, Map<String, ForeignKey>> foreignKeyMap,
                                                                  List<DependRule> dependRuleList,
                                                                  Map<String, Condition> conditionMap, List<SchemeUserRelation> schemeUserRelations,
                                                                  SchemeMain schemeMain, Integer testFlag, TimeDto timeDto) {
        Map<String, List<SchemeDetail>> schemeDetailMap = schemeDetailList.stream().filter(x -> x.getOrderTable() != null &&
                        x.getGetDataFrom().equals(GetDataFromType.TABLE))
                .collect(Collectors.groupingBy(SchemeDetail::getOrderTable));
        if (orderTableRelationMap.isEmpty()) {
            return null;
        }

        // 将dependRuleList取出orderTable,orderField,然后根据orderTable分组
        Map<String, List<DependRule>> tableDependRuleMap = dependRuleList.stream().filter(s -> s.getOrderTable() != null).collect(Collectors.groupingBy(DependRule::getOrderTable));

        Map<String, List<Map<String, Object>>> tableDataMap = new HashMap<>();

        // 优先查询带有用户条件的筛选，后续再根据外键对之后的明细数据进行筛选，减少查询数据量,如果该规则没有用户使用，直接返回空
        // 添加时间的查询之后，如果时间查询的表和用户查询的表不是相同的表，这里需要做额外的联表查询的处理
        // todo 暂时先默认两个表是相同的，
        String userSelectTableName = schemeMain.getBelongRelTableName();
        String timeSelectTableName = schemeMain.getTimeFieldTable();
        if (!userSelectTableName.equals(timeSelectTableName)) {
            throw new GlobalException(ErrorCodeEnum.VERIFY_ERROR.getMsg(), "用户筛选表和时间筛选表不一致，目前只开通了两个表一致的筛选。不一致要联表查询，做额外的处理，目前这块逻辑没理清楚");
        }

        String timeCondition;
        if (timeDto==null){
            timeDto = new TimeDto();
            timeDto.setStartTime(TimeUtils.transTimeFormula(schemeMain.getTimeFormulaStart(),schemeMain.getExecutionUnit()));
            timeDto.setEndTime(TimeUtils.transTimeFormula(schemeMain.getTimeFormulaEnd(), schemeMain.getExecutionUnit()));
        }
        timeCondition = SqlUtils.transBetweenCondition(schemeMain.getTimeField(), timeDto.getStartTime()
                    , timeDto.getEndTime());

        schemeUserRelations = checkUserData(schemeUserRelations,timeDto,schemeMain.getSchemeCode(),CollectDataTypeEnum.SCHEME,testFlag);
        String userCondition = SqlUtils.transInCondition(schemeMain.getBelongRelTableField(), schemeUserRelations.stream()
                .map(SchemeUserRelation::getArchiveUserCode).collect(Collectors.toList()));
        if (Objects.isNull(orderTableRelationMap.get(userSelectTableName))) {
            throw new RuntimeException("数据库表单之间关系未定义，请联系系统管理员维护");
        }
//        String foreignKey = orderTableRelationMap.get(userSelectTableName).get(0).getOrderForeign();
        List<String> foreignKeyList = foreignKeyMap.get(userSelectTableName).values().stream().map(ForeignKey::getForeignKey).collect(Collectors.toList());
        List<String> fieldNameList = getFieldNameList(orderTableRelationMap, userSelectTableName, schemeDetailMap, tableDependRuleMap, schemeMain.getBelongRelTableField(),foreignKeyList);

        String tableName = schemeDetailMap.get(userSelectTableName).get(0).getOrderTable();
        String condition = Objects.isNull(conditionMap.get(tableName)) ? "" : conditionMap.get(tableName).getConditionField();

        List<Map<String, Object>> orderDataList = selectOrderDataList(tableName, fieldNameList, condition, userCondition, timeCondition, schemeMain.getDataSourceId(), testFlag);
        tableDataMap.put(userSelectTableName, orderDataList);
        List<QueueTableEntity> queueTableEntityList = QueueTableEntity.queueTableEntityList(userSelectTableName,foreignKeyMap,schemeDetailMap.keySet());
        // 循环schemeDetailMap
        for (QueueTableEntity queueTable : queueTableEntityList) {
            String orderTable = queueTable.getThisTable();
            if (orderTable.equals(userSelectTableName)) {
                continue; // 已经优先查询过了，不需要再额外查询
            }
            // 根据当前表以及已查询数据，判断使用哪个外键对
//            GetForeignKeyDto getForeignKeyDto = getForeignKey(orderTable,tableDataMap,foreignKeyMap);
            String useDataTableName = queueTable.getLastTable();  // 默认使用的表名是关联用户所用的表，在发生更改外键使用表时，更新这个值
            String foreignKey = foreignKeyMap.get(useDataTableName).get(orderTable).getForeignKey();
            List<String> userConditionForeignKeyCodeList = tableDataMap.get(useDataTableName)
                    .stream()
                    .map(s -> s.get(foreignKey).toString())
                    .collect(Collectors.toList());
            String relForeignKey = foreignKeyMap.get(useDataTableName).get(orderTable).getRelTableForeignKey();
            userCondition = SqlUtils.transInCondition(relForeignKey, userConditionForeignKeyCodeList);
            foreignKeyList = new ArrayList<>();
            for (String key : foreignKeyMap.get(orderTable).keySet()) {
                foreignKeyList.add(foreignKeyMap.get(orderTable).get(key).getForeignKey());
            }
            fieldNameList = getFieldNameList(orderTableRelationMap, orderTable, schemeDetailMap, tableDependRuleMap, null, foreignKeyList);
            tableName = schemeDetailMap.get(orderTable).get(0).getOrderTable();
            condition = Objects.isNull(conditionMap.get(tableName)) ? "" : conditionMap.get(tableName).getConditionField();
            // 如果测试的话只拿两条数据
            orderDataList = selectOrderDataList(tableName, fieldNameList, condition, userCondition, "", schemeMain.getDataSourceId(), 0);
            tableDataMap.put(orderTable, orderDataList);
        }

        return tableDataMap;
    }
    private GetForeignKeyDto getForeignKey(String orderTable, Map<String, List<Map<String, Object>>> tableDataMap ,
                                           Map<String, Map<String, ForeignKey>> foreignKeyMap ){
        Map<String,ForeignKey> keyMap = foreignKeyMap.get(orderTable);
        for (String key : keyMap.keySet()) {
            List<Map<String, Object>> tableData = tableDataMap.get(key);
            if (tableData!=null&& !tableData.isEmpty()){
                return GetForeignKeyDto.builder()
                        .foreignKey(  keyMap.get(key).getRelTableForeignKey())
                        .useDataTableName(key)
                        .build();
            }
        }
       throw new GlobalException("外键关联失败，表："+orderTable+"与["+tableDataMap.keySet()+"]均没有关联");


    }

    private List<String> getFieldNameList(Map<String, List<OrderTableRelation>> orderTableRelationMap, String
            orderTable,
                                          Map<String, List<SchemeDetail>> schemeDetailMap,
                                          Map<String, List<DependRule>> tableDependRuleMap, String belongUserField,
                                          List<String> foreignKeyList) {
//        String foreignKey = orderTableRelationMap.get(orderTable).get(0).getOrderForeign();
        // 获得第一层的表名称，如果有依赖表的话，会有多层的字段名称需要获取
        if (Objects.isNull(schemeDetailMap.get(orderTable))) {
            throw new RuntimeException("表" + orderTable + "没有配置字段信息");
        }
        List<String> fieldNameList = schemeDetailMap.get(orderTable).stream().map(SchemeDetail::getOrderField).collect(Collectors.toList());
        if (tableDependRuleMap.get(orderTable) != null) {
            fieldNameList.addAll(tableDependRuleMap.get(orderTable).stream().map(DependRule::getOrderField).collect(Collectors.toList()));
        }
        if (StringUtils.isNotBlank(belongUserField)) {
            fieldNameList.add(belongUserField);
        }
        fieldNameList.addAll(foreignKeyList); //需要外键对主表明细表之间关联
        fieldNameList.add("id"); // 必须使用该字段，用于区分粒度
        return fieldNameList.stream().distinct().collect(Collectors.toList()); // 去重
    }

    private List<Map<String, Object>> selectOrderDataList(String tableName, List<String> fieldNameList,
                                                          String condition, String userCondition, String timeCondition
            , Integer dataSourceId, Integer testFlag) {
        // 通过拼接sql语句，将fieldNameList中的字段拼接成需要获取的语句，tableName为目标表名
        // 根据对应表名查询生效条件
        condition = SqlUtils.replaceSqlCondition(condition);
        String sql = "select " + String.join(",", fieldNameList) + " from " + tableName + " where "
                + userCondition + timeCondition + (StringUtils.isBlank(condition) ? "" : " and (" + condition+ ")") + " and is_delete = 0";
        sql+=" order by id desc";
        if (testFlag==1) {
            sql += " limit 2";
        }

        log.info("sql:{}", sql);
        return dataSourceService.execute(sql, dataSourceId);
    }

    @Override
    public Map<String, SchemeMain> getSchemeMainMap(List<String> schemeMainList) {
        return list(new LambdaQueryWrapper<SchemeMain>().in(SchemeMain::getSchemeCode, schemeMainList)).
                stream().collect(Collectors.toMap(SchemeMain::getSchemeCode, a -> a));
    }

    @Override
    public Map<String, List<SchemeMain>> selectMenu() {
        List<SchemeMain> schemeMainList = list();
        return schemeMainList.stream().collect(Collectors.groupingBy(SchemeMain::getSort));
    }

    @Override
    public SchemeMainDetailVO selectOneById(Long id) {
        SchemeMain schemeMain = getById(id);
        List<SchemeDetail> schemeDetailList = schemeDetailService.listSchemeDetailBySchemeCode(schemeMain.getSchemeCode(),SelectTypeEnum.CALC);
        List<Condition> conditionList = conditionService.listConditionsBySchemeCode(schemeMain.getSchemeCode());
        List<SchemeUserRelation> schemeUserRelationList = schemeUserRelationService.listBySchemeCode(schemeMain.getSchemeCode());
        return SchemeMainDetailVO.builder()
                .schemeMain(schemeMain)
                .schemeDetailList(schemeDetailList)
                .conditionList(conditionList)
                .schemeUserRelations(schemeUserRelationList)
                .build();
    }

    @Override
    public PageInfo<SchemeMain> list(SchemeMainDto schemeMainDto) {
        long createTimeStart = 0, createTimeEnd = 0, updateTimeStart = 0, updateTimeEnd = 0;
        if (schemeMainDto.getCreateTime() != null) {
            createTimeStart = schemeMainDto.getCreateTime();
            createTimeEnd = createTimeStart + SysConstant.Time.DAY_TIME_STAMP;
            schemeMainDto.setCreateTime(null);
        }
        if (schemeMainDto.getUpdateTime() != null) {
            updateTimeStart = schemeMainDto.getUpdateTime();
            updateTimeEnd = updateTimeStart + SysConstant.Time.DAY_TIME_STAMP;
            schemeMainDto.setUpdateTime(null);
        }
        LambdaQueryWrapper<SchemeMain> queryWrapper = new LambdaQueryWrapper<>(schemeMainDto);
        if (createTimeStart != 0) {
            queryWrapper
                    .ge(SchemeMain::getCreateTime, createTimeStart)
                    .le(SchemeMain::getCreateTime, createTimeEnd);
        }
        if (updateTimeStart != 0) {
            queryWrapper
                    .ge(SchemeMain::getUpdateTime, updateTimeStart)
                    .le(SchemeMain::getUpdateTime, updateTimeEnd);
        }

        if (schemeMainDto.getCreateStartTime() != null && schemeMainDto.getCreateEndTime() != null) {
            queryWrapper.ge(SchemeMain::getCreateTime, schemeMainDto.getCreateStartTime())
                    .le(SchemeMain::getCreateTime, schemeMainDto.getCreateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        if (schemeMainDto.getUpdateStartTime() != null && schemeMainDto.getUpdateEndTime() != null) {
            queryWrapper.ge(SchemeMain::getUpdateTime, schemeMainDto.getUpdateStartTime())
                    .le(SchemeMain::getUpdateTime, schemeMainDto.getUpdateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }

        queryWrapper.orderByDesc(SchemeMain::getId);
        List<SchemeMain> schemeMainList = list(queryWrapper);
        return new PageInfo<>(schemeMainList);
    }

    // 循环map将里面的值key和value对应出来，方便后面的搜索
    private Map<String, Map<String, DependData>> getDependDateMapMap
    (Map<String, List<DependData>> dependDataMap) {
        if (dependDataMap == null || dependDataMap.isEmpty()) {
            return null;
        }
        Map<String, Map<String, DependData>> dependDateMapMap = new HashMap<>();
        dependDataMap.forEach((key, value) -> {
            Map<String, DependData> dependDataMap1 = value.stream()
                    .filter(s -> s.getKey() != null)// 如果是范围依赖的话，这里不需要进行map处理
                    .collect(Collectors.toMap(DependData::getKey, v -> v));
            dependDateMapMap.put(key, dependDataMap1);

        });
        return dependDateMapMap;
    }

    @Override
    public TestBillResultVO testScheme(String code) {
        List<String> codeList = Collections.singletonList(code);
        TestBillResultOriginVO testBillResultOriginVO = startCreateBill(codeList, 1);
        TestBillResultVO testBillResultVO = new TestBillResultVO();
        BillResultVO billResultVO = billMainService.buildBillResultVO(testBillResultOriginVO.getBillDataList(), SelectTypeEnum.CALC);
        // 根据displayOrder进行排序
        // 处理源数据
        getOriginData(testBillResultOriginVO, testBillResultVO);
        testBillResultVO.setBillDataList(testBillResultOriginVO.getBillDataList());
        testBillResultVO.setResultNameList(billResultVO.getResultNameList());
        return testBillResultVO;
    }

    private void getOriginData(TestBillResultOriginVO testBillResultOriginVO, TestBillResultVO testBillResultVO) {
        if (testBillResultOriginVO.getOriginData().isEmpty()) {
            return;
        }
        List<ResultNameCodeVO> originNameCodeList = new ArrayList<>();
        List<BillResultDataVO> originDataVOList = new ArrayList<>();
        //获得testBillResultOriginVO.getOriginData()的所有key
        Set<String> tableNameSet = testBillResultOriginVO.getOriginData().get(0).keySet();
        Set<String> filedNameSet = new HashSet<>();
        for (String tableName : tableNameSet) {
            filedNameSet.addAll(testBillResultOriginVO.getOriginData().get(0).get(tableName).keySet());
        }
        List<TableFieldRelation> tableFieldRelationList = tableFieldRelationService.list(new LambdaQueryWrapper<TableFieldRelation>()
                .in(TableFieldRelation::getTableName, new ArrayList<>(tableNameSet))
                .select(TableFieldRelation::getFieldName, TableFieldRelation::getField, TableFieldRelation::getTableName));

        // tableFieldRelationList去重，根据getTableName(), getField()两个字段的值联合
        tableFieldRelationList = tableFieldRelationList.stream()
                .distinct()
                .collect(Collectors.toList());
        // 将tableFieldRelationList转换为Map<String,Map<String,String>>的形式，
        // 其中key是表名，value是Map<String,String>，其中key是字段名，value是字段中文名
        Map<String, String> tableFiledNameMap = tableFieldRelationList.stream()
                .collect(Collectors.toMap(s -> String.format("%s[-]%s", s.getTableName(), s.getField()), TableFieldRelation::getFieldName));
        for (String tableName : testBillResultOriginVO.getOriginData().get(0).keySet()) {
            for (String filedName : testBillResultOriginVO.getOriginData().get(0).get(tableName).keySet()) {
                originNameCodeList.add(ResultNameCodeVO.builder()
                        .resultCode(String.format("%s[-]%s", tableName, filedName))
                        .resultName(tableFiledNameMap.get(String.format("%s[-]%s", tableName, filedName)))
                        .build());
            }
        }

        //获得testBillResultOriginVO.getOriginData()
        // 这里的map,第一层的key是表名称，第二层的key是字段名称，最里面的value是表-字段-值
        for (Map<String, Map<String, Object>> originDatum : testBillResultOriginVO.getOriginData()) {
            BillResultDataVO originData = new BillResultDataVO();
            Map<String, String> keyMap1 = new HashMap<>();
            for (String tableName : originDatum.keySet()) {
                for (String filedName : originDatum.get(tableName).keySet()) {
                    if (originDatum.get(tableName).get(filedName) != null) {
                        keyMap1.put(String.format("%s[-]%s", tableName, filedName), originDatum.get(tableName).get(filedName).toString());
                    } else {
                        keyMap1.put(String.format("%s[-]%s", tableName, filedName), "NULL");

                    }

                }
            }
            originData.setKeyMap(keyMap1);
            originDataVOList.add(originData);
        }
        testBillResultVO.setOriginNameList(originNameCodeList);
        testBillResultVO.setOriginDataList(originDataVOList);
    }

    @Override
    public List<String> getNeedSchemeMainList(ExecutionTimeUnit executionUnit, int time) {
        return list(new LambdaQueryWrapper<SchemeMain>()
                .eq(SchemeMain::getExecutionUnit, executionUnit)
                .eq(SchemeMain::getExecutionTime, time)).stream().map(SchemeMain::getSchemeCode).collect(Collectors.toList());
    }

    @Override
    public SchemeMain addSchemeMain(SchemeMain schemeMain) {
        schemeMain.setSchemeCode(codeService.getCode(IdTypeEnum.SCHEME_CODE));
        List<SchemeDetail> schemeDetailList = new ArrayList<>();
        // 增加所属用户编码code字段
        schemeDetailList.add(SchemeDetail.builder()
                .type(SchemeDetailParamEnum.ORDER_DATA)
                .schemeCode(schemeMain.getSchemeCode())
                .getDataFrom(GetDataFromType.TABLE)
                .orderTable(schemeMain.getBelongRelTableName())
                .orderField(schemeMain.getBelongRelTableField())
                .orderName("所属用户编码")
                .resultCode(codeService.getCode(IdTypeEnum.RESULT_FIELD))
                .resultName("所属用户编码")
                .resultType(ResutlTypeEnum.STRING)
                .calculateOrder(0)
                .displayOrder(1)
                .getDataFrom(GetDataFromType.TABLE)
                .build());

        // 增加时间字段
        schemeDetailList.add(SchemeDetail.builder()
                .type(SchemeDetailParamEnum.ORDER_DATA)
                .schemeCode(schemeMain.getSchemeCode())
                .getDataFrom(GetDataFromType.TABLE)
                .orderTable(schemeMain.getTimeFieldTable())
                .orderField(schemeMain.getTimeField())
                .orderName(schemeMain.getTimeFieldName())
                .resultCode(codeService.getCode(IdTypeEnum.RESULT_FIELD))
                .resultName("计算时间")
                .resultType(ResutlTypeEnum.STRING)
                .calculateOrder(0)
                .displayOrder(2)
                .getDataFrom(GetDataFromType.TABLE)
                .build());
        schemeDetailService.saveBatch(schemeDetailList);
        schemeMain.setTimeFieldResultCode(schemeDetailList.get(1).getResultCode());
        save(schemeMain);
        return schemeMain;
    }

    @Override
    public List<SchemeMain> listByCode(List<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)){
            return null;
        }
        return list(new LambdaQueryWrapper<SchemeMain>()
                .in(SchemeMain::getSchemeCode, codeList));
    }

    @Override
    public List<SchemeMain> listByCode(Set<String> codeList) {
        return list(new LambdaQueryWrapper<SchemeMain>()
                .in(SchemeMain::getSchemeCode, codeList));
    }
    @Override
    public List<BillData> getFlashData(BillResultFlashDto billResultDto){
        List<BillData> billResultList = billResultService.listBySchemeAndBatch(billResultDto);
        if (billResultList.isEmpty()) {
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, billResultDto.getCollectResultCode() + "批次下没有账单数据");
        }

        return billResultList;
    }

    private List<BillData> doFlash( List<BillData> billResultList) {
        List<String> schemeCodeList = billResultList.stream().map(BillData::getSchemeCode).distinct().collect(Collectors.toList());
        List<SchemeDetail> schemeDetails = schemeDetailService.listSchemeDetailBySchemeCode(schemeCodeList,SelectTypeEnum.CALC);
        List<SchemeMain> schemeMainList = listByCode(schemeCodeList);
        Map<String, List<SchemeDetail>> schemeDetailMap = schemeDetails.
                stream().collect(Collectors.groupingBy(SchemeDetail::getSchemeCode));
        Map<String,List<BillData>> billResultGroupMap = billResultList.stream().collect(Collectors.groupingBy(BillData::getSchemeCode));
        for (SchemeMain schemeMain : schemeMainList) {

            Map<String, Map<String, Object>> billResultMap = new HashMap<>();
            // 对billResultList进行分组，用billCode和resultCode作为分组条件，将结果存储在billResultMap中
            for (BillData result : billResultGroupMap.get(schemeMain.getSchemeCode())) {
                result.getData().put("billCode",result.getBillCode());
                billResultMap.put(result.getBillCode(), result.getData());
            }
            List<SchemeDetail> schemeDetailList = schemeDetailMap.get(schemeMain.getSchemeCode());
            List<SchemeDetail> sequenceSchemeDetailList = schemeDetailList.stream()
                    .sorted(Comparator.comparing(SchemeDetail::getCalculateOrder))
                    .collect(Collectors.toList());
            // 准备依赖相关数据
            List<String> dependCodeList=new ArrayList<>();
            List<DependRule> dependRuleList=new ArrayList<>();
            Map<String, List<DependRule>> dependRuleMap=new HashMap<>();
            Map<String, List<DependData>> dependDataMap=new HashMap<>();
            Map<String, Map<String, DependData>> dependDataMapMap=new HashMap<>();
            Map<String, DependMain> dependMainMap=new HashMap<>();
            dependCodeList = schemeDetailList.stream().filter(s->s.getDependCode()!=null).map(SchemeDetail::getDependCode).collect(Collectors.toList());
            if (!dependCodeList.isEmpty()){
                dependRuleList = dependRuleService.getDependRuleByCodeAndFilter(dependCodeList,schemeMain.getSchemeCode());
                dependRuleMap = dependRuleList.stream().collect(Collectors.groupingBy(DependRule::getDependCode));
                dependDataMap = dependDataService.getDependDataMap(dependRuleList.stream()
                        .map(DependRule::getDependCode).collect(Collectors.toList()));
                dependDataMapMap = getDependDateMapMap(dependDataMap);
                dependMainMap = dependMainService.getDependMainMap(dependRuleList.stream()
                        .map(DependRule::getDependCode).distinct().collect(Collectors.toList()));
            }
            List<ErrReason> dependErrReasonList = new ArrayList<>();
            // 如果已经计算成功的，也跳过，只拿失败的
            for (SchemeDetail schemeDetail : sequenceSchemeDetailList) {
                // 数据来源是源数据的不进行覆盖
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.ORDER_DATA)) {
                    continue;
                }
                if ((schemeDetail.getType().equals(SchemeDetailParamEnum.FOR_MUL_TAG) ||
                        schemeDetail.getType().equals(SchemeDetailParamEnum.SINGLE_TAG))
                        && schemeDetail.getGetDataFrom().equals(GetDataFromType.SCHEME)) {
                    calculateAndAddTagFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.SUM)) {
                    calculateAndAddSumFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.COUNT)) {
                    calculateCountFiled(schemeDetail, billResultMap);
                }
                if (schemeDetail.getType().equals(SchemeDetailParamEnum.MAX) ||
                        schemeDetail.getType().equals(SchemeDetailParamEnum.MIN)) {
                    calculateMaxOrMinFiled(schemeDetail, billResultMap);
                }
                for (BillData result : billResultGroupMap.get(schemeMain.getSchemeCode())) {
                    // 如果传入的值是手动更新过的值，不需要再被刷新
                    if (result.getLastData()!=null){
                        if (result.getLastData().get(schemeDetail.getResultCode())!=null){
                            continue;
                        }
                    }
                    // 这里不需要源数据进行计算，所以直接传入null就行。刷新错误
                    BillResult billResult1 = calculateResultBySchemeDetail(schemeMain,schemeDetail, null, dependRuleMap,
                            dependDataMap, dependDataMapMap, dependMainMap, billResultMap.get(result.getBillCode()), dependErrReasonList);
                    result.getData().put(billResult1.getResultCode(), billResult1.getValue());
                }
            }

            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, BillData.class);
            for (BillData entity : billResultList) {
                Query query = new Query(Criteria.where("_id").is(entity.getId()));
                Update update = new Update()
                        .set("data", entity.getData());
                bulkOps.updateOne(query, update);
            }
            bulkOps.execute();
        }
        return billResultList;
    }

    @Override
    public void flash(BillResultFlashDto billResultDto) {
        // 获取账单数据
        List<BillData> billDataList=getFlashData(billResultDto);
        doFlash(billDataList);
    }
    @Override
    public void flashByCollectCode(BillResultFlashDto billResultDto) {
        // 获取账单数据
        List<BillData> billDataList=getFlashData(billResultDto);
        billDataList = doFlash(billDataList);
        collectSchemeMainService.flashCollect(billDataList, billResultDto.getCollectResultCode());
    }

    @Override
    public List<BillData> createAndCollect(CreateAndCollectDto createAndCollectDto){
        List<BillData> billDataList = new ArrayList<>();
        for (String schemeCode : createAndCollectDto.getSchemeCode()) {
            billDataList.addAll(startCreateBill(Collections.singletonList(schemeCode),2,createAndCollectDto.getTimeDto()).getBillDataList());
        }
        return billDataList;
    }
}


