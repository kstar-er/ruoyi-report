package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.constant.enums.IdTypeEnum;
import com.ruoyi.colorfulfog.mapper.CollectSchemeMainMapper;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.vo.BillResultDataVO;
import com.ruoyi.colorfulfog.model.vo.ListBillResultMapByTimeVO;
import com.ruoyi.colorfulfog.service.busniess.interfaces.CodeService;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import com.ruoyi.colorfulfog.utils.TimeUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CollectSchemeMainServiceImpl extends ServiceImpl<CollectSchemeMainMapper, CollectSchemeMain> implements CollectSchemeMainService{

    @Resource
    CollectSchemeDetailService collectSchemeDetailService;
    @Resource
    BillResultService billResultService;
    @Resource
    BillMainService billMainService;
    @Resource
    CodeService codeService;

    @Resource
    CollectResultMainService collectResultMainService;
    @Resource
    CollectResultService collectResultService;
    @Override
    public void createCollect(String collectCode){
        List<CollectSchemeDetail> collectSchemeDetails = collectSchemeDetailService.listByCode(collectCode);
        CollectSchemeMain collectSchemeMain = this.getOneByCode(collectCode);

        List<String> schemeCodeList = collectSchemeDetails.stream().map(CollectSchemeDetail::getSchemeCode)
                .collect(Collectors.toList());
        // 计划表的Map,key是计划表的code
        Map<String,List<CollectSchemeDetail>> collectSchemeDetailMap = collectSchemeDetails.stream()
                .collect(Collectors.groupingBy(CollectSchemeDetail::getSchemeCode));
        List<CollectResultDto> collectResultDtoList = new ArrayList<>();
        for (String schemeCode : schemeCodeList) {
            collectResultDtoList.add(
                    CollectResultDto.builder()
                            .schemeCode(schemeCode)
                            .timeFieldCode(collectSchemeDetailMap.get(schemeCode).get(0).getTimeField())
                            .timeStart(collectSchemeMain.getTimeStart())
                            .timeEnd(collectSchemeMain.getTimeEnd())
                            .build());
        }
        //一个计划表为key的map
        Map<String,List<BillResultDataVO>> schemeCodeResultMap = new HashMap<>();
        List<BillMain> billMainListAll = new ArrayList<>();
        for (CollectResultDto collectResultDto : collectResultDtoList) {
            ListBillResultMapByTimeVO listBillResultMapByTimeVO = billResultService.listBillResultMapByTime(collectResultDto);
            schemeCodeResultMap.put(collectResultDto.getSchemeCode(),listBillResultMapByTimeVO.getBillResultDataVOList());
            billMainListAll.addAll(listBillResultMapByTimeVO.getBillMainList());
        }
        Map<String,List<BillMain>> belongBillMainListMap = billMainListAll.stream().collect(Collectors.groupingBy(BillMain::getBelongArchiveCode));
        // 以上准备好需要收集的数据并做好转换
        // 将数据转换为map的形式
        Map<String,Map<String,Object>> collectResultMap = calculateSumField(schemeCodeResultMap,collectSchemeDetails,collectSchemeMain);
        // 将map转换为需要存储的格式()——突然发现为啥不用nosql的数据库呢？？
        List<CollectResultMain> collectResultMainList = new ArrayList<>();
        List<CollectResult> collectSchemeDetailList = new ArrayList<>();
        List<String> resultCodeList = codeService.getCode(IdTypeEnum.COLLECT_DATA_CODE,collectResultMap.keySet().size());
        int index = 0;
        // 一个所属一行数据，一个主表多个明细字段
        for (String belongCode : collectResultMap.keySet()) {
            for (CollectSchemeDetail collectSchemeDetail : collectSchemeDetails) {
                collectSchemeDetailList.add(CollectResult.builder()
                        .billCode(resultCodeList.get(index))
                        .schemeCode(collectSchemeDetail.getSchemeCode())
                        .resultCode(collectSchemeDetail.getSchemeResultCode())
                        .resultName(collectSchemeDetail.getSchemeResultName())
                        .type(collectSchemeDetail.getCollectType())
                        .value(collectResultMap.get(belongCode).get(collectSchemeDetail.getCollectResultCode()).toString())
                        .build());
            }
            collectResultMainList.add(CollectResultMain.builder()
                    .billCode(resultCodeList.get(index))
                    .schemeCode(collectSchemeMain.getCollectSchemeCode())
                    .belongArchiveCode(belongCode)
                    .belongArchiveName(belongBillMainListMap.get(belongCode).get(0).getBelongArchiveName())
                    .billType(belongBillMainListMap.get(belongCode).get(0).getBillType())
                    .costTerm(collectSchemeMain.getCostTerm())
                    .build());
            List<BillMain> billMains = belongBillMainListMap.get(belongCode);
            for (BillMain billMain : billMains) {
                billMain.setCostTerm(collectSchemeMain.getCostTerm());
                billMain.setCollectResultCode(resultCodeList.get(index));
            }
        }
        // 更新原始数据的账期和将汇总的code绑定到上面。
        // 保存数据到数据库
        billMainService.updateBatchById(billMainListAll);
        collectResultService.saveBatch(collectSchemeDetailList);
        collectResultMainService.saveBatch(collectResultMainList);

    }

    /**
     * 更具写入的总数据，
     */

    // 计算汇总的字段数值
    private  Map<String,Map<String,Object>> calculateSumField(Map<String,List<BillResultDataVO>> schemeCodeResultMap ,List<CollectSchemeDetail> collectSchemeDetails,CollectSchemeMain collectSchemeMain){
        // 收集结果的map,第一个key是账单分类的code,第二个key是收集的字段的Code，最后的vale是汇总的值
        Map<String,Map<String,Object>> collectResultMap = new HashMap<>();
        for (CollectSchemeDetail collectSchemeDetail : collectSchemeDetails) {
            List<BillResultDataVO> billResultDataVOList = schemeCodeResultMap.get(collectSchemeDetail.getSchemeCode());
            for (BillResultDataVO billResultDataVO : billResultDataVOList) {
                // 这里得到的是一行账单数据，
                // 需要根据汇总的字段，以及分类的字段，计算出汇总字段的数值，并赋值给汇总字段
                Map<String,String> dataMap = billResultDataVO.getKeyMap();
                // 所属的code
                String belong =  dataMap.get("belongArchiveCode");
                String value = dataMap.get(collectSchemeDetail.getSchemeResultCode());
                if (collectResultMap.get(belong) !=null){
                    if (collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.SUM)) {
                        if (collectResultMap.get(belong).get(collectSchemeDetail.getCollectResultCode()) != null) {
                            // 将value转换为long类
                            BigDecimal origin =  (BigDecimal) collectResultMap.get(belong).get(collectSchemeDetail.getCollectResultCode());
                            origin = origin.add(BigDecimal.valueOf(Double.parseDouble(value)));
                            collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), origin);
                        } else {
                            collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), BigDecimal.valueOf(Double.parseDouble(value)));
                        }
                    }else {
                        collectResultMap.get(belong).put(collectSchemeDetail.getCollectResultCode(), value);
                    }
                }else {
                    Map<String,Object> map = new HashMap<>();
                    if (collectSchemeDetail.getCollectType().equals(CollectSchemeDetail.CollectTypeEnum.SUM)) {
                        // 将String 的value转换为BigDecimal类
                        map.put(collectSchemeDetail.getCollectResultCode(), BigDecimal.valueOf(Double.parseDouble(value)));
                    }else {
                        map.put(collectSchemeDetail.getCollectResultCode(), value);
                    }
                    collectResultMap.put(belong,map);
                }
            }
        }
        return collectResultMap;


    }

    @Override
    public CollectSchemeMain getOneByCode(String code){
        CollectSchemeMain collectSchemeMain = getOne(new LambdaQueryWrapper<CollectSchemeMain>()
                .eq(CollectSchemeMain::getCollectSchemeCode, code));
        if (collectSchemeMain==null){
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"找不到汇总计划:"+code);
        }
        collectSchemeMain.setTimeStart(TimeUtils.transTimeFormula(collectSchemeMain.getTimeFormulaStart()));
        collectSchemeMain.setTimeEnd(TimeUtils.transTimeFormula(collectSchemeMain.getTimeFormulaEnd()));

        collectSchemeMain.setCostTerm(TimeUtils.transCostTermFormula(collectSchemeMain.getCostTermFormula()));
        return collectSchemeMain;
    }

    @Override
    public   List<CollectSchemeMain> listBySchemeCodeList(List<String> schemeCodeList){
        return list(new LambdaQueryWrapper<CollectSchemeMain>()
                .in(CollectSchemeMain::getCollectSchemeCode, schemeCodeList));
    }

    @Override
    public   List<CollectSchemeMain> listBySchemeCodeList(Set<String> schemeCodeList){
        return list(new LambdaQueryWrapper<CollectSchemeMain>()
                .in(CollectSchemeMain::getCollectSchemeCode, schemeCodeList));
    }

}
