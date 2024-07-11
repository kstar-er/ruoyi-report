package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.CollectSchemeMain;

import java.util.List;
import java.util.Set;

public interface CollectSchemeMainService extends IService<CollectSchemeMain>{

    CollectSchemeMain getOneByCode(String code);
    void createCollect(String collectCode);



   List<CollectSchemeMain> listBySchemeCodeList(List<String> schemeCodeList);
    List<CollectSchemeMain> listBySchemeCodeList(Set<String> schemeCodeList);
}
