package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.ExportErrReason;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.model.ErrReason;
import com.ruoyi.colorfulfog.mapper.ErrReasonMapper;
import com.ruoyi.colorfulfog.service.table.interfaces.ErrReasonService;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

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
        dependDataService.saveBatch(dependDataList);
        if (haveDealErr.size()==errReason.size()){
            return true;
        }{
            return false;
        }
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
                           .billResultVO(billMainService.buildBillResultVO(billDataList1, SelectTypeEnum.EXPORT))
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
        List<ErrReason> errReasons = list(new LambdaQueryWrapper<ErrReason>()
                .eq(ErrReason::getDealFlag,0)
                .in(ErrReason::getDependCode, errReason.stream().map(ErrReason::getDependCode).collect(Collectors.toList()))
                .in(ErrReason::getKey, errReason.stream().map(ErrReason::getKey).collect(Collectors.toList()))
        );
        // 去重errReason
        errReason = errReason.stream().collect(
                collectingAndThen(
                        toCollection(
                                () -> new TreeSet<>(comparing(ErrReason::getDependCode).thenComparing(ErrReason::getKey))
                        ),
                        ArrayList::new
                )
        );
        Map<String, ErrReason> errReasonMap = errReasons.stream().collect(Collectors.toMap(
                arr -> String.format("%s[-]%s", arr.getDependCode(), arr.getKey()), errReason1 -> errReason1
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
