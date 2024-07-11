package com.ruoyi.colorfulfog.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.colorfulfog.model.CwuTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CwuTableMapper extends BaseMapper<CwuTable> {
    Page<CwuTable> selectPageDbTableList(@Param("page") Page<CwuTable> page, @Param("cwuTable") CwuTable cwuTable);
}
