package com.ruoyi.colorfulfog.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by raodeming on 2021/8/6.
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource.hikari")
@Data
public class HikariPoolProperties extends HikariConfig {

    public HikariPool dataSource(String url, String username, String password, String driverClassName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName(driverClassName);

        hikariConfig.setConnectionTimeout(getConnectionTimeout());
        hikariConfig.setValidationTimeout(getValidationTimeout());
        hikariConfig.setIdleTimeout(getIdleTimeout());
        hikariConfig.setMaxLifetime(getMaxLifetime());
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(10);
        return new HikariPool(hikariConfig);
    }
}
