package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.SchemeDetail;

import java.util.List;
import java.util.Map;

public interface SchemeDetailService extends IService<SchemeDetail> {

    Map<String, List<SchemeDetail>> getSchemeDetailBySchemeCode(List<String> schemeCodeList);

    List<SchemeDetail> listSchemeDetailBySchemeCode(List<String> schemeCodeList);
    List<SchemeDetail> listSchemeDetailBySchemeCode(String schemeCode);

    void addSchemeDetailBatch(List<SchemeDetail> schemeDetailList);

    void updateSchemeDetailBatch(List<SchemeDetail> schemeDetailList);


    List<SchemeDetail> calculateOrder(List<SchemeDetail> schemeDetailList);

    void deleteSchemeDetail(List<Integer> ids);

}

