package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.BillResultFlashDto;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;

import java.util.List;

public interface BillMainService extends IService<BillMain> {

    BillResultVO list(BillResultDto billResultDto, Integer page, Integer size);

    List<BillMain> listByCollectCode(List<String> collectCode);
    BillResultVO buildBillResultVO(List<BillMain> billMainList,BillResultDto billResultDto);

    List<BillMain> listBySchemeAndBatch(BillResultFlashDto billResultDto);

}



