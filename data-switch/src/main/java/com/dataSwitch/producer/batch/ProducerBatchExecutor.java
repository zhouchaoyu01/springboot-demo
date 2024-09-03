package com.dataSwitch.producer.batch;


import com.dataSwitch.base.common.MessageModel;
import com.dataSwitch.service.IQueueProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者批量组包线程
 * <p>
 * Created by sunlei on 2020/12/30.
 */
public class ProducerBatchExecutor extends Thread {
    private static Log logger = LogFactory.getLog(ProducerBatchExecutor.class);
    public static final int MAX_SIZE = 10;   //集合最大等待大小
    public static final long MAX_WAIT = 1;  //集合最大等待时间(ms)
    private long excTime = 0;
    private IQueueProcessor processor;
    private String key;
    private ReentrantLock lock;

    private List<MessageModel> msgList;

    public ProducerBatchExecutor() {
    }

    ;

    public ProducerBatchExecutor(IQueueProcessor processor, String key) {
        this.processor = processor;
        this.key = key;
        msgList = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (true) {
            try {
                addMsg(null);
                Thread.sleep(MAX_WAIT);
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public synchronized void addMsg(MessageModel msg) {
        try {
//            long currentTime = System.currentTimeMillis();
//            long offset = currentTime - excTime;
            if (null != msg) {
                msgList.add(msg);
            }
//            if(msgList.size() > MAX_SIZE || (msgList.size()>0 && offset >= MAX_WAIT ))
            if (msgList.size() > MAX_SIZE) {
                logger.info("batch addMsg size:" + msgList.size());
                processor.process(msgList, key);
                msgList.clear();
//                excTime = currentTime;
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            if(msgList!=null && msgList.size() > 0){
                logger.info("last size:" + msgList.size());
                processor.process(msgList, key);
                msgList.clear();
            }
        }
    }

    //lock貌似没有synchronized性能高
    /*public  void addMsg(Message msg)
    {
        try {
            lock.lock();
            long currentTime = System.currentTimeMillis();
            long offset = currentTime - excTime;
            if(null!= msg)
            {
                msgList.add(msg);
            }
            if(msgList.size() > MAX_SIZE || (msgList.size()>0 && offset >= MAX_WAIT ))
            {
                logger.info("batch addMsg size:"+msgList.size());
                processor.process(msgList,key);
                msgList.clear();
                excTime = currentTime;
            }
        }catch (Exception e)
        {
            logger.error(e);
        }
        finally {
            lock.unlock();
        }
    }*/

}
