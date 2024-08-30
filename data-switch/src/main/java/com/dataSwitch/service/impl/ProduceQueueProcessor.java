package com.dataSwitch.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.dataSwitch.base.common.MessageModel;
import com.dataSwitch.service.IQueueProcessor;
import com.dataSwitch.utils.DataSwitchConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by sunlei on 2020/12/9.
 */
@Service
public class ProduceQueueProcessor implements IQueueProcessor {
    private static Log logger = LogFactory.getLog(ProduceQueueProcessor.class);

//    @Autowired
//    private RabbitMqService rabbitMqService;

    /**
     * 批量消息发送 TODO 阻塞 调用API送给数经平台
     *
     * @param message
     * @param key
     */
    @Override
    public void process(Object message, String key) {
        try {
            if (null != message) {
//                MessageProperties properties = new MessageProperties();
//                properties.setContentType("text/plain");
//                //properties.setMessageId(msgId);
//                MessageModel obj=(MessageModel)message;
//                String processingType=obj.getProcessingType();
//                String exchange= DataSwitchConstants.SINGLE_DIRECT_EXCHANGE_DATASWITCH;
//                String routeKey=DataSwitchConstants.SINGLE_DIRECT_EXCHANGE_QUEUE_BINDING+key;
//
//                if(DataSwitchConstants.PRCOCESSING_TYPE_0.equals(processingType))
//                {
//                    exchange=DataSwitchConstants.DIRECT_EXCHANGE_DATASWITCH_EXTRACTION;
//                    routeKey=DataSwitchConstants.DIRECT_EXCHANGE_QUEUE_BINDING_EXTRACTING+key;
//                }

                //todo:
                logger.info(JSONObject.toJSONString(message));
//                Message msg = new Message(JSONObject.toJSONString(message).getBytes("UTF-8"),properties);
//                rabbitMqService.send(exchange, routeKey, msg);
                logger.debug("===ProduceQueueProcessor process success.===");
            } else {
                logger.info("===message is null.===");
            }
        } catch (Exception e) {
            logger.error("QueueConsumer consumerStart occurs error.", e);
            //TODO 如果发送有异常，发邮件出来
        }
    }

    /**
     * 单笔消息发送
     *
     * @param message
     * @param key
     */
    @Override
    public void singleProcess(Object message, String key) {
        try {
            if (null != message) {
                MessageModel obj=(MessageModel)message;
                logger.info(obj.toString());

//                MessageProperties properties = new MessageProperties();
//                properties.setContentType("text/plain");
//                //properties.setMessageId(msgId);
//                MessageModel obj=(MessageModel)message;
//                String processingType=obj.getProcessingType();
//                String exchange=DataSwitchConstants.SINGLE_DIRECT_EXCHANGE_DATASWITCH;
//                String routeKey=DataSwitchConstants.SINGLE_DIRECT_EXCHANGE_QUEUE_BINDING+key;
//
//                if(DataSwitchConstants.PRCOCESSING_TYPE_0.equals(processingType))
//                {
//                    exchange=DataSwitchConstants.DIRECT_EXCHANGE_DATASWITCH_EXTRACTION;
//                    routeKey=DataSwitchConstants.DIRECT_EXCHANGE_QUEUE_BINDING_EXTRACTING+key;
//                }
//                Message msg = new Message(JSONObject.toJSONString(message).getBytes("UTF-8"),properties);
//                rabbitMqService.send(exchange,routeKey, msg);
                logger.debug("===ProduceQueueProcessor singleProcess success.===");
            } else {
                logger.info("===message is null.===");
            }
        } catch (Exception e) {
            logger.error("QueueConsumer consumerStart occurs error.", e);
            //TODO 如果发送有异常，发邮件出来
        }
    }


}
