package com.dataSwitch.producer.queue;


import com.dataSwitch.base.common.MessageModel;
import com.dataSwitch.service.IQueueProcessor;
import com.dataSwitch.producer.batch.ProducerBatchExecutor;
import com.dataSwitch.utils.DataSwitchConstants;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by sunlei on 2020/12/2.
 */
@Component
public class QueueFactory {
    private static Log logger = LogFactory.getLog(QueueFactory.class);
    public static final int QUEUE_LENGTH = 1024;

    /**
     * 批量消息阻塞队列
     */
    private static Map<String,BlockingQueue<MessageModel>> batachQueueMap = new HashMap<>();

    /**
     * 单笔消息阻塞队列
     */
    private static Map<String,BlockingQueue<MessageModel>> singleQueueMap = new HashMap<>();

    @Resource
    private IQueueProcessor queueProcessor;

    @Autowired
    @Qualifier("producerThreadPool")
    private ThreadPoolTaskExecutor producerThreadPool;

    @PostConstruct
    public void init()
    {
        for(int i = 0; i< DataSwitchConstants.QUEUE_SIZE; i++)
        {
            BlockingQueue<MessageModel> queue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
            ProducerBatchExecutor producerBatchExecutor = new ProducerBatchExecutor(queueProcessor,i+"");
            producerBatchExecutor.start();
            QueueThread queueThread = new QueueThread(queue,i+"",producerBatchExecutor);
            queueThread.start();
            batachQueueMap.put(i+"",queue);
        }

        for(int i = 0; i< DataSwitchConstants.SINGLE_QUEUE_SIZE; i++)
        {
            BlockingQueue<MessageModel> queue = new ArrayBlockingQueue<>(QUEUE_LENGTH);
            SingleQueueThread singleQueueThread = new SingleQueueThread(queue,i+"",queueProcessor);
            singleQueueThread.start();
            singleQueueMap.put(i+"",queue);
        }
    }

    public void addQueue(MessageModel model,String batchFlag)
    {
        try {
            long t1 = System.currentTimeMillis();
            String id = model.getOrderId();
            if(DataSwitchConstants.BATCH_FLAG_1.equals(batchFlag))
            {
                //long key = Math.abs(id.hashCode()% DataSwitchConstants.QUEUE_SIZE);
                long key = Math.abs(Long.valueOf(id) % DataSwitchConstants.QUEUE_SIZE);
                BlockingQueue<MessageModel> queue = batachQueueMap.get(key+"");
                if(null!= queue)
                {
                    long t2 = System.currentTimeMillis();
                    logger.debug("before add queue size:"+queue.size()+",Key is:"+key+",cost:"+(t2-t1));
                    queue.put(model);
                    long t3 = System.currentTimeMillis();
                    logger.debug("before add queue size:"+queue.size()+",cost:"+(t3-t2));
                    //此处用线程池，只是用来塞mq队列，用来增加生产端效率，理论上此处不会有顺序问题，如果不用线程池，只要有一个队列阻塞，就会导致主进程停止
                }
            }
            else
            {
                //long key = Math.abs(id.hashCode()% DataSwitchConstants.SINGLE_QUEUE_SIZE);
                long key = Math.abs(Long.valueOf(id) % DataSwitchConstants.SINGLE_QUEUE_SIZE);
                BlockingQueue<MessageModel> queue = singleQueueMap.get(key+"");
                if(null!= queue)
                {
                    long t2 = System.currentTimeMillis();
                    logger.debug("before add queue size:"+queue.size()+",Key is:"+key+",cost:"+(t2-t1));
                    queue.put(model);
                    long t3 = System.currentTimeMillis();
                    logger.debug("before add queue size:"+queue.size()+",cost:"+(t3-t2));
                    //此处用线程池，只是用来塞mq队列，用来增加生产端效率，理论上此处不会有顺序问题，如果不用线程池，只要有一个队列阻塞，就会导致主进程停止
                }
            }

        }catch (Exception e)
        {
            logger.error("QueueFactory addQueue occurs error.",e);
        }
    }

}
