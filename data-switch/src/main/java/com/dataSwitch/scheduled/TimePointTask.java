package com.dataSwitch.scheduled;


import com.dataSwitch.base.bean.DataSwitchControl;
import com.dataSwitch.base.bean.DataSwitchSubControl;
import com.dataSwitch.service.IDataSwitchConfigService;
import com.dataSwitch.utils.DataSwitchConstants;
import com.dataSwitch.utils.RedisUtils;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据转移执行时间切点回写任务
 *
 * Created by sunlei on 2020/12/16.
 */
@Component
public class TimePointTask {
    private static Log logger = LogFactory.getLog(TimePointTask.class);
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private IDataSwitchConfigService dataSwitchConfigService;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void doTask()
    {
        logger.info("TimePointTask doTask start.");
        List<Object> field = new ArrayList<>();
        List<DataSwitchControl> dataSwitchControls = dataSwitchConfigService.getAllDataSwitchControl();
        for(DataSwitchControl dataSwitchControl:dataSwitchControls)
        {
            List<DataSwitchSubControl> dataSwitchSubControlList = dataSwitchConfigService.getDataSwitchSubControlByMainId(dataSwitchControl.getRowId());
            for(DataSwitchSubControl dataSwitchSubControl:dataSwitchSubControlList)
            {
                String key = DataSwitchConstants.getDataSwitchControlKey(dataSwitchControl.getRowId()+"",dataSwitchSubControl.getRowId()+"");
                field.add(key);
            }
        }
        List<Object> resList = redisUtils.hmGet(DataSwitchConstants.START_TIME_REDIS_KEY,field);
        for(int i=0;i<field.size();i++)
        {
            if(null!=resList.get(i))
            {
                String keys = field.get(i)+"";
                String subKey = keys.split(DataSwitchConstants.CONNECTOR)[1];
                DataSwitchSubControl dataSwitchSubControl = new DataSwitchSubControl();
                dataSwitchSubControl.setRowId(Long.valueOf(subKey));
                dataSwitchSubControl.setStartTime(resList.get(i)+"");
                dataSwitchConfigService.updateSubControlStartTime(dataSwitchSubControl);
            }
        }
        logger.info("TimePointTask doTask end.");
    }
}
