package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;

import java.util.List;
import java.util.Map;

public interface CollectResultService extends IService<CollectResult>{

    Map<String, List<CollectResult>> listBillCodeResultMapByBillCode(List<String> billCode);
    List<CollectResult> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList);

}
