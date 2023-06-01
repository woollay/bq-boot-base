package com.biuqu.boot.configure;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据库连接池配置
 *
 * @author BiuQu
 * @date 2023/1/27 22:57
 */
@Configuration
public class DataSourceConfigurer
{
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource()
    {
        DataSourceBuilder<DruidDataSource> builder = DataSourceBuilder.create().type(DruidDataSource.class);
        //TODO 可以在此处实现数据库密码托管逻辑
        return builder.build();
    }
}
