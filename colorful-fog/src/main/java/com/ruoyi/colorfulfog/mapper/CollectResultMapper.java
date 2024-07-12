package com.ruoyi.colorfulfog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.CollectResult;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectResultMapper extends BaseMapper<CollectResult> {
    List<CollectResult> selectByCodeAndName(@Param("codesAndNames") List<ManualUpdateDto> codesAndNames);
}