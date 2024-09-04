package com.cz.v1.strategy.impl;

import com.cz.v1.strategy.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 实现支付宝支付策略
 * @author zhouchaoyu
 * @time 2024-08-18-17:57
 */
@Slf4j
@Component("ALIPAY")
public class AlipayStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        log.info("AlipayStrategy pay... Using Alipay to pay " + amount + " yuan." );
        // 支付宝支付逻辑
    }
}
