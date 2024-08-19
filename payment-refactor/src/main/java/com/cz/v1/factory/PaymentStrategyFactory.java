package com.cz.v1.factory;

import com.cz.v1.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 工厂
 * @author zhouchaoyu
 * @time 2024-08-18-22:09
 */
@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> paymentStrategies;

    @Autowired
    public PaymentStrategyFactory(Map<String, PaymentStrategy> paymentStrategies) {
        this.paymentStrategies = paymentStrategies;
    }

    public PaymentStrategy getPaymentStrategy(String type) {
        PaymentStrategy strategy = paymentStrategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown payment type: " + type);
        }
        return strategy;
    }
}