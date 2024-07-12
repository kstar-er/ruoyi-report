package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Range;
import com.ruoyi.colorfulfog.config.exception.GlobalException;
import com.ruoyi.colorfulfog.constant.enums.DependTypeEnum;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.DependMain;
import com.ruoyi.colorfulfog.model.dto.DependDataDto;
import com.ruoyi.colorfulfog.model.vo.CalculateValueVO;
import com.ruoyi.colorfulfog.service.table.interfaces.DependMainService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.mapper.DependDataMapper;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;

import javax.annotation.Resource;

@Service
public class DependDataServiceImpl extends ServiceImpl<DependDataMapper, DependData> implements DependDataService {

    @Resource
    DependMainService dependMainService;
    @Override
    public Map<String, List<DependData>> getDependDataMap(List<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)){
            return null;
        }
        codeList = codeList.stream().distinct().collect(Collectors.toList());

        return list(new LambdaQueryWrapper<DependData>().in(DependData::getDependCode, codeList)).stream().
                collect(Collectors.groupingBy(DependData::getDependCode));
    }
    @Override
    public void addBatch(List<DependData> dependDataList){

        List<String> keyList = dependDataList.stream().map(DependData::getKey).collect(Collectors.toList());
        // 获取keyList中重复的Key值
        List<String> duplicateKeys = keyList.stream().filter(key -> Collections.frequency(keyList, key) > 1).collect(Collectors.toList());
        // 判断是否存在重复的key值
        if(!duplicateKeys.isEmpty()) {
            throw new GlobalException("存在重复的key值：" + duplicateKeys, "请检查excel表中的数据和已导入数据");
        }
        List<DependData> originDependDataList = list(new LambdaQueryWrapper<DependData>()
                .eq(DependData::getDependCode, dependDataList.get(0).getDependCode()));
        Map<String,DependData> originDependDataMap = originDependDataList.stream().collect(Collectors.toMap(DependData::getKey, v -> v));
        List<DependData> removeList = new ArrayList<>();
        for (DependData dependData : dependDataList) {
            if ( originDependDataMap.get(dependData.getKey())!=null){
                removeList.add(originDependDataMap.get(dependData.getKey()));
            }
        }
        if (!removeList.isEmpty()){
            removeBatchByIds(removeList.stream().map(DependData::getId).collect(Collectors.toList()));
        }
        saveBatch(dependDataList);
    }
    @Override
    public List<DependData> select(DependDataDto dependDatadto, Integer currentPage, Integer pageSize){
        LambdaQueryWrapper<DependData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DependData::getDependCode, dependDatadto.getDependCode())
                .like(dependDatadto.getValue()!=null,DependData::getValue, dependDatadto.getValue())
                .like(dependDatadto.getKey()!=null,DependData::getKey, dependDatadto.getKey());
        if (dependDatadto.getRange()==null){
            PageHelper.startPage(currentPage, pageSize);
        }
        List<DependData> dependDataList = list(queryWrapper);
        if (dependDatadto.getRange()!=null){
            BigDecimal bigValue = new BigDecimal(dependDatadto.getRange());
            BigDecimal startValue = null;
            BigDecimal endValue = null;
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
                    List<DependData > dependDataAns = new ArrayList<>();
                     dependDataAns.add(dependData);
                    return dependDataAns;
                }
            }
        }
        return dependDataList;
    }

}

