package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.ErrorCodeEnum;
import com.ruoyi.colorfulfog.mapper.BillResultMapper;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.vo.BillResultDataVO;
import com.ruoyi.colorfulfog.model.vo.ListBillResultMapByTimeVO;
import com.ruoyi.colorfulfog.service.table.interfaces.BillMainService;
import com.ruoyi.colorfulfog.service.table.interfaces.BillResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillResultServiceImpl extends ServiceImpl<BillResultMapper, BillResult> implements BillResultService {

    @Autowired
    BillMainService billMainService;
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
    public ListBillResultMapByTimeVO listBillResultMapByTime(CollectResultDto collectResultDto){
        List<BillResult> billResultList = list(new LambdaQueryWrapper<BillResult>()
                .eq(BillResult::getSchemeCode,collectResultDto.getSchemeCode())
                .eq(BillResult::getResultCode,collectResultDto.getTimeFieldCode())
                .ge(BillResult::getValue,collectResultDto.getTimeStart())
                .le(BillResult::getValue,collectResultDto.getTimeEnd()));
        List<String> billCodeList = billResultList.stream().map(BillResult::getBillCode).collect(Collectors.toList());
        Map<String, List<BillResult>> billResultMap =  listBillCodeResultMapByBillCode(billCodeList);
        List<BillResultDataVO> billResultDataVOList = new ArrayList<>();
        List<BillMain> billMainList ;
        if (billCodeList.isEmpty()){
            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR,"对应时间账单数据未生成");
        }
        billMainList = billMainService.list(new LambdaQueryWrapper<BillMain>().in(BillMain::getBillCode, billCodeList));
        //  遍历billMainList，根据billMainList中的billCode去billResultMap中获取对应的resultDataList，然后存放到List<BillResultDataVO>中
        for (BillMain main : billMainList) {
            // todo 可以优化，减少将返回的参数变成，BillResultValueVO，减少传输的数据量
            billResultDataVOList.add(new BillResultDataVO(main, billResultMap.get(main.getBillCode())));
        }
        return ListBillResultMapByTimeVO.builder()
                .billMainList(billMainList)
                .billResultDataVOList(billResultDataVOList)
                .build();
    }
    @Override
    public  List<BillResult> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList){
        return  baseMapper.selectByCodeAndName(manualUpdateDtoList);
    }
    @Override
    public  List<BillResult> listByBillCode(List<String> billCode){
        return list(new LambdaQueryWrapper<BillResult>()
                .in(BillResult::getBillCode, billCode));
    }
    @Override
    public List<BillResult> listBySchemeAndBatch(BillResultFlashDto billResultDto){
        return list(new LambdaQueryWrapper<BillResult>()
                .eq(BillResult::getBatchCode,billResultDto.getBatchCode()));
    }

}


