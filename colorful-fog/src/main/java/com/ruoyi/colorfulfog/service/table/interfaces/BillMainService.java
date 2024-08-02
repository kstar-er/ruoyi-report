package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.dto.AddDataManualDto;
import com.ruoyi.colorfulfog.model.dto.BillResultDto;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface BillMainService extends IService<BillMain> {

    BillResultVO list(BillResultDto billResultDto, Integer page, Integer size);

    List<BillData> listByCollectCode(List<String> collectCode);

    Map<String, List<Map<String, Double>>> list(DataSourceDTO dataSourceDTO, List<FilterCriteria> filterCriteriaList);

    @NotNull
    Map<String, List<Map<String, Double>>> getAggregatedDataMap(DataSourceDTO dataSourceDTO, List<BaseData> billMainList);

    BillResultVO buildBillResultVO(List<BillData> billMainList, SelectTypeEnum selectTypeEnum);

    /**
     * 设置账单类型为无效，同时刷新账单数据
     */
    String setInValid(List<String> billCode);

    /**
     * 手动添加账单数据
     * @param billDataList
     */
    void addDataManual(List<AddDataManualDto> billDataList);
}



