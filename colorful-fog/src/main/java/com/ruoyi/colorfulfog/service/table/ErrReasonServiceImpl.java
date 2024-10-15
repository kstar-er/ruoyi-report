package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.mapper.ErrReasonMapper;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.ErrReason;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.ExportErrReason;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.ErrReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ErrReasonServiceImpl extends ServiceImpl<ErrReasonMapper, ErrReason> implements ErrReasonService{

    @Autowired
    DependDataService dependDataService;

    @Autowired
    DependMainService dependMainService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BillMainService billMainService;


    @Transactional
    @Override
    public boolean dealErrReason(List<ErrReason> errReason){
        // 处理errReason数据
        List<ErrReason> haveDealErr = new ArrayList<>();
        haveDealErr.addAll(errReason.stream().filter(err -> err.getDealFlag()!=null && err.getDealFlag() == 1).collect(Collectors.toList()));
        errReason = errReason.stream().filter(err -> err.getValue() != null).collect(Collectors.toList());

        // 将errReason数据转换为DependData数据
        List<DependData> dependDataList = new ArrayList<>();
        Map<String, DependMain> dependMainMap = dependMainService.getDependMainMap(errReason.stream().map(ErrReason::getDependCode)
                .collect(Collectors.toList()));
        for (ErrReason reason : errReason) {
            if (dependMainMap.get(reason.getDependCode())==null){
                throw new GlobalException(reason.getDependCode()+"依赖主表已经不存在","");
            }
            // 只有分类依赖的情况可以导入数据
            if (DependTypeEnum.SORT.equals(dependMainMap.get(reason.getDependCode()).getDependType())){
                DependData dependData = DependData.builder()
                        .dependCode(reason.getDependCode())
                        .key(reason.getKey())
                        .value(reason.getValue())
                        .build();
                haveDealErr.add(reason);
                dependDataList.add(dependData);
            }
        }
        updateErrReason(haveDealErr);
        // 处理依赖表的数据
        if (dependDataList.size()>0){
            dependDataService.addBatch(dependDataList);
        }
        if (haveDealErr.size()==errReason.size()){
            return true;
        }{
            return false;
        }
    }
    @Override
    public List<ErrReason> listErrReasonByBillCode(List<String> billCodeList){
        return list(new LambdaQueryWrapper<ErrReason>()
                .in(ErrReason::getBillCode,billCodeList)
                .eq(ErrReason::getDealFlag,0));
    }

    @Transactional
    @Override
    public ExportErrReason exportErrReasonList(List<Integer> errReasonIds){
        ExportErrReason exportErrReason = new ExportErrReason();
        List<ErrReason> errReasonList = listByIds(errReasonIds);
        Map<String,List<ErrReason>> errReasonMap = errReasonList.stream().collect(Collectors.groupingBy(ErrReason::getSchemeCode));
        exportErrReason.setErrReasonList(errReasonList);
        List<String> billCodeList = errReasonList.stream().map(ErrReason::getBillCode).collect(Collectors.toList());
        List<BillData> billDataList = mongoTemplate.find(new Query(Criteria.where("billCode").in(billCodeList)), BillData.class);
        Map<String,List<BillData>> billDataMap = billDataList.stream().collect(Collectors.groupingBy(BillData::getSchemeCode));
        List<ExportExcelVO> exportExcelVOList = new ArrayList<>();
        billDataMap.forEach((schemeCode,billDataList1)->{
           exportExcelVOList.add(ExportExcelVO.builder()
                           .schemeName(errReasonMap.get(schemeCode).get(0).getSchemeName())
                           .billResultVO(billMainService.buildBillResultVO(billDataList1, SelectTypeEnum.CALC))
                   .build());
        });
        exportErrReason.setExportExcelVOList(exportExcelVOList);
        return exportErrReason;
    }

    /**
     * 根据dependCode和Key去重保存新的errReason
     * @param errReason
     */
    @Transactional
    @Override
    public void saveErrReasonBatch(List<ErrReason> errReason){
        // 对errReason去重，根据reason.getDependCode(), reason.getKey()两个字段去重
        Set<ErrReason> uniqueErrReasons = new LinkedHashSet<>(errReason);
        errReason = new ArrayList<>(uniqueErrReasons);
        List<ErrReason> errReasonOld = list(new LambdaQueryWrapper<ErrReason>()
                .eq(ErrReason::getDealFlag,0)
                .in(ErrReason::getDependCode, errReason.stream().map(ErrReason::getDependCode).collect(Collectors.toList()))
                .in(ErrReason::getKey, errReason.stream().map(ErrReason::getKey).collect(Collectors.toList()))
        );
        Map<String, ErrReason> errReasonMap = errReasonOld.stream().collect(Collectors.toMap(
                arr -> String.format("%s[-]%s", arr.getDependCode(), arr.getKey()), errReason1 -> errReason1,
                (existing, replacement) -> existing
        ));
        errReason.removeIf(reason -> errReasonMap.get(String.format("%s[-]%s", reason.getDependCode(), reason.getKey())) != null);
        saveBatch(errReason);
    }

    private void updateErrReason(List<ErrReason> errReason){
        errReason = errReason.stream().distinct().collect(Collectors.toList());
        for (ErrReason reason : errReason) {
            reason.setDealFlag(1);
        }
        this.updateBatchById(errReason);
    }
}
