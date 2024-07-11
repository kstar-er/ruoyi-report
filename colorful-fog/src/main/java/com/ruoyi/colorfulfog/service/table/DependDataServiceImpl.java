package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.DependDataMapper;
import com.ruoyi.colorfulfog.model.DependData;
import com.ruoyi.colorfulfog.service.table.interfaces.DependDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DependDataServiceImpl extends ServiceImpl<DependDataMapper, DependData> implements DependDataService {

    @Override
    public Map<String, List<DependData>> getDependDataMap(List<String> codeList) {
        if (CollectionUtils.isEmpty(codeList)){
            return null;
        }
        codeList = codeList.stream().distinct().collect(Collectors.toList());

        return list(new LambdaQueryWrapper<DependData>().in(DependData::getDependCode, codeList)).stream().
                collect(Collectors.groupingBy(DependData::getDependCode));
    }
}

