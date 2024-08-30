package com.dataSwitch.service;

import com.dataSwitch.base.bean.DataSwitchControl;
import com.dataSwitch.base.bean.DataSwitchSubControl;

import java.sql.Connection;

/**
 * Created by sunlei on 2020/12/16.
 */
public interface ProducerService {

    int sendRecord(Connection conn, String timeStart, String timeEnd, DataSwitchControl dataSwitchControl, DataSwitchSubControl
            dataSwitchSubControl, String driver) throws Exception;
}
