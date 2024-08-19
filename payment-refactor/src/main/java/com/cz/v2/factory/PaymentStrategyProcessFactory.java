package com.cz.v2.factory;


import com.cz.v2.template.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 工厂
 * @author zhouchaoyu
 * @time 2024-08-18-22:09
 */
@Component
public class PaymentStrategyProcessFactory {

    private final Map<String, PaymentProcessor> paymentProcessorMap;

    @Autowired
    public PaymentStrategyProcessFactory(Map<String, PaymentProcessor> paymentProcessorMap) {
        this.paymentProcessorMap = paymentProcessorMap;
    }

    public PaymentProcessor getPaymentStrategy(String type) {
        PaymentProcessor strategy = paymentProcessorMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown payment type: " + type);
        }
        return strategy;
    }
}