package com.ruoyi.colorfulfog.model.dto.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceDTO {
    /**
     * 查询哪个数据，账单表:billMain ,汇总表:collectResult
     */
    @NotNull
    private String sourceInterfaceName;
    /**
     * 汇总计划，或者方案管理的code值
     */
    @NotNull
    private String sourceValue;

    /**
     * X轴字段
     */
    private String xAxis;
    /**
     * Y轴字段
     */
    @NotNull
    private List<String> yAxis;
    /**
     * X轴是否为时间   （true 为时间类型查询，将忽略X轴字段）
     */
    @NotNull
    private Boolean xAxisIsTime;
    /**
     * 时间间隔为月还是天  （为月时，年必填，为天时，年月必填）
     */
    private String timeUnit;
    /**
     * 某年  （传年份即可例如： 2024）
     */
    private Integer year;
    /**
     * 某月 （传月即可 7）
     */
    private Integer month;


    List<FilterCriteria> filterCriteria;
}