package com.dataSwitch.producer;


import com.dataSwitch.base.bean.DataSwitchControlExt;
import com.dataSwitch.service.DbDirectService;
import com.dataSwitch.service.IDataSwitchConfigService;
import com.dataSwitch.service.ProducerService;
import com.dataSwitch.utils.RedisUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunlei on 2020/11/10.
 */
@Component
public class Producer {
    private static Log logger = LogFactory.getLog(Producer.class);
    @Autowired
    private IDataSwitchConfigService dataSwitchConfigService;
    @Autowired
    @Qualifier("producerThreadPool")
    private ThreadPoolTaskExecutor dataThreadPool;
    @Autowired
    @Qualifier("exceptionThreadPool")
    private ThreadPoolTaskExecutor exceptionThreadPool;
    @Autowired
    @Qualifier("timeReloadThreadPool")
    private ThreadPoolTaskExecutor timeReloadThreadPool;

    @Autowired
    private DbDirectService dbDirectService;
    @Autowired
    private ProducerService producerService;

    public static Map<String,ProducerThread> taskMap = new HashMap<>();


    @Autowired
    private RedisUtils redisUtils;


    public void start()
    {
        logger.info("Producer start...");
        try {
            // 只是为了获取到缓存用于后面生成任务
            List<DataSwitchControlExt> dataSwitchControlExtList = dataSwitchConfigService.getAllDataSwitchControlByPriority();

            for (DataSwitchControlExt dataSwitchControlExt:dataSwitchControlExtList) {
                createTask(dataSwitchControlExt);
            }
        }
        catch (Exception e)
        {
            logger.error("Producer occurs error.",e);
        }
    }

    public void createTask(DataSwitchControlExt dataSwitchControlExt)
    {
        ProducerThread producerThread = new ProducerThread(dataSwitchControlExt,dataSwitchConfigService,
                exceptionThreadPool,timeReloadThreadPool,dbDirectService,producerService,redisUtils);
        //线程key为:主键+"_"+priority
//        taskMap.put(DataSwitchConstants.getDataSwitchControlKey(dataSwitchControlExt.getRowId()+"",dataSwitchControlExt.getPriorityLevel()),producerThread);
        dataThreadPool.execute(producerThread);
    }

}
