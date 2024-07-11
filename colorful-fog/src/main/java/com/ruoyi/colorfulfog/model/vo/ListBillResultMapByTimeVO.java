package com.ruoyi.colorfulfog.model.vo;

import com.ruoyi.colorfulfog.model.BillMain;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ListBillResultMapByTimeVO {
    List<BillResultDataVO> billResultDataVOList;
    List<BillMain> billMainList;
}
