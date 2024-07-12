package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.constant.enums.CollectDataTypeEnum;
import com.ruoyi.colorfulfog.constant.enums.CollectObjectEnum;
import com.ruoyi.colorfulfog.model.BillResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;

import java.util.List;
import java.util.Map;

public interface BillResultService extends IService<BillResult> {


    Map<String, List<BillResult>> listBillCodeResultMapByBillCode(List<String> billCode);

    List<BaseData> listBillResultMapByTime(CollectResultDto collectResultDto, CollectObjectEnum collectDataTypeEnum);

    List<BillData> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList);

    List<BillData> listByBillCode(List<String> billCode);
    List<BillData> listBySchemeAndBatch(BillResultFlashDto billResultDto);
    List<String> testSave();
}


