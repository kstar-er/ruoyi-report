package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.dto.CollectResultDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.vo.ListBillResultMapByTimeVO;

import java.util.List;
import java.util.Map;

public interface BillResultService extends IService<BillResult> {


    Map<String, List<BillResult>> listBillCodeResultMapByBillCode(List<String> billCode);

    ListBillResultMapByTimeVO listBillResultMapByTime(CollectResultDto collectResultDto);

    List<BillResult> listByBillCodeAndName(List<ManualUpdateDto> manualUpdateDtoList);

    List<BillResult> listByBillCode(List<String> billCode);
    List<BillResult> listBySchemeAndBatch(BillResultFlashDto billResultDto);

}


