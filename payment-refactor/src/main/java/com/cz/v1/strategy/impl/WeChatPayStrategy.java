package com.cz.v1.strategy.impl;

import com.cz.v1.strategy.PaymentStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 实现微信支付策略
 * @author zhouchaoyu
 * @time 2024-08-18-17:57
 */
@Slf4j
@Component("WECHAT_PAY")
public class WeChatPayStrategy implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        log.info("WeChatPayStrategy pay... Using WeChat  to pay " + amount + " yuan. ");
        //  微信支付逻辑
    }
}
