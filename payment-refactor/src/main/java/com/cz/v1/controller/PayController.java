package com.cz.v1.controller;


import com.cz.v1.factory.PaymentStrategyFactory;
import com.cz.v1.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 策略加工厂
 * @author zhouchaoyu
 * @time 2024-08-18-22:24
 */
@RestController
public class PayController {
    @Autowired
    private PaymentStrategyFactory paymentStrategyFactory;

    @GetMapping("/v1/{type}/{money}")
    public void v1(@PathVariable("type") String type, @PathVariable("money") String money){
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getPaymentStrategy(type);
        paymentStrategy.pay(Double.parseDouble(money));

    }
}
