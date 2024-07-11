package com.ruoyi.colorfulfog.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.colorfulfog.model.CwuTableColumn;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@InterceptorIgnore(dataPermission = "true")
@Mapper
public interface CwuTableColumnMapper extends BaseMapper<CwuTableColumn> {
    /**
     * 根据表名称查询列信息
     *
     * @param tableName 表名称
     * @return 列信息
     */
    List<CwuTableColumn> selectDbTableColumnsByName(String tableName);
}
