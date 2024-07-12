package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.*;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.dto.CreateAndCollectDto;
import com.ruoyi.colorfulfog.model.dto.DoCreateCollectBillDataDto;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.vo.CalculateValueVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.ruoyi.colorfulfog.utils.JEPUtils;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectSchemeMainMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class CollectSchemeMainServiceImpl extends ServiceImpl<CollectSchemeMainMapper, CollectSchemeMain> implements CollectSchemeMainService {

    @Resource
    CollectSchemeDetailService collectSchemeDetailService;
    @Resource
    BillResultService billResultService;
    @Resource
    BillMainService billMainService;
    @Resource
    CodeService codeService;

    @Resource
    SchemeMainService schemeMainService;
    @Resource
    CollectResultMainService collectResultMainService;


    @Autowired
    MongoTemplate mongoTemplate;

    @Transactional
    @Override
    public void createCollect(String collectCode, String belongArchiveCode) {
        List<CollectSchemeDetail> collectSchemeDetails = collectSchemeDetailService.listByCode(collectCode);
        CollectSchemeMain collectSchemeMain = this.getOneByCode(collectCode);
        createCollectFirst(collectSchemeMain, collectSchemeDetails, belongArchiveCode);

    }

    @Override
    public List<ExportExcelVO> createAndCollect(CreateAndCollectDto createAndCollectDto) {
        List<CollectSchemeDetail> collectSchemeDetails = collectSchemeDetailService.listByCode(createAndCollectDto.getCollectCode());
        Map<String,List<CollectSchemeDetail>> collectSchemeDetailMap = collectSchemeDetails.stream()
                        .filter(collectSchemeDetail -> collectSchemeDetail.getSchemeCode() != null).
                collect(Collectors.groupingBy(CollectSchemeDetail::getSchemeCode));
        CollectSchemeMain collectSchemeMain = this.getOneByCode(createAndCollectDto.getCollectCode());
        if (createAndCollectDto.getSchemeCode()==null||createAndCollectDto.getSchemeCode().isEmpty()){
            createAndCollectDto.setSchemeCode(collectSchemeDetails.stream()
                    .map(CollectSchemeDetail::getSchemeCode)
                    .filter(Objects::nonNull).distinct().collect(Collectors.toList()));
        }
        List<BillData> billDataList = schemeMainService.createAndCollect(createAndCollectDto);
        Map<String,List<BillData>> billDataMap = billDataList.stream().collect(Collectors.groupingBy(BillData::getSchemeCode));
        List<BaseData> baseData = new ArrayList<>(billDataList);
        baseData = baseData.stream().filter(s->!s.getStatus().equals(BillCheckStatusEnum.HAVE_DELETED)).collect(Collectors.toList());
        List<CollectBillData> collectBillDataList = doCreateCollectBillData(
                DoCreateCollectBillDataDto.builder()
                        .collectSchemeMain(collectSchemeMain)
                        .collectSchemeDetails(collectSchemeDetails)
                        .billMainList(baseData)
                        .isSave(false)
                        .build());
        List<ExportExcelVO> exportExcelVOS = new ArrayList<>();
        exportExcelVOS.add(ExportExcelVO.builder()
                        .schemeName(collectSchemeMain.getCollectSchemeName())
                        .billResultVO(collectResultMainService.buildBillResultVO(collectBillDataList, SelectTypeEnum.CALC))
                .build());
        for (String schemeCode : createAndCollectDto.getSchemeCode()) {
            List<BillData> billData = billDataMap.get(schemeCode);
            ExportExcelVO exportExcelVO = ExportExcelVO.builder()
                    .schemeName(collectSchemeDetailMap.get(schemeCode).get(0).getSchemeName())
                    .billResultVO(billMainService.buildBillResultVO(billData,SelectTypeEnum.CALC))
                    .build();
            exportExcelVOS.add(exportExcelVO);
        }
        return exportExcelVOS;
    }
    private void createCollectFirst(CollectSchemeMain collectSchemeMain, List<CollectSchemeDetail> collectSchemeDetails, String belongArchiveCode) {
        List<String> schemeCodeList = collectSchemeDetails.stream()
                .filter(collectSchemeDetail -> collectSchemeDetail.getSchemeCode() != null)
                .map(CollectSchemeDetail::getSchemeCode)
                .distinct()
                .collect(Collectors.toList());
        // 计划表的Map,key是计划表的code
        Map<String, List<CollectSchemeDetail>> collectSchemeDetailMap = collectSchemeDetails.stream()
                .filter(collectSchemeDetail -> collectSchemeDetail.getSchemeCode() != null)
                .collect(Collectors.groupingBy(CollectSchemeDetail::getSchemeCode));
        List<CollectResultDto> collectResultDtoList = new ArrayList<>();
        for (String schemeCode : schemeCodeList) {
            collectResultDtoList.add(
                    CollectResultDto.builder()
                            .schemeCode(schemeCode)
                            .timeFieldCode(collectSchemeDetailMap.get(schemeCode).get(0).getTimeField())
                            .belongArchiveCode(belongArchiveCode)
                            .timeStart(collectSchemeMain.getTimeStart())
                            .timeEnd(collectSchemeMain.getTimeEnd())
                            .costTerm(collectSchemeMain.getCostTerm())
                            .collectSchemeCode(collectSchemeMain.getCollectSchemeCode())
                            .build());
        }
        //一个计划表为key的map
        List<BaseData> billMainListAll = new ArrayList<>();
        collectResultDtoList = collectResultDtoList.stream().distinct().collect(Collectors.toList());

        for (CollectResultDto collectResultDto : collectResultDtoList) {
                List<BaseData> listBillResultMapByTimeVO = billResultService.listBillResultMapByTime(collectResultDto,collectSchemeMain.getCollectObject());
                billMainListAll.addAll(listBillResultMapByTimeVO);
        }

        billMainListAll = billMainListAll.stream().filter(s->!s.getStatus().equals(BillCheckStatusEnum.HAVE_DELETED)).collect(Collectors.toList());
       // 以上准备好需要收集的数据并做好转换
        // 将数据转换为map的形式
         doCreateCollectBillData(DoCreateCollectBillDataDto.builder()
                .collectSchemeMain(collectSchemeMain)
                .collectSchemeDetails(collectSchemeDetails)
                .billMainList(billMainListAll)
                .isSave(true)
                .build());
    }

    // 将准备好的数据传入，进行汇总计划的收集
    private List<CollectBillData> doCreateCollectBillData(DoCreateCollectBillDataDto createCollectBillDataDto) {
        Map<String, List<BaseData>> belongBillMainListMap = createCollectBillDataDto.getBillMainList().stream().collect(Collectors.groupingBy(BaseData::getBelongArchiveCode));
        Map<String, List<BaseData>> baseDataMap = createCollectBillDataDto.getBillMainList().stream().collect(Collectors.groupingBy(BaseData::getSchemeCode));
        String belongCodeField = createCollectBillDataDto.getCollectSchemeMain().getBelongCodeField();
        String belongNameField = createCollectBillDataDto.getCollectSchemeMain().getBelongNameField();
        CollectSchemeMain collectSchemeMain = createCollectBillDataDto.getCollectSchemeMain();
        Map<String, Map<String, Object>> collectResultMap = calculateSumFieldByBillData(baseDataMap, createCollectBillDataDto.getCollectSchemeDetails()
                , belongCodeField);
        List<CollectBillData> collectResultMainList = new ArrayList<>();
        List<String> resultCodeList = codeService.getCode(IdTypeEnum.COLLECT_DATA_CODE, collectResultMap.keySet().size());
        int index = 0;
        // 一个所属一行数据，一个主表多个明细字段
        for (String belongCode : collectResultMap.keySet()) {
            String belongArchiveCode = null;
            String belongArchiveName = null;
            if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.COLLECT)) {
                belongArchiveCode = belongBillMainListMap.get(belongCode).get(0).getData().get(belongCodeField).toString();
                belongArchiveName = belongBillMainListMap.get(belongCode).get(0).getData().get(belongNameField).toString();
            }else if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.BILL)) {
                belongArchiveCode = belongCode;
                belongArchiveName = belongBillMainListMap.get(belongCode).get(0).getBelongArchiveName();
            }
            collectResultMainList.add(CollectBillData.builder()
                    .billCode(resultCodeList.get(index))
                    .schemeCode(createCollectBillDataDto.getCollectSchemeMain().getCollectSchemeCode())
                    .belongArchiveCode(belongArchiveCode)
                    .belongArchiveName(belongArchiveName)
                    .billType(belongBillMainListMap.get(belongCode).get(0).getBillType())
                    .costTerm(collectSchemeMain.getCostTerm())
                    .data(collectResultMap.get(belongCode))
                    .auditStatus(AuditStatusEnum.WAIT_START_AUDIT)
                    .build());
            List<? extends BaseData> billMains = belongBillMainListMap.get(belongCode);
            for (BaseData billMain : billMains) {
                billMain.setCostTerm(collectSchemeMain.getCostTerm());
                billMain.setCollectResultCode(resultCodeList.get(index));
            }
            if (createCollectBillDataDto.getIsSave()) {
                Update update = new Update();
                update.set("collectResultCode", resultCodeList.get(index));
                update.set("costTerm", collectSchemeMain.getCostTerm());
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").in(belongBillMainListMap.get(belongCode)
                        .stream().map(BaseData::getId).collect(Collectors.toList())));
                if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.COLLECT)) {
                    mongoTemplate.updateMulti(query, update, CollectBillData.class);
                }else if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.BILL)) {
                    mongoTemplate.updateMulti(query, update, BillData.class);
                }
            }

            index++;
        }
        // 更新原始数据的账期和将汇总的code绑定到上面。
        // 保存数据到数据库
        if (createCollectBillDataDto.getIsSave()){
            mongoTemplate.insertAll(collectResultMainList);
        }
        return collectResultMainList;
    }

    /**
     * 更新写入的总数据，
     * 添加了一个汇总数字的字段，用于传输空白字段的值加入到刷新中
     */
    private Map<String, Map<String, Object>> calculateSumFieldByBillData(Map<String, List<BaseData>> schemeCodeResultMap, List<CollectSchemeDetail> collectSchemeDetails, String belongCodeField){
        return calculateSumFieldByBillData(schemeCodeResultMap, collectSchemeDetails, belongCodeField, null);
    }

    // 计算汇总的字段数值
    private Map<String, Map<String, Object>> calculateSumFieldByBillData(Map<String, List<BaseData>> schemeCodeResultMap, List<CollectSchemeDetail> collectSchemeDetails, String belongCodeField,Map<String,Map<String,Object>> belongCollectData) {
        // 收集结果的map,第一个key是账单分类的code,第二个key是收集的字段的Code，最后的vale是汇总的值
        Map<String, Map<String, Object>> collectResultMap = new HashMap<>();
        collectSchemeDetails = collectSchemeDetails.stream().sorted(Comparator.comparing(CollectSchemeDetail::getCalculateOrder)).collect(Collectors.toList());
        for (CollectSchemeDetail collectSchemeDetail : collectSchemeDetails) {
            List<? extends BaseData> billResultDataVOList = schemeCodeResultMap.get(collectSchemeDetail.getSchemeCode());
            List<String> groupByValueList = new ArrayList<>();
            if (collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.CONDITION_SUM)) {
                groupByValueList = Arrays.asList(collectSchemeDetail.getGroupByFieldValue().split(","));
            }

            if ((billResultDataVOList == null || billResultDataVOList.isEmpty()) &&
                    !(collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.EQUATION)||
                    collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.EMPTY))) {
                continue;
            }
            if (billResultDataVOList != null) {
                for (BaseData billResultDataVO : billResultDataVOList) {
                    // 这里得到的是一行账单数据，
                    // 需要根据汇总的字段，以及分类的字段，计算出汇总字段的数值，并赋值给汇总字段
                    Map<String, Object> dataMap = billResultDataVO.getData();
                    String belong;
                    // 所属的code
                    if (belongCodeField != null) {
                        belong = billResultDataVO.getData().get(belongCodeField).toString();
                    } else {
                        belong = billResultDataVO.getBelongArchiveCode();
                    }
                    String value = dataMap.get(collectSchemeDetail.getSchemeResultCode()).toString();

                    BigDecimal tmp;
                    try {
                        tmp = BigDecimal.valueOf(Double.parseDouble(value));
                    } catch (Exception e) {
                        tmp = BigDecimal.ZERO;
                    }
                    if (collectResultMap.get(belong) == null) {
                        Map<String, Object> map = new HashMap<>();
                        collectResultMap.put(belong, map);
                    }
                    CalculateValueVO calculateValueVO;
                    BigDecimal resultValue = BigDecimal.ZERO;
                    switch (collectSchemeDetail.getCollectType()) {
                        case SUM:
                        case CONDITION_SUM:
                            if (!groupByValueList.contains(billResultDataVO.getData().get(collectSchemeDetail.getGroupByField()))
                                    && collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.CONDITION_SUM)) {
                                continue;
                            }
                            if (collectResultMap.get(belong).get(collectSchemeDetail.getCollectResultCode()) == null) {
                                collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), tmp.toString());
                                continue;
                            }
                            // 将value转换为long类
                            BigDecimal origin = new BigDecimal(String.valueOf(collectResultMap.get(belong).get(collectSchemeDetail.getCollectResultCode())));
                            origin = origin.add(tmp);
                            collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), origin.toString());
                            break;
                        case ONE:
                        case EMPTY:
                            collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), value);
                            break;
                    }
                }
            }
            /**
             * 计算完之后做后续计算的放在最外面一轮过
             */
            if (collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.EMPTY)){
                if (belongCollectData==null) {
                    continue;
                }
                for (String belong : collectResultMap.keySet()) {
                    collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), belongCollectData.get(belong).get(collectSchemeDetail.getCollectResultCode()));
                }
            }
            if (collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.EQUATION)) {
                for (String belong : collectResultMap.keySet()) {
                    CalculateValueVO valueVO = JEPUtils.calculate(collectSchemeDetail.getExpression(), collectResultMap.get(belong));
                    collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), valueVO.getValue());
                }
            }
        }
        return collectResultMap;
    }

    @Override
    public CollectSchemeMain getOneByCode(String code) {
        CollectSchemeMain collectSchemeMain = getOne(new LambdaQueryWrapper<CollectSchemeMain>()
                .eq(CollectSchemeMain::getCollectSchemeCode, code));
        if (collectSchemeMain == null) {
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, "找不到汇总计划:" + code);
        }
        collectSchemeMain.setTimeStart(TimeUtils.transTimeFormula(collectSchemeMain.getTimeFormulaStart(),ExecutionTimeUnit.MONTHLY));
        collectSchemeMain.setTimeEnd(TimeUtils.transTimeFormula(collectSchemeMain.getTimeFormulaEnd(),ExecutionTimeUnit.MONTHLY));

        collectSchemeMain.setCostTerm(TimeUtils.transCostTermFormula(collectSchemeMain.getCostTermFormula()));
        return collectSchemeMain;
    }

    @Override
    public List<CollectSchemeMain> listBySchemeCodeList(List<String> schemeCodeList) {
        return list(new LambdaQueryWrapper<CollectSchemeMain>()
                .in(CollectSchemeMain::getCollectSchemeCode, schemeCodeList));
    }

    @Override
    public List<CollectSchemeMain> listBySchemeCodeList(Set<String> schemeCodeList) {
        return list(new LambdaQueryWrapper<CollectSchemeMain>()
                .in(CollectSchemeMain::getCollectSchemeCode, schemeCodeList));
    }

    @Override
    public void flashCollect(List<BillData> billDataList, String collectResultCode) {
        List<BaseData> baseDataList = new ArrayList<>(billDataList);
        baseDataList = baseDataList.stream().filter(baseData->!baseData.getStatus().equals(BillCheckStatusEnum.HAVE_DELETED)).collect(Collectors.toList());
        Map<String, List<BaseData>> schemeCodeResultMap = baseDataList.stream().collect(Collectors.groupingBy(BaseData::getSchemeCode));
        CollectBillData collectBillData = mongoTemplate.findOne(new Query(Criteria.where("billCode").is(collectResultCode)), CollectBillData.class);
        List<CollectSchemeDetail> collectSchemeDetails = collectSchemeDetailService.listSchemeDetailBySchemeCode(Collections.singletonList(collectBillData.getSchemeCode()), SelectTypeEnum.CALC);
        Map<String,Map<String,Object>> belongCollectData = new HashMap<>();
        belongCollectData.put(collectBillData.getBelongArchiveCode(), collectBillData.getData());
        Map<String, Map<String, Object>> collectResultMap = calculateSumFieldByBillData(schemeCodeResultMap, collectSchemeDetails, null, belongCollectData);
        if (collectResultMap.keySet().size() > 1) {
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, "当前汇总账单存在多个商家，无法计算汇总字段");
        }
        String key = collectResultMap.keySet().iterator().next();
        Query query = new Query(Criteria.where("billCode").is(collectResultCode));
        Update update = new Update()
                .set("data", collectResultMap.get(key));
        mongoTemplate.updateFirst(query, update, CollectBillData.class);
    }
}
