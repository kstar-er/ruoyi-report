package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.Condition;

import java.util.List;
import java.util.Map;

/**
 * @author jxwlUser
 * @description 针对表【cwu_condition】的数据库操作Service
 * @createDate 2023-10-13 10:47:54
 */
public interface ConditionService extends IService<Condition> {

    Map<String, Condition> getConditionMap(String schemeCode);
    List<Condition> listConditionsBySchemeCode(String schemeCode);
}

