package com.ruoyi.colorfulfog.modules.datasource.service.impl;


import com.ruoyi.colorfulfog.config.HikariPoolProperties;
import com.ruoyi.colorfulfog.modules.datasource.controller.dto.DataSourceDto;
import com.ruoyi.colorfulfog.modules.datasource.service.JdbcService;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by raodeming on 2021/8/6.
 */
@Service
@Slf4j
public class JdbcServiceImpl implements JdbcService {

    @Autowired
    private HikariPoolProperties hikariPoolProperties;

    /**
     * 所有数据源的连接池存在map里
     */
    private Map<Integer, HikariPool> map = new ConcurrentHashMap<>();
    private Object lock = new Object();

    public HikariPool getJdbcConnectionPool(DataSourceDto dataSource) {
        if (map.containsKey(dataSource.getId())) {
            return map.get(dataSource.getId());
        } else {
            try {
                synchronized (lock) {
                    if (!map.containsKey(dataSource.getId())) {
                        HikariPool pool = hikariPoolProperties.dataSource(dataSource.getJdbcUrl(),
                                dataSource.getUsername(), dataSource.getPassword(), dataSource.getDriverName());
                        map.put(dataSource.getId(), pool);
                        log.info("创建连接池成功：{}", dataSource.getJdbcUrl());
                    }
                }
                return map.get(dataSource.getId());
            } finally {
            }
        }
    }
    /**
     * 删除数据库连接池
     *
     * @param id
     */
    @Override
    public void removeJdbcConnectionPool(Integer id) {
        try {
            HikariPool pool = map.get(id);
            if (pool != null) {
                log.info("remove pool success, datasourceId:{}", id);
                map.remove(id);
            }
        } catch (Exception e) {
            log.error("error", e);
        } finally {
        }
    }

    /**
     * 获取连接
     *
     * @param dataSource
     * @return
     * @throws SQLException
     */
    @Override
    public Connection getPooledConnection(DataSourceDto dataSource) throws SQLException{
        HikariPool pool = getJdbcConnectionPool(dataSource);
        return pool.getConnection();
    }
    /**
     * 测试数据库连接  获取一个连接
     *
     * @param dataSource
     * @return
     * @throws ClassNotFoundException driverName不正确
     * @throws SQLException
     */
    @Override
    public Connection getUnPooledConnection(DataSourceDto dataSource) throws SQLException {
        HikariPool druidDataSource = hikariPoolProperties.dataSource(dataSource.getJdbcUrl(),
                dataSource.getUsername(), dataSource.getPassword(), dataSource.getDriverName());
        return druidDataSource.getConnection();
    }

}
