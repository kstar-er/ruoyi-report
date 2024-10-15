package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.CollectGroup;

import java.util.List;
import java.util.Map;

public interface CollectGroupService extends IService<CollectGroup>{

    Map<String, List<CollectGroup>> getMapByCollectSchemeCode(String collectSchemeCode);


}
