package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.SchemeDetail;

import java.util.List;

public interface CollectSchemeDetailService extends IService<CollectSchemeDetail>{

    List<CollectSchemeDetail> listByCode(String code);
    List<CollectSchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList, SelectTypeEnum selectTypeEnum);

    void calculateOrder(String collectSchemeCode);
}
