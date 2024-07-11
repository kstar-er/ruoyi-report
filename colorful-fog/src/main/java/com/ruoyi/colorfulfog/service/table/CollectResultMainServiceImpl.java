package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.SysConstant;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.mapper.CollectResultMainMapper;
import com.ruoyi.colorfulfog.model.*;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultMainDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.entity.CollectDataTypeEntity;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.CollectBillResultDataVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import com.ruoyi.colorfulfog.model.vo.ResultNameCodeVO;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Override
    public BillResultVO list(CollectResultMainDto billResultDto, Integer currentPage, Integer pageSize){
        CollectResultMain collectResultMain = new CollectResultMain();
        BeanUtils.copyProperties(billResultDto, collectResultMain);
        LambdaQueryWrapper<CollectResultMain> queryWrapper = new LambdaQueryWrapper<>(collectResultMain);

        // 时间戳的处理需要加一天，才可以获得结束日期当天的数据，前端传过来的是结束日期的0点
        if (billResultDto.getCreateStartTime() != null && billResultDto.getCreateEndTime() != null) {
            queryWrapper.ge(CollectResultMain::getCreateTime, billResultDto.getCreateStartTime())
                    .le(CollectResultMain::getCreateTime, billResultDto.getCreateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        if (billResultDto.getUpdateStartTime() != null && billResultDto.getUpdateEndTime() != null) {
            queryWrapper.ge(CollectResultMain::getUpdateTime, billResultDto.getUpdateStartTime())
                    .le(CollectResultMain::getUpdateTime, billResultDto.getUpdateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        queryWrapper.orderByDesc(CollectResultMain::getId);
        PageHelper.startPage(currentPage, pageSize);
        List<CollectResultMain> billMainList = list(queryWrapper);

        return buildBillResultVO(billMainList);
    }

    private BillResultVO buildBillResultVO(List<CollectResultMain> billMainList) {
        List<String> billCodeList = billMainList.stream().map(CollectResultMain::getBillCode).distinct().collect(Collectors.toList());
        Map<String,List<CollectResult>> billResultMap = collectResultService.listBillCodeResultMapByBillCode(billCodeList);
        if (billResultMap == null){
            return null;
        }
        // 从billResultMap中获取任意一个value
        List<CollectResult> billResultList = new ArrayList<>(billResultMap.values()).get(0);

        // 从billResultList中获取所有的resultName和resultCode存放到List<ResultNameCodeVO>中
        List<ResultNameCodeVO> resultNameCodeVOList = billResultList.stream()
                .map(billResult -> new ResultNameCodeVO(billResult.getResultName(), billResult.getResultCode(),billResult.getResultType()))
                .collect(Collectors.toList());
        List<CollectBillResultDataVO> billResultDataVOList = new ArrayList<>();

        //  遍历billMainList，根据billMainList中的billCode去billResultMap中获取对应的resultDataList，然后存放到List<BillResultDataVO>中
        for (CollectResultMain main : billMainList) {
            // todo 可以优化，减少将返回的参数变成，BillResultValueVO，减少传输的数据量
            billResultDataVOList.add(new CollectBillResultDataVO(main, billResultMap.get(main.getBillCode())));
        }

        BillResultVO billResultVO = new BillResultVO();
        billResultVO.setResultNameList(resultNameCodeVOList);
        billResultVO.setResultDataList(new PageInfo<>(billResultDataVOList));
        return billResultVO;

    }

    @Override
    public  List<CollectResultMain> listByBillCode(List<String> billCodes){
        return list(new LambdaQueryWrapper<CollectResultMain>()
                .in(CollectResultMain::getBillCode,billCodes));
    }
    @Override
    public  List<CollectResultMain> listByBillCode(String billCodes){
        return list(new LambdaQueryWrapper<CollectResultMain>()
                .eq(CollectResultMain::getBillCode,billCodes));
    }
    @Override
    public     List<CollectResultMain> listByCollectCode(List<String> collectCode){
        return list(new LambdaQueryWrapper<CollectResultMain>()
                .in(CollectResultMain::getCollectResultCode,collectCode));
    }


    @Override
    public List<ExportExcelVO> exportExcel(String collectDataCode){
        // 获取所有的账单编号数据
        List<CollectDataTypeEntity> collectDataCodeList = new ArrayList<>();
        List<ExportExcelVO> exportExcelVOList = new ArrayList<>();
        List<CollectResultMain> collectResultMainList = listByBillCode(collectDataCode);
        if (CollectionUtils.isEmpty(collectResultMainList)){
            throw new GlobalException(ErrorCodeEnum.DATA_NOT_EXIST,collectDataCode);
        }
        CollectSchemeMain collectSchemeMain = collectSchemeMainService.getOneByCode(collectResultMainList.get(0).getSchemeCode());
        CollectDataTypeEntity collectDataTypeEntity = CollectDataTypeEntity.builder()
                .collectCodeList(Collections.singletonList(collectDataCode))
                .collectSchemeMain(collectSchemeMain)
                .collectResultMainList(collectResultMainList)
                .build();
        // 判断下一级取数是从哪里取到
        if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.COLLECT)){
            collectDataTypeEntity.setDataType(CollectObjectEnum.COLLECT);
        }else if (collectSchemeMain.getCollectObject().equals(CollectObjectEnum.BILL)){
            collectDataTypeEntity.setDataType(CollectObjectEnum.BILL);
        }
        collectDataCodeList.add(collectDataTypeEntity);
        getAllCode(collectDataCodeList.get(0),collectDataCodeList);
        // 现在拿到了全部主表的数据，以及对应的方案的名称
        for (CollectDataTypeEntity dataTypeEntity : collectDataCodeList) {
            if (dataTypeEntity.getCollectSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getCollectSchemeMain().getCollectSchemeName())
                        .billResultVO(buildBillResultVO(dataTypeEntity.getCollectResultMainList()))
                        .build());
            }else if (dataTypeEntity.getSchemeMain()!=null){
                exportExcelVOList.add(ExportExcelVO.builder()
                        .schemeName(dataTypeEntity.getSchemeMain().getName())
                        .billResultVO(billMainService.buildBillResultVO(dataTypeEntity.getBillMainList(),new BillResultDto()))
                        .build());
            }
        }
        return exportExcelVOList;
    }
    // 递归获取所有的编码，以及区分一下不同编码是哪个表的
    private void getAllCode(CollectDataTypeEntity collectDataTypeEntity, List<CollectDataTypeEntity> collectDataCodeList){
        if (collectDataTypeEntity.getDataType().equals(CollectObjectEnum.COLLECT)){
            List<CollectResultMain> collectResultMainList = listByCollectCode(collectDataTypeEntity.getCollectCodeList());
            Map<String,List<CollectResultMain>> collectResultMainMap = collectResultMainList.stream()
                    .collect(Collectors.groupingBy(CollectResultMain::getSchemeCode));
            // 需要收集的账单判断下一层收集的是什么类型
            List<CollectSchemeMain> collectSchemeMainList = collectSchemeMainService.listBySchemeCodeList((collectResultMainMap.keySet()));
            for (CollectSchemeMain collectSchemeMain : collectSchemeMainList) {
                String schemeCode = collectSchemeMain.getCollectSchemeCode();
                CollectDataTypeEntity collectDataTypeEntity1 = CollectDataTypeEntity.builder()
                        .collectCodeList(collectResultMainMap.get(schemeCode).stream().map(CollectResultMain::getBillCode).collect(Collectors.toList()))
                        .collectSchemeMain(collectSchemeMain)
                        .dataType(collectSchemeMain.getCollectObject())
                        .collectResultMainList(collectResultMainMap.get(schemeCode))
                        .build();
                collectDataCodeList.add(collectDataTypeEntity1);
                getAllCode(collectDataTypeEntity1,collectDataCodeList);
            }
        }else if (collectDataTypeEntity.getDataType().equals(CollectObjectEnum.BILL)){
            // 账单类型的话，递归在这里终止
            List<BillMain> billMainList = billMainService.listByCollectCode(collectDataTypeEntity.getCollectCodeList());
            Map<String,List<BillMain>> billMainMap = billMainList.stream().collect(Collectors.groupingBy(BillMain::getSchemeCode));
            List<SchemeMain> schemeMainList = schemeMainService.listByCode(billMainMap.keySet());

            for (SchemeMain schemeMain : schemeMainList) {
                String schemeCode = schemeMain.getSchemeCode();
                collectDataCodeList.add(CollectDataTypeEntity.builder()
                        .collectCodeList(billMainMap.get(schemeCode).stream().map(BillMain::getBillCode).collect(Collectors.toList()))
                                .billMainList(billMainMap.get(schemeCode))
                        .schemeMain(schemeMain)
                        .build());
            }
        }
    }

    @Override
    public String manualUpdate(List<ManualUpdateDto> manualUpdateDto){
        // 按类型进行分组
        Map<CollectObjectEnum,List<ManualUpdateDto>> collectObjectEnumListMap =
                manualUpdateDto.stream().collect(Collectors.groupingBy(ManualUpdateDto::getBillType));
        Map<String,BillResult> billResultMap;
        Map<String,CollectResult> collectBillResultMap;
        // 按类型进行处理
        List<UpdateBillLog> updateBillLogs = new ArrayList<>();
        List<ManualUpdateDto> billManualUpdateDtoList = collectObjectEnumListMap.get(CollectObjectEnum.BILL);
        if (billManualUpdateDtoList!= null){
            List<BillResult> billResultList = billResultService.listByBillCodeAndName(billManualUpdateDtoList);
            if (CollectionUtils.isEmpty(billResultList)){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"找不到对应的需要修改的字段");
            }
            // 使用code和name两个字段联合对billResultList分组
            billResultMap = billResultList.stream()
                    .collect(Collectors.toMap(s->String.format("%s-%s",s.getBillCode(),s.getResultName()),s->s));
            for (ManualUpdateDto updateDto : billManualUpdateDtoList) {
                BillResult billResult = billResultMap.get(String.format("%s-%s",updateDto.getBillCode(),updateDto.getFieldName()));
                if (!billResult.getValue().equals(updateDto.getUpdateValue())){
                    // 不等的时候才更新，相等就不更新了
                    updateBillLogs.add(UpdateBillLog.builder()
                            .resultType(updateDto.getBillType())
                            .originValue(billResult.getValue())
                            .afterValue(updateDto.getUpdateValue())
                            .resultName(updateDto.getFieldName())
                            .billCode(updateDto.getBillCode())
                            .build());
                    billResult.setValue(updateDto.getUpdateValue());
                }

            }
            billResultService.updateBatchById(billResultList);
        }

        List<ManualUpdateDto> collectManualUpdateDtoList = collectObjectEnumListMap.get(CollectObjectEnum.COLLECT);
        if (collectManualUpdateDtoList!= null){
            List<CollectResult> collectResultList = collectResultService.listByBillCodeAndName(collectManualUpdateDtoList);
            if (CollectionUtils.isEmpty(collectResultList)){
                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"找不到对应的需要修改的字段");
            }
            // 使用code和name两个字段联合对billResultList分组
            collectBillResultMap = collectResultList.stream()
                    .collect(Collectors.toMap(s->String.format("%s-%s",s.getBillCode(),s.getResultName()),s->s));
            for (ManualUpdateDto updateDto : collectManualUpdateDtoList) {
                CollectResult billResult = collectBillResultMap.get(String.format("%s-%s",updateDto.getBillCode(),updateDto.getFieldName()));
                if (!billResult.getValue().equals(updateDto.getUpdateValue())){
                    updateBillLogs.add(UpdateBillLog.builder()
                            .resultType(updateDto.getBillType())
                            .originValue(billResult.getValue())
                            .afterValue(updateDto.getUpdateValue())
                            .resultName(updateDto.getFieldName())
                            .billCode(updateDto.getBillCode())
                            .build());
                    billResult.setValue(updateDto.getUpdateValue());
                }
            }
            collectResultService.updateBatchById(collectResultList);
        }
        if (!updateBillLogs.isEmpty()){
            updateBillLogService.saveBatch(updateBillLogs);
            return "本次更新成功条数："+updateBillLogs.size();
        }
        return "本次更新成功条数：0";
    }
}
