package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.DependRule;

import java.util.List;

public interface DependRuleService extends IService<DependRule> {

    /**
     * 深度搜索依赖表，需要解决循环依赖的问题
     *
     * @param codeList
     * @return
     */
    List<DependRule> getDependRuleByCode(List<String> codeList);
    List<DependRule> getDependRuleByCodeAndFilter(List<String> codeList,String schemeCode);

}

