package com.dataSwitch.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.dataSwitch.base.bean.DataSwitchControl;
import com.dataSwitch.base.bean.DataSwitchSubControl;
import com.dataSwitch.base.common.MessageModel;
import com.dataSwitch.service.DbDirectService;
import com.dataSwitch.service.ProducerService;
import com.dataSwitch.producer.queue.QueueFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

/**
 * 生产者获取数据，发送mq
 *
 * Created by sunlei on 2020/12/16.
 */
@Service
public class ProducerServiceImpl implements ProducerService {
    private static Log logger = LogFactory.getLog(ProducerServiceImpl.class);
    @Autowired
    private DbDirectService dbDirectService;
    @Autowired
    private QueueFactory queueFactory;

    @Override
    public int sendRecord(Connection conn, String timeStart, String timeEnd, DataSwitchControl dataSwitchControl, DataSwitchSubControl
                          dataSwitchSubControl, String driver) throws Exception{
        PreparedStatement ps = null;
        ResultSet rs = null;
        int thisCount = 0;
        try {
            long dbStartTime = System.currentTimeMillis();
            ps = dbDirectService.getDBRecords(conn, timeStart, timeEnd,dataSwitchSubControl.getSrcTbName(),dataSwitchSubControl.getQueryColumn(),driver);
            rs = ps.executeQuery();
            long dbEndTime = System.currentTimeMillis();
            logger.debug("本次数据库耗时: "+(dbEndTime - dbStartTime));
            /*if (rs.first()) {*/
                // 数据库json数组数据转string填入消息体，并放入消息列表
                ResultSetMetaData rsm = rs.getMetaData();
                int columnCount = rsm.getColumnCount();
                //rs.beforeFirst();
                while (rs.next()) {
                    long t1 = System.currentTimeMillis();
                    JSONObject jsonObj = new JSONObject();
                    for (int i = 0; i < columnCount; i++) {
                        String colName = rsm.getColumnName(i + 1);
                        String colType = rsm.getColumnTypeName(i + 1);
                        String value = rs.getString(colName);
                        if (value == null) {
                            value = "";
                        }
                        jsonObj.put(colName + "|" + colType, value);
                    }
                    //long t2 = System.currentTimeMillis();
                    //logger.info("set map cost:"+(t2-t1));
                    //DATA_SWITCH_CONFIG表ROW_ID+主键
                    MessageModel model = new MessageModel();
//                    model.setProcessingType(dataSwitchControl.getProcessingType());
                    model.setStartTime(timeStart);
                    model.setEndTime(timeEnd);
                    model.setSrcTbName(dataSwitchSubControl.getSrcTbName());
                    model.setDistTbName(dataSwitchSubControl.getDistTbName());
                    model.setJsonObject(jsonObj);
                    model.setId(String.valueOf(dataSwitchControl.getRowId()));
                    model.setOrderId(rs.getString(dataSwitchSubControl.getRowColumn()));
                    long t4 = System.currentTimeMillis();
                    //logger.info("组对象："+(t4-t1));
                    //发送消息到队列
                    queueFactory.addQueue(model,dataSwitchControl.getBatchFlag());
                    thisCount += 1;
                }
                long sendEndTime = System.currentTimeMillis();
                logger.debug("本次转移任务主键:"+dataSwitchControl.getRowId()+",转移表名:"+dataSwitchSubControl.getSrcTbName()+",发送mq耗时: "+(sendEndTime - dbEndTime)+",记录数: " +thisCount);
            //}
            return thisCount;
        }catch (Exception e)
        {
            throw new Exception(e);
        }finally {
            dbDirectService.closeConnection(null,ps,rs);
        }
    }

}
