package com.dataSwitch.config.ds;

import com.alibaba.druid.pool.DruidDataSource;
import com.dataSwitch.base.bean.DatabaseConfig;
import com.dataSwitch.base.dao.DatabaseConfigMapper;
import com.dataSwitch.utils.DataSwitchConstants;
import com.dataSwitch.utils.DataUtil;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2020/11/16.
 */
@Component
public class DataSourceService {
    private static Log logger = LogFactory.getLog(DataSourceService.class);
    //private static final String DS_DRIVER_CLASS_NAME = "com.ibm.db2.jcc.DB2Driver";
    private static final int INITIAL_SIZE = 2;
    private static final int MAX_ACTIVE_SIZE = 50; //最大连接数
    private static final int MIN_IDLE = 5;  //最小连接数
    private static final int MAX_WAIT = 5*1000;  //超时时间

    @Resource
    private DatabaseConfigMapper databaseConfigMapper;

    @Resource
    private DynamicDataSource dynamicDataSource;

    public static Map<Object, Object> dataSources = new HashMap<>();

    @PostConstruct
    public void initDataSource()
    {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setStatus("0");
        List<DatabaseConfig> databaseConfigList = databaseConfigMapper.selectDatabaseConfigList(databaseConfig);

        for (DatabaseConfig dsConfig : databaseConfigList)
        {
            DruidDataSource druidDataSource = new DruidDataSource();
            druidDataSource.setUsername(dsConfig.getUserName());
            druidDataSource.setPassword(dsConfig.getPassword());
            druidDataSource.setDriverClassName(dsConfig.getDbDriver());
            druidDataSource.setUrl(DataUtil.generateUrl(dsConfig.getDbDriver(),dsConfig.getDbHost(), dsConfig.getDbPort(), dsConfig.getDbName(), dsConfig.getDbSchema()));
            druidDataSource.setMaxActive(dsConfig.getMaxSize());//从数据库中获取最大连接数
            druidDataSource.setInitialSize(dsConfig.getMinSize());
            druidDataSource.setMinIdle(dsConfig.getMinSize());//从数据库中获取最小连接数
            druidDataSource.setTestOnBorrow(true);
            druidDataSource.setTestOnReturn(false);
            druidDataSource.setTestWhileIdle(true);
            druidDataSource.setMaxWait(MAX_WAIT);
            dataSources.put(dsConfig.getDsName(), druidDataSource);
        }
        dynamicDataSource.setTargetDataSources(dataSources);
        dynamicDataSource.afterPropertiesSet();
        logger.info("动态数据源多数据源初始化完成...");
    }

    /**
     * 运行时动态加载数据源
     * @param dsConfig
     */
    public void dynamicSetDataSource(DatabaseConfig dsConfig)
    {
        try {
            if(null!=dataSources.get(dsConfig.getDsName()))
            {
                //如果不为空，已存在配置，先注销原数据源
                DruidDataSource oriDataSource = (DruidDataSource)dataSources.get(dsConfig.getDsName());
                oriDataSource.close();
                dataSources.remove(dsConfig.getDsName());
            }
            if(DataSwitchConstants.STATUS_0.equals(dsConfig.getStatus()))
            {
                DruidDataSource druidDataSource = new DruidDataSource();
                druidDataSource.setUsername(dsConfig.getUserName());
                druidDataSource.setPassword(dsConfig.getPassword());
                druidDataSource.setDriverClassName(dsConfig.getDbDriver());
                druidDataSource.setUrl(DataUtil.generateUrl(dsConfig.getDbDriver(),dsConfig.getDbHost(), dsConfig.getDbPort(), dsConfig.getDbName(), dsConfig.getDbSchema()));
                druidDataSource.setMaxActive(dsConfig.getMaxSize());//从数据库中获取最大连接数
                druidDataSource.setInitialSize(dsConfig.getMinSize());
                druidDataSource.setMinIdle(dsConfig.getMinSize());//从数据库中获取最小连接数
                druidDataSource.setMaxWait(MAX_WAIT);
                dataSources.put(dsConfig.getDsName(), druidDataSource);

                dynamicDataSource.setTargetDataSources(dataSources);
                dynamicDataSource.afterPropertiesSet();
            }
            logger.info("动态数据源修改完成...");
        }catch (Exception e)
        {
            logger.info("dynamicSetDataSource occurs error.",e);
        }
    }

}
