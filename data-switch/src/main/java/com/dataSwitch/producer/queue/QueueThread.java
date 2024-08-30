package com.dataSwitch.producer.queue;


import com.dataSwitch.base.common.MessageModel;
import com.dataSwitch.producer.batch.ProducerBatchExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Created by sunlei on 2020/12/2.
 */
public class QueueThread extends Thread {
    private static Log logger = LogFactory.getLog(QueueThread.class);
    private BlockingQueue<MessageModel> queue;
    private String key; //这个key是阻塞队列的序号
    private ProducerBatchExecutor producerBatchExecutor;

    public QueueThread(){};

    public QueueThread(BlockingQueue<MessageModel> queue, String key, ProducerBatchExecutor producerBatchExecutor)
    {
        this.queue = queue;
        this.key = key;
        this.producerBatchExecutor = producerBatchExecutor;
    }

    @Override
    public void run() {
        try {
            while(true)
            {
                long start = System.currentTimeMillis();
                if(queue.size()>900)
                {
                    logger.warn("current thread:"+Thread.currentThread()+",queue size:"+queue.size()+",key is:"+key);
                }
                MessageModel obj = queue.take();
                long before = System.currentTimeMillis();
                logger.debug("take cost:"+(before-start));
                //TODO 如果mq内存满了，此处线程会被占用，queue队列会阻塞
                producerBatchExecutor.addMsg(obj);
                long end = System.currentTimeMillis();
                logger.debug("add msg cost:"+(end-before));
            }
        }catch (Exception e)
        {
            logger.error("QueueThread run occurs error.",e);
        }

    }

    public void batchSend()
    {

    }


    public BlockingQueue getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue queue) {
        this.queue = queue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
