package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.CopyFieldDto;

import java.util.List;
import java.util.Map;

public interface SchemeDetailService extends IService<SchemeDetail> {

    Map<String, List<SchemeDetail>> getSchemeDetailBySchemeCode(List<String> schemeCodeList);

    List<SchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList, SelectTypeEnum selectTypeEnum);
    List<SchemeDetail> listSchemeDetailBySchemeCode(String schemeCode,SelectTypeEnum selectTypeEnum);

    void addSchemeDetailBatch(List<SchemeDetail> schemeDetailList);

    /**
     * 单个更新接口，更新完后需要重新对计算顺序进行排序。所以只更新单个。
     * @param schemeDetailList
     */
    void updateSchemeDetailBatch(List<SchemeDetail> schemeDetailList);


    List<SchemeDetail> calculateOrder(List<SchemeDetail> schemeDetailList);

    void deleteSchemeDetail(List<Integer> ids);
    void copyField(CopyFieldDto copyFieldDto);

}

