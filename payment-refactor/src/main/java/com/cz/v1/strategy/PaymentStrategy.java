package com.cz.v1.strategy;

/**
 * 定义支付策略接口
 * @author zhouchaoyu
 * @time 2024-08-18-17:57
 */
public interface PaymentStrategy {
    void pay(double amount);
}
