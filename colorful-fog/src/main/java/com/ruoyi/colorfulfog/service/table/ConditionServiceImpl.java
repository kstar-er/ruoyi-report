package com.ruoyi.colorfulfog.service.table;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.colorfulfog.model.Condition;
import com.ruoyi.colorfulfog.service.table.interfaces.ConditionService;
import com.ruoyi.colorfulfog.mapper.ConditionMapper;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jxwlUser
 * @description 针对表【cwu_condition】的数据库操作Service实现
 * @createDate 2023-10-13 10:47:54
 */
@Service
public class ConditionServiceImpl extends ServiceImpl<ConditionMapper, Condition>
        implements ConditionService {

    @Override
    public Map<String, Condition> getConditionMap(String schemeCode) {
        return list(new LambdaQueryWrapper<Condition>().eq(Condition::getSchemeCode, schemeCode))
                .stream().collect(Collectors.toMap(Condition::getTableName, s -> s));
    }
    @Override
    public List<Condition> listConditionsBySchemeCode(String schemeCode){
        return list(new LambdaQueryWrapper<Condition>().eq(Condition::getSchemeCode, schemeCode));
    }
}





