package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.dto.CollectResultMainDto;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;

import java.util.List;

public interface CollectResultMainService extends IService<CollectResultMain>{
    BillResultVO list(CollectResultMainDto billResultDto, Integer page, Integer size);
    List<CollectResultMain> listByBillCode(List<String> billCodes);
    List<CollectResultMain> listByBillCode(String billCodes);

    /**
     * 上层的收集方法对结果进行汇总
     * @param collectCode
     * @return
     */
    List<CollectResultMain> listByCollectCode(List<String> collectCode);
    List<ExportExcelVO> exportExcel(String collectDataCode);

    String manualUpdate(List<ManualUpdateDto> manualUpdateDto);
}
