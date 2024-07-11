package com.ruoyi.colorfulfog.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 导出汇总数据的excel返回对象
 */
@Builder
@Data
public class ExportExcelVO {
    String schemeName;
    BillResultVO billResultVO;
}
