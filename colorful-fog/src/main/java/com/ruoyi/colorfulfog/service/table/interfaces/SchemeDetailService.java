package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.SchemeDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.CopyFieldDto;
import com.ruoyi.colorfulfog.model.vo.ExportTemplateVO;

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

    /**
     * 导出字段模板，导出的字段包括基础账单字段
     * 通过判断字段是否是计算过程中需要用到的字段，决定是否必填
     *
     * @param schemeCode
     * @return
     */
    List<ExportTemplateVO> exportTemplate(String schemeCode);

    /**
     * 从schemeDetail中获取orderTable,排除掉orderTable是空的情况
     * @param schemeDetails
     * @return
     */
    List<String> getOrderTableName(List<SchemeDetail> schemeDetails);

}

