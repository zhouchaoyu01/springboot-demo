package com.dataSwitch.producer;


import com.dataSwitch.base.bean.DataSwitchControlExt;
import com.dataSwitch.base.bean.DataSwitchSubControl;
import com.dataSwitch.base.bean.DatabaseConfig;
import com.dataSwitch.base.bean.ExceptionPointRecord;
import com.dataSwitch.base.common.DataSwitchException;
import com.dataSwitch.service.DbDirectService;
import com.dataSwitch.service.IDataSwitchConfigService;
import com.dataSwitch.service.ProducerService;
import com.dataSwitch.config.WhileConfig;
import com.dataSwitch.config.ds.DbContextHolder;
import com.dataSwitch.utils.DataSwitchConstants;
import com.dataSwitch.utils.DataUtil;
import com.dataSwitch.utils.RedisUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import static com.dataSwitch.utils.DataUtil.calculateNextTime;

/**
 * Created by sunlei on 2021/1/14.
 */
public class ProducerThread implements Runnable {
    private static Log logger = LogFactory.getLog(ProducerThread.class);

    private IDataSwitchConfigService dataSwitchConfigService;
    private ThreadPoolTaskExecutor exceptionThreadPool;
    private ThreadPoolTaskExecutor timeReloadThreadPool;
    private DbDirectService dbDirectService;
    private ProducerService producerService;
    private RedisUtils redisUtils;

    private DataSwitchControlExt dsc;
    /**
     * 线程停止标识
     */
    private volatile boolean stopFlag = true;

    public ProducerThread() {
    }

    ;

    public ProducerThread(DataSwitchControlExt dataSwitchControlExt, IDataSwitchConfigService dataSwitchConfigService, ThreadPoolTaskExecutor exceptionThreadPool,
                          ThreadPoolTaskExecutor timeReloadThreadPool, DbDirectService dbDirectService, ProducerService producerService, RedisUtils redisUtils) {
        this.dsc = dataSwitchControlExt;
        this.dataSwitchConfigService = dataSwitchConfigService;
        this.exceptionThreadPool = exceptionThreadPool;
        this.timeReloadThreadPool = timeReloadThreadPool;
        this.dbDirectService = dbDirectService;
        this.producerService = producerService;
        this.redisUtils = redisUtils;
    }

    @Override
    public void run() {
        logger.info("start...");
        List<DataSwitchSubControl> dataSwitchSubControlList = dataSwitchConfigService.getDataSwitchSubControlByMainIdAndPriority(dsc.getRowId(), null, dsc.getPriorityLevel());
        if (CollectionUtils.isNotEmpty(dataSwitchSubControlList)) {
            if (dataSwitchSubControlList.size() == 1) {
                singleDeal(dataSwitchSubControlList.get(0));
            } else {
                complexDeal(dataSwitchSubControlList);
            }
        }
        logger.info("end.");
    }


    /**
     * 子任务有多条时，调用此方法
     *
     * @param dataSwitchSubControlList
     */
    public void complexDeal(List<DataSwitchSubControl> dataSwitchSubControlList) {
        try {
            //循环从数据库中查询步进内的数据 出现发送消息异常，记录异常时间点，并发送告警短信，继续往下执行
            while (WhileConfig.getInstance().isWhileFlag() && stopFlag) {
                for (DataSwitchSubControl dataSwitchSubControl : dataSwitchSubControlList) {
                    String startStr = dataSwitchSubControl.getStartTime();
                    Connection conn = null;
                    //String timestart = "";
                    try {
                        int totalCount = 0;
                        DatabaseConfig databaseConfig = dataSwitchConfigService.getDatabaseConfigById(dataSwitchSubControl.getDsId());
                        // 获取数据库连接 根据更新时间查询记录
                        DbContextHolder.setDbType(databaseConfig.getDsName());
                        conn = dbDirectService.getDBConnection(databaseConfig);
                        //配置时间可能错误，修正
                        if (StringUtils.isBlank(startStr)) {
                            throw new DataSwitchException("", "开始同步的时间配置错误,退出.startStr:" + startStr);
                        }
                        if (startStr.length() < 23) {
                            startStr = DataUtil.fixTimeString(startStr);
                        }
                        //timestart = startStr;

                        // 获取数据库当前时间
//                        String nowDBDateString = dbDirectService.getDBCurrentDateTime(conn, databaseConfig.getDbDriver());
                        String nowDBDateString = "2024-08-29 16:31:00.000";//TODO
                        //注意这里时间格式SSS精确到毫秒，如果数据库是精确到微秒 则需要对时间字符串进行截取，忽略掉微秒部分，否则导致时间比较出错.需要留意忽略掉微秒部分在数据库中的比较是否符合业务逻辑
                        logger.info("记录开始时间: " + startStr + "数据库当前时间: " + nowDBDateString);

                        //获取查询结束时间
                        String nextStr = calculateNextTime(startStr, nowDBDateString, dsc);
                        //发送mq
                        int thisCount = producerService.sendRecord(conn, startStr, nextStr, dsc, dataSwitchSubControl, databaseConfig.getDbDriver());
                        totalCount = totalCount + thisCount;
                        dataSwitchSubControl.setStartTime(nextStr);//记录时间点，作为下次的起始时间点
                        //timestart = nextStr;
                        logger.info("处理记录总数: " + totalCount);
                        //将时间塞入redis中
                        final String time = startStr;
                        timeReloadThreadPool.execute(() -> {
                            //时间点记在了sub任务表上，key值用主表主键+"_"+sub表主键
                            redisUtils.hmSet(DataSwitchConstants.START_TIME_REDIS_KEY, DataSwitchConstants.getDataSwitchControlKey(dataSwitchSubControl.getMainId() + "", dataSwitchSubControl.getRowId() + ""), time);
                        });
                    } catch (Exception e) {
                        logger.info("Producer occurs error.", e);

                        final String excptionTime = startStr;
                        //1.记录时间断点; 2.发送告警邮件
                        exceptionThreadPool.execute(() -> {
                                    DbContextHolder.clearDbType();
                                    //记录异常时间点
                                    ExceptionPointRecord exceptionPointRecord = new ExceptionPointRecord();
                                    exceptionPointRecord.setDscId(dsc.getRowId());
                                    exceptionPointRecord.setExceptionTime(excptionTime);
                                    exceptionPointRecord.setExceptionCause(e.getMessage().length() > 1024 ? e.getMessage().substring(0, 1024) : e.getMessage());
                                    exceptionPointRecord.setStatus("0");
                                    exceptionPointRecord.setCreateTime(new Date());
                                    exceptionPointRecord.setLastModifyTime(new Date());
                                    dataSwitchConfigService.saveRecord(exceptionPointRecord);
                                    //发送告警
                                    String msg = "Producer occurs error,current datetime: " + (new Date()) + ",excptionTime: " + excptionTime + ", error detail: " + e.getMessage();
                                    //mailMessageSender.sendMailMessage(msg,"生产者异常告警");
                                }
                        );

                    } finally {
                        //清空数据源
                        dbDirectService.closeConnection(conn, null, null);
                        DbContextHolder.clearDbType();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Producer occurs error.", e);
            // 发邮件告警
            String msg = "Producer occurs error,current datetime: " + (new Date()) + ", error detail: " + e.getMessage();
            //mailMessageSender.sendMailMessage(msg,"生产者异常告警");
        } finally {
            for (DataSwitchSubControl dataSwitchSubControl : dataSwitchSubControlList) {
                dataSwitchConfigService.updateSubControlStartTime(dataSwitchSubControl);
            }
            logger.info("主进程结束%n");
        }
    }

    /**
     * 子任务只有1条时，调用此方法，1条时效率更高
     *
     * @param dataSwitchSubControl
     */
    public void singleDeal(DataSwitchSubControl dataSwitchSubControl) {
        String startStr = dataSwitchSubControl.getStartTime();
        Connection conn = null;
        String timestart = "";
        try {
            int totalCount = 0;
            DatabaseConfig databaseConfig = dataSwitchConfigService.getDatabaseConfigById(dataSwitchSubControl.getDsId());
            // 获取数据库连接 根据更新时间查询记录
            DbContextHolder.setDbType(databaseConfig.getDsName());
            conn = dbDirectService.getDBConnection(databaseConfig);
            //配置时间可能错误，修正
            if (StringUtils.isBlank(startStr)) {
                throw new DataSwitchException("", "开始同步的时间配置错误,退出.startStr:" + startStr);
            }
            if (startStr.length() < 23) {
                startStr = DataUtil.fixTimeString(startStr);
            }
            timestart = startStr;

            //循环从数据库中查询步进内的数据 出现发送消息异常，记录异常时间点，并发送告警短信，继续往下执行
            while (WhileConfig.getInstance().isWhileFlag() && stopFlag) {
                try {
                    // 获取数据库当前时间
//                    String nowDBDateString = dbDirectService.getDBCurrentDateTime(conn, databaseConfig.getDbDriver());
                    String nowDBDateString = "2024-08-29 16:33:00.000";//TODO
                    //注意这里时间格式SSS精确到毫秒，如果数据库是精确到微秒 则需要对时间字符串进行截取，忽略掉微秒部分，否则导致时间比较出错.需要留意忽略掉微秒部分在数据库中的比较是否符合业务逻辑
                    logger.info("记录开始时间: " + timestart + "数据库当前时间: " + nowDBDateString);

                    //获取查询结束时间
                    String nextStr = calculateNextTime(timestart, nowDBDateString, dsc);

                    //发送mq
                    int thisCount = producerService.sendRecord(conn, timestart, nextStr, dsc, dataSwitchSubControl, databaseConfig.getDbDriver());
                    totalCount = totalCount + thisCount;
                    timestart = nextStr;
                    dataSwitchSubControl.setStartTime(nextStr);//记录时间点
                    logger.info("处理记录总数: " + totalCount);
                    //将时间塞入redis中
                    final String time = timestart;
                    timeReloadThreadPool.execute(() -> {
                        redisUtils.hmSet(DataSwitchConstants.START_TIME_REDIS_KEY, DataSwitchConstants.getDataSwitchControlKey(dataSwitchSubControl.getMainId() + "", dataSwitchSubControl.getRowId() + ""), time);
                    });
                } catch (Exception e) {
                    logger.info("Producer occurs error.", e);
                    final String excptionTime = timestart;
                    //1.记录时间断点; 2.发送告警邮件
                    exceptionThreadPool.execute(() -> {
                        DbContextHolder.clearDbType();
                        //记录异常时间点
                        ExceptionPointRecord exceptionPointRecord = new ExceptionPointRecord();
                        exceptionPointRecord.setDscId(dsc.getRowId());
                        exceptionPointRecord.setExceptionTime(excptionTime);
                        exceptionPointRecord.setExceptionCause(e.getMessage().length() > 1024 ? e.getMessage().substring(0, 1024) : e.getMessage());
                        exceptionPointRecord.setStatus("0");
                        exceptionPointRecord.setCreateTime(new Date());
                        exceptionPointRecord.setLastModifyTime(new Date());
                        dataSwitchConfigService.saveRecord(exceptionPointRecord);
                        //发送告警
                        String msg = "Producer occurs error,current datetime: " + (new Date()) + ",excptionTime: " + excptionTime + ", error detail: " + e.getMessage();
                        //mailMessageSender.sendMailMessage(msg,"生产者异常告警");
                    });


                }
            }
        } catch (Exception e) {
            logger.error("Producer occurs error.", e);
            // 发邮件告警
            String msg = "Producer occurs error,current datetime: " + (new Date()) + ", error detail: " + e.getMessage();
            //mailMessageSender.sendMailMessage(msg,"生产者异常告警");
        } finally {
            dbDirectService.closeConnection(conn, null, null);
            DbContextHolder.clearDbType();
            if (StringUtils.isNotBlank(timestart)) {
                dataSwitchConfigService.updateSubControlStartTime(dataSwitchSubControl);
            }
            logger.info("主进程结束%n");
        }
    }

    public void setStopFlag() {
        this.stopFlag = false;
    }
}
