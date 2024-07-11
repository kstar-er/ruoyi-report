package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.mapper.DependRuleMapper;
import com.ruoyi.colorfulfog.model.DependRule;
import com.ruoyi.colorfulfog.service.table.interfaces.DependRuleService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DependRuleServiceImpl extends ServiceImpl<DependRuleMapper, DependRule> implements DependRuleService {

    @Override
    public List<DependRule> getDependRuleByCode(List<String> codeList) {
        // 将dependRuleList根据dependCode进行分组
        Map<String, List<DependRule>> dependRuleMap = new HashMap<>();
        codeList = codeList.stream().filter(s ->  s != null && !s.isEmpty()).collect(Collectors.toList());
        dependRuleMap = getDependRuleMapByCode(codeList, dependRuleMap);
        // 如果dependRuleMap为空，则返回空List
        if (dependRuleMap.isEmpty()) {
            return null;
        }
        // 将dependRuleMap转回List
        return dependRuleMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    private Map<String, List<DependRule>> getDependRuleMapByCode(List<String> codeList, Map<String, List<DependRule>> dependRuleMap) {
        if (codeList.isEmpty()){
            return dependRuleMap;
        }
        List<DependRule> dependRuleList = list(new LambdaQueryWrapper<DependRule>().in(DependRule::getDependCode, codeList));
        // 将dependRuleList根据dependCode进行分组

        Map<String, List<DependRule>> newDependRuleMap = dependRuleList.stream().collect(Collectors.groupingBy(DependRule::getDependCode));

        // 将newDependRuleMap和dependRuleMap进行合并
        dependRuleMap.putAll(newDependRuleMap);
        List<String> nextDependCodeList = dependRuleList.stream().filter(Objects::nonNull).map(DependRule::getSecondDependCode).collect(Collectors.toList());
        nextDependCodeList = nextDependCodeList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        // 如果dependRuleMap为空，则返回空List
        if (CollectionUtils.isEmpty(nextDependCodeList)) {
            return dependRuleMap;
        }
        nextDependCodeList = nextDependCodeList.stream().filter(s -> !dependRuleMap.containsKey(s)).collect(Collectors.toList());
        return getDependRuleMapByCode(nextDependCodeList, dependRuleMap);

    }
    public   List<DependRule> getDependRuleByCodeAndFilter(List<String> codeList,String schemeCode){
        return getDependRuleByCode(codeList).stream()
                .filter(dependRule -> dependRule.getSchemeCode().equals(schemeCode)).collect(Collectors.toList());
    }
}

