package com.cz.v2.controller;


import com.cz.v1.factory.PaymentStrategyFactory;
import com.cz.v1.strategy.PaymentStrategy;
import com.cz.v2.factory.PaymentStrategyProcessFactory;
import com.cz.v2.template.PaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 策略加工厂加模板
 * @author zhouchaoyu
 * @time 2024-08-18-22:24
 */
@RestController
public class Pay2Controller {
    @Autowired
    private PaymentStrategyProcessFactory paymentStrategyProcessFactory;

    @GetMapping("/v2/{type}/{money}")
    public void v1(@PathVariable("type") String type, @PathVariable("money") String money){
        PaymentProcessor paymentStrategy = paymentStrategyProcessFactory.getPaymentStrategy(type);
        paymentStrategy.processPayment(type,Double.parseDouble(money));

    }
}
