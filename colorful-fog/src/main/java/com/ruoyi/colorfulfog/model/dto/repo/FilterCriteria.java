package com.ruoyi.colorfulfog.model.dto.repo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterCriteria {
    //字段名
    private String fieldName;
    //比较操作符
    private String operator;
    //查询的值
    private List<String> value;
}
