package com.cz.v2.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 *  模板方法模式
 * @author zhouchaoyu
 * @time 2024-08-18-22:10
 */
@Slf4j
public abstract class PaymentProcessor  {

    //强制子类实现
    public abstract void processPayment(String type, double amount);

    protected void validatePayment(double amount) {
        // 通用的支付验证逻辑
        log.info("Validating payment amount: " + amount);
    }

    protected void afterPayment() {
        // 通用的支付后处理逻辑
        log.info("Payment processed successfully.");
    }
}

