package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.DataRule;

import java.util.List;

/**
* @author zwh
* @date 2023/2/8 10:20
*/
public interface DataRuleService extends IService<DataRule>{

    List<DataRule> getDataRule(Long roleId);
    List<DataRule> getDataRule(String tableName, Long roleId);

    //设置新数据规则
    void setDataRule(Integer jobId, List<DataRule> dataRule);
}
