package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.CollectSchemeDetail;

import java.util.List;

public interface CollectSchemeDetailService extends IService<CollectSchemeDetail>{

    List<CollectSchemeDetail> listByCode(String code);

    /**
     * 获取collectSchemeCode对应的collectSchemeDetail
     * @param schemeCodeList
     * @param selectTypeEnum
     * @return
     */
    List<CollectSchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList, SelectTypeEnum selectTypeEnum);

    /**
     * 获取schemeCode对应的collectSchemeDetail
     * @param code
     * @return
     */
    List<CollectSchemeDetail> listBySchemeCode(String code);
    void calculateOrder(String collectSchemeCode);
}
