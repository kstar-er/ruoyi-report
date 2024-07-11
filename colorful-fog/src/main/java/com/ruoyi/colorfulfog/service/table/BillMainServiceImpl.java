package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.colorfulfog.constant.SysConstant;
import com.ruoyi.colorfulfog.mapper.BillMainMapper;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.vo.BillResultDataVO;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ResultNameCodeVO;
import com.ruoyi.colorfulfog.service.table.interfaces.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BillMainServiceImpl extends ServiceImpl<BillMainMapper, BillMain> implements BillMainService {

    @Autowired
    BillResultService billResultService;

    @Autowired
    SchemeDetailService schemeDetailService;

    @Autowired
    DependRuleService dependRuleService;
    @Autowired
    DependDataService dependDataService;
    @Override
    public BillResultVO list(BillResultDto billResultDto, Integer currentPage, Integer pageSize){
        BillMain billMain = new BillMain();
        BeanUtils.copyProperties(billResultDto, billMain);
        // 按方案编码，时间，账单编码等参数查询
        LambdaQueryWrapper<BillMain> queryWrapper = new LambdaQueryWrapper<>(billMain);

        // 时间戳的处理需要加一天，才可以获得结束日期当天的数据，前端传过来的是结束日期的0点
        if (billResultDto.getCreateStartTime() != null && billResultDto.getCreateEndTime() != null) {
            queryWrapper.ge(BillMain::getCreateTime, billResultDto.getCreateStartTime())
                    .le(BillMain::getCreateTime, billResultDto.getCreateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        if (billResultDto.getUpdateStartTime() != null && billResultDto.getUpdateEndTime() != null) {
            queryWrapper.ge(BillMain::getUpdateTime, billResultDto.getUpdateStartTime())
                    .le(BillMain::getUpdateTime, billResultDto.getUpdateEndTime() + SysConstant.Time.DAY_TIME_STAMP);
        }
        queryWrapper.orderByDesc(BillMain::getId);
        PageHelper.startPage(currentPage, pageSize);
        List<BillMain> billMainList = list(queryWrapper);

        return buildBillResultVO(billMainList,billResultDto);

    }

    @Override
    public BillResultVO buildBillResultVO(List<BillMain> billMainList,BillResultDto billResultDto){
        List<String> billCodeList = billMainList.stream().map(BillMain::getBillCode).distinct().collect(Collectors.toList());
        Map<String,List<BillResult>> billResultMap = billResultService.listBillCodeResultMapByBillCode(billCodeList);
        if (billResultMap == null){
            return null;
        }
        // 从billResultMap中获取任意一个value
        List<BillResult> billResultList = new ArrayList<>(billResultMap.values()).get(0);

        // 从billResultList中获取所有的resultName和resultCode存放到List<ResultNameCodeVO>中
        List<ResultNameCodeVO> resultNameCodeVOList = billResultList.stream()
                .map(billResult -> new ResultNameCodeVO(billResult.getResultName(), billResult.getResultCode(),billResult.getResultType(),billResult.getDisplayOrder()))
                .collect(Collectors.toList());
        List<BillResultDataVO> billResultDataVOList = new ArrayList<>();

        for (BillMain main : billMainList) {
            // todo 可以优化，减少将返回的参数变成，BillResultValueVO，减少传输的数据量
            billResultDataVOList.add(new BillResultDataVO(main, billResultMap.get(main.getBillCode())));
        }
        // 做再次的查询，
        billResultDataVOList = billResultDataVOList.stream().filter(s-> {
            if (billResultDto.getFieldSelectList()==null){
                return true;
            }
            for (String key: billResultDto.getFieldSelectList().keySet()) {
                if (!s.getKeyMap().get(key).equals(billResultDto.getFieldSelectList().get(key))){
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        BillResultVO billResultVO = new BillResultVO();
        billResultVO.setResultNameList(resultNameCodeVOList);
        billResultVO.setResultDataList(new PageInfo<>(billResultDataVOList));
        billResultVO.getResultDataList().setTotal(billResultDataVOList.size());

        return billResultVO;
    }

    @Override
    public     List<BillMain> listByCollectCode(List<String> collectCode){
        return list(new LambdaQueryWrapper<BillMain>()
                .in(BillMain::getCollectResultCode, collectCode));
    }

    @Override
    public List<BillMain> listBySchemeAndBatch(BillResultFlashDto billResultDto){
        return list(new LambdaQueryWrapper<BillMain>()
                .eq(BillMain::getSchemeCode, billResultDto.getSchemeCode())
                .eq(BillMain::getBatchCode,billResultDto.getBatchCode()));
            }



}



