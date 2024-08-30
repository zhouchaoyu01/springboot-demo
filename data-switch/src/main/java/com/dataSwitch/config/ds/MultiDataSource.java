package com.dataSwitch.config.ds;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunlei on 2020/11/16.
 */
@Configuration
public class MultiDataSource {

    public static final String BASE_DATA_SOURCE = "baseDataSource";

    @Bean(name = BASE_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.basedatasource")
    public DruidDataSource masterDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    /*@Bean(name = MultiDataSource.SEC_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.datasource02")
    public DruidDataSource secDataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }*/

    @Primary
    @DependsOn(BASE_DATA_SOURCE)
    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        dynamicDataSource.setDefaultTargetDataSource(masterDataSource());
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(BASE_DATA_SOURCE, masterDataSource());
        //dataSourceMap.put(SEC_DATA_SOURCE, secDataSource());
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        return dynamicDataSource;
    }
}
