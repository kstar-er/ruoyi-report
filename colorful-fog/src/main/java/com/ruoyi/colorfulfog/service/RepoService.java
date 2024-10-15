package com.ruoyi.colorfulfog.service;

import com.ruoyi.colorfulfog.model.dto.repo.DataSourceDTO;
import com.ruoyi.colorfulfog.model.dto.repo.FilterCriteria;
import com.ruoyi.colorfulfog.model.vo.BillResultVO;

import java.util.List;
import java.util.Map;

public interface RepoService {
    Map<String, Map<String, Double>> getData(DataSourceDTO dataSourceDTO);
}
