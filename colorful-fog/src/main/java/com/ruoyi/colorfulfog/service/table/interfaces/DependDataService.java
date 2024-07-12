package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.model.DependData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.DependDataDto;

import java.util.List;
import java.util.Map;

public interface DependDataService extends IService<DependData> {

    Map<String, List<DependData>> getDependDataMap(List<String> codeList);

    void addBatch(List<DependData> dependDataList);
    List<DependData> select(DependDataDto dependData,Integer currentPage,Integer pageSize);
}

