package com.ruoyi.colorfulfog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;
import com.ruoyi.common.annotation.DataColumn;
import com.ruoyi.common.annotation.DataPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* DataSource Mapper
* @author Raod
* @date 2021-03-18 12:09:57.728203200
**/
@Mapper
public interface DataSourceMapper extends BaseMapper<DataSource> {

    @DataPermission({
            @DataColumn(key = "userName",value = "create_user")
    })
    List<DataSource> selectAll();

}
