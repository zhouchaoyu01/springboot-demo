package com.dataSwitch.utils;

/**
 * Created by sunlei on 2020/11/18.
 */
public class DataSwitchConstants {

    /**
     * 批量消息发送使用32个队列
     */
    public static final int QUEUE_SIZE = 32;
    /**
     * 单笔消息发送使用16个队列
     */
    public static final int SINGLE_QUEUE_SIZE = 16;

    /**
     *
     */
    public static final int SINGLE_EXTRACT_QUEUE_SIZE = 16;


    public static final String DIRECT_EXCHANGE_DATASWITCH = "dataSwitch_exchange";

    public static final String DIRECT_QUEUE_DATASWITCH = "dataSwitch_queue_";

    public static final String DIRECT_EXCHANGE_QUEUE_BINDING = "dataSwitch_queue_binding_";

    public static final String DIRECT_EXCHANGE_DATASWITCH_EXTRACTION = "dataSwitch_exchange_extract_";

    public static final String DIRECT_QUEUE_DATASWITCH_EXTRACTION = "dataSwitch_queue_extract_";

    public static final String DIRECT_EXCHANGE_QUEUE_BINDING_EXTRACTING = "dataSwitch_queue_binding_extracting_";

    public static final String SINGLE_DIRECT_EXCHANGE_DATASWITCH = "dataSwitch_single_exchange";

    public static final String SINGLE_DIRECT_QUEUE_DATASWITCH = "dataSwitch_single_queue_";

    public static final String SINGLE_DIRECT_EXCHANGE_QUEUE_BINDING = "dataSwitch_single_queue_binding_";

    /**
     * 对于入库慢的消息进补偿队列处理
     */
    public static final String DIRECT_EXCHANGE_DATASWITCH_COMPENSATE = "dataSwitch_compensate_exchange";

    public static final String DIRECT_QUEUE_DATASWITCH_COMPENSATE = "dataSwitch_queue_compensate";

    public static final String DIRECT_EXCHANGE_QUEUE_BINDING_COMPENSATE= "dataSwitch_queue_binding_compensate";
    /**
     * 状态 0：否
     */
    public static final String STATUS_0 = "0";
    /**
     * 状态 1：是
     */
    public static final String STATUS_1 = "1";
    /**
     * 追补任务类型 0：断点续传
     */
    public static final String TASK_TYPE_0 = "0";
    /**
     * 追补任务类型 1：数据比对
     */
    public static final String TASK_TYPE_1 = "1";
    /**
     * 追补任务状态 0：未执行
     */
    public static final String COMPARE_STATUS_0 = "0";
    /**
     * 追补任务状态 1：执行中
     */
    public static final String COMPARE_STATUS_1 = "1";
    /**
     * 追补任务状态 2：完成
     */
    public static final String COMPARE_STATUS_2 = "2";

    public static final String START_TIME_REDIS_KEY="DATASWITCH_";

    /**
     * 数据转移子任务类型： 0 源库子表
     */
    public static final String SOURCE_TYPE_0 = "0";
    /**
     * 数据转移子任务类型： 1 目标库子表
     */
    public static final String SOURCE_TYPE_1 = "1";
    /**
     * 是否批量发送mq消息：0 单笔发送
     */
    public static final String BATCH_FLAG_0 = "0";
    /**
     * 是否批量发送mq消息：1 批量发送
     */
    public static final String BATCH_FLAG_1 = "1";

    /**
     * 数据分发模式：0 :采集
     */
    public static final String PRCOCESSING_TYPE_0="0";

    /**
     * 数据分发模式：1 :消费
     */
    public static final String PRCOCESSING_TYPE_1="1";

    /**
     * 数据库驱动:db2
     */
    public static final String DB_DRIVER_DB2 = "com.ibm.db2.jcc.DB2Driver";
    /**
     * 数据库驱动:mysql
     */
    public static final String DB_DRIVER_MYSQL = "com.mysql.cj.jdbc.Driver";

    public static final String CONNECTOR = "_";

    public static String getDataSwitchControlKey(String key,String subKey)
    {
        return key+CONNECTOR+subKey;
    }
    /**
     * 数据库驱动:oracle
     */
    public static final String DB_DRIVER_ORACLE = "oracle.jdbc.driver.OracleDriver";
}
