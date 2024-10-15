package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.BillMain;
import com.ruoyi.colorfulfog.model.SchemeMain;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.mongodb.BaseData;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface BillMainService extends IService<BillMain> {

    BillResultVO list(BillResultDto billResultDto, Integer page, Integer size,SelectTypeEnum selectTypeEnum);

    List<BillData> listByCollectCode(List<String> collectCode);

    Map<String, Map<String, Double>> list(DataSourceDTO dataSourceDTO, List<FilterCriteria> filterCriteriaList);

    /**
     *
     * @param dataSourceDTO
     * @param billMainList
     * @return 返回聚合后的数据
     * 第一个String是：X轴的分组的值，如果2024-6，2024-7
     * 第二次的List表示：！这里的List实际是多余的一个结构，List里面的Map是多余的！
     * List内Map的String取值是：Y轴的字段名称
     * Double是：Y轴最后展示的汇总过的值
     */
    @NotNull
    Map<String, Map<String, Double>> getAggregatedDataMap(DataSourceDTO dataSourceDTO, List<BaseData> billMainList);

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
    List<String> getBatchCode(ReCreateByUserCode reCreateByUserCode);
    /**
     * 预修改账单数据，用户提交预修改，财务进行审核，审核通过后修改到正式数据
     *
     */
    void preUpdateData(List<ManualUpdateDto> manualUpdateDtoList);
    /**
     * 对预修改数据进行审核，审核通过后修改到正式数据
     * 审核不通过的只是修改预修改数据状态。
     */
    void auditPreUpdateData(List<AuditDto> auditDtoList);
    List<SchemeMain> getPreDataSchemeCodeByCostTerm(GetPreDataSchemeCodeDto getPreDataSchemeCodeDto);
}



