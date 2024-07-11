package com.ruoyi.colorfulfog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.colorfulfog.model.BillResult;
import com.ruoyi.colorfulfog.model.dto.ManualUpdateDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillResultMapper extends BaseMapper<BillResult> {
    List<BillResult> selectByCodeAndName(@Param("codesAndNames") List<ManualUpdateDto> codesAndNames);
}