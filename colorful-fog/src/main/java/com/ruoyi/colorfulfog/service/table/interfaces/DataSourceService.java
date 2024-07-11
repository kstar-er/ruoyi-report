
package com.ruoyi.colorfulfog.service.table.interfaces;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.colorfulfog.modules.dataset.controller.dto.DataSetDto;
import com.ruoyi.colorfulfog.modules.datasource.controller.dto.DataSourceDto;
import com.ruoyi.colorfulfog.modules.datasource.controller.param.ConnectionParam;
import com.ruoyi.colorfulfog.modules.datasource.dao.entity.DataSource;

import java.util.List;
import java.util.Map;

/**
* @desc DataSource 数据集服务接口
* @author Raod
* @date 2021-03-18 12:09:57.728203200
**/
public interface DataSourceService extends IService<DataSource> {

    /**
     * 获取所有数据源
     * @return
     */
    List<DataSource> queryAllDataSource();
    List<DataSource> listAllDataSource();
    /**
     * 测试 连接
     * @param connectionParam
     * @return
     */
    Boolean testConnection(ConnectionParam connectionParam);

    /**
     * 执行sql
     * @param dto
     * @return
     */
    List<Map<String,Object>> execute(DataSourceDto dto);

    /**
     * 还要拿dataBase
     * @param sql
     * @return
     */
    List<Map<String,Object>> execute(String sql,Integer dataSourceId);

    /**
     * 执行sql,统计数据total
     * @param dataSourceDto
     * @param dto
     * @return
     */
    long total(DataSourceDto dataSourceDto, DataSetDto dto);
}
