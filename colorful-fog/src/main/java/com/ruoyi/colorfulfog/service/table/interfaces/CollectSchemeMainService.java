package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.CollectSchemeMain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.CreateAndCollectDto;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;

import java.util.List;
import java.util.Set;

public interface CollectSchemeMainService extends IService<CollectSchemeMain>{

    CollectSchemeMain getOneByCode(String code);
    void createCollect(String collectCode, String belongArchiveCode);
    List<ExportExcelVO> createAndCollect(CreateAndCollectDto createAndCollectDto);




    List<CollectSchemeMain> listBySchemeCodeList(List<String> schemeCodeList);
    List<CollectSchemeMain> listBySchemeCodeList(Set<String> schemeCodeList);
    void flashCollect(List<BillData> billDataList,String collectResultCode);
}
