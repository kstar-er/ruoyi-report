package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectResultMapper;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectResultService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class CollectResultServiceImpl extends ServiceImpl<CollectResultMapper, CollectResult> implements CollectResultService{

    @Override
    public Map<String, List<CollectResult>> listBillCodeResultMapByBillCode(List<String> billCode){
            if (CollectionUtils.isEmpty(billCode)){
                return null;
            }
            return list(new LambdaQueryWrapper<CollectResult>().in(CollectResult::getBillCode, billCode))
                    .stream().collect(Collectors.groupingBy(CollectResult::getBillCode));
    }
    @Override
    public List<CollectResult> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList){
        return baseMapper.selectByCodeAndName(manualUpdateDtoList);
    }
}
