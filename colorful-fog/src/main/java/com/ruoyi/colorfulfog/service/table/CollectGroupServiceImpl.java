package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.CollectGroupMapper;
import com.ruoyi.colorfulfog.model.CollectGroup;
import com.ruoyi.colorfulfog.service.table.interfaces.CollectGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CollectGroupServiceImpl extends ServiceImpl<CollectGroupMapper, CollectGroup> implements CollectGroupService {

    @Override
    public Map<String, List<CollectGroup>> getMapByCollectSchemeCode(String collectSchemeCode) {
        List<CollectGroup> collectGroups = this.list(new LambdaQueryWrapper<CollectGroup>().eq(CollectGroup::getCollectSchemeCode, collectSchemeCode));
        return collectGroups.stream().collect(Collectors.groupingBy(CollectGroup::getSchemeCode));
    }
}
