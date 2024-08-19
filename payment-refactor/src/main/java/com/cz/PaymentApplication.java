package com.cz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zhouchaoyu
 * @time 2024-08-18-17:54
 */
@Slf4j
@SpringBootApplication
public class PaymentApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(PaymentApplication.class, args);

//        for (String beanDefinitionName : ioc.getBeanDefinitionNames()) {
//            log.info(beanDefinitionName);
//        }

    }
}
