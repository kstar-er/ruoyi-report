package com.ruoyi.colorfulfog.service.table.interfaces;

import com.ruoyi.colorfulfog.constant.enums.SelectTypeEnum;
import com.ruoyi.colorfulfog.model.CollectResultMain;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.model.dto.*;
import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.mongodb.BillData;
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

    Map<String, List<Map<String, Double>>> list(DataSourceDTO dataSourceDTO, List<FilterCriteria> filterCriteriaList);

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

    String manualUpdate(List<ManualUpdateDto> manualUpdateDto);

    String audit(AuditDto auditDto);

    List<CollectBillData> listBillResultMapByTime(CollectResultDto collectResultDto);
    void updateBillData(CollectBillData collectBillData);
}
