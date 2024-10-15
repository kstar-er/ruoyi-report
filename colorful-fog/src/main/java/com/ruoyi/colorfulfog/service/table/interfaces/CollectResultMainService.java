package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.mongodb.CollectBillData;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;
import com.ruoyi.colorfulfog.model.vo.ExportExcelVO;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

public interface CollectResultMainService extends IService<CollectResultMain>{
    BillResultVO list(CollectResultMainDto billResultDto, Integer page, Integer size);
    List<CollectResultMain> listByBillCode(List<String> billCodes);
    List<CollectBillData> listById(String billCodes);
    List<CollectBillData> listByIdList(List<String> billCodes);
    List<CollectBillData> listByBillCode(String billCodes);

    Map<String, Map<String, Double>> list(DataSourceDTO dataSourceDTO, List<FilterCriteria> filterCriteriaList);

    void buildQueryCriteria(Query query, FilterCriteria filterCriteria, String fieldName, List<String> value);

    BillResultVO listByUserCode(CollectResultMainDto billResultDto, Integer page, Integer size, SelectTypeEnum selectTypeEnum);
    BillResultVO buildBillResultVO(List<CollectBillData> billMainList,SelectTypeEnum selectTypeEnum);
    /**
     * 上层的收集方法对结果进行汇总
     *
     * @param collectCode
     * @return
     */
    List<CollectBillData> listByCollectCode(List<String> collectCode);
    List<ExportExcelVO> exportExcel(ExportExcelDto exportExcelDto,SelectTypeEnum selectTypeEnum);
    List<ExportExcelVO> exportExcelBatch(List<String> idList,SelectTypeEnum selectTypeEnum);

    // 更新接口，需要传入账单编号，以及需要更新的字段
    // 根据不同的类型调用不同表的更新方法
    String manualUpdate(List<ManualUpdateDto> manualUpdateDto);

    String audit(AuditDto auditDto);

    List<CollectBillData> listBillResultMapByTime(CollectResultDto collectResultDto);
    void updateBillData(CollectBillData collectBillData);
    List<CollectBillData> getCostTerm(GetCostTermDto getCostTermDto);

    /**
     * 通过账单编码删除
     * @param billCode
     */
    void deleteByIds(List<String> billCode);
    String billDataManualUpdate(List<ManualUpdateDto> billManualUpdateDtoList,String updateUser);
    String collectBillDataManualUpdate(List<ManualUpdateDto> collectManualUpdateDtoList,String updateUser);


}
