package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.system.domain.DataRule;
import com.ruoyi.system.mapper.DataRuleMapper;
import com.ruoyi.system.service.DataRuleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zwh
* @date 2023/2/8 10:20
*/
@Service
public class DataRuleServiceImpl extends ServiceImpl<DataRuleMapper, DataRule> implements DataRuleService {
    @Override
    public List<DataRule> getDataRule(Long jobId) {
        return list(new LambdaQueryWrapper<DataRule>().eq(DataRule::getJobId, jobId));
    }

    @Override
    public List<DataRule> getDataRule(String tableName, Long roleId) {
        return list(
                new LambdaQueryWrapper<DataRule>()
                        .eq(DataRule::getRuleTable, tableName)
                        .eq(DataRule::getJobId, roleId)
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setDataRule(Integer jobId, List<DataRule> dataRules) {
//        if (jobService.getById(jobId) == null) {
//            throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, "设置数据规则时，jobId不存在请注意");
//        }

//        deleteOldDataRule(jobId);
//
//        //因为新配置可能从旧配置那里复制来，所以得去掉基础表信息
//        for (DataRule rule : dataRules) {
//            if (!jobId.equals(rule.getJobId())) {
//                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, "设置数据规则时，id不一样，请注意");
//            }
//            if (StringUtils.isBlank(rule.getGroupCode())) {
//                throw new GlobalException(ErrorCodeEnum.PARAMETER_ERROR, "设置数据规则时，级别编码为空，请注意");
//            }
//            rule.setJobId(jobId);
//            rule.setId(null);
//            rule.setIsDelete(null);
//            rule.setCreateTime(null);
//            rule.setCreateUser(null);
//            rule.setUpdateTime(null);
//            rule.setUpdateUser(null);
//        }
//
//        dataRuleService.saveBatch(dataRules);
        return;
    }
}
