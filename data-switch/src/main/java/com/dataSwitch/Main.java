package com.dataSwitch;

import com.dataSwitch.producer.Producer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-28
 */
@MapperScan(basePackages = "com.dataSwitch.base.*")
@SpringBootApplication(scanBasePackages = {"com.dataSwitch.*"})
public class Main  implements CommandLineRunner, ApplicationListener<ContextClosedEvent> {
    private static Log logger = LogFactory.getLog(Main.class);

    public static ConfigurableApplicationContext content;

    @Autowired
    private Producer producer;

    public static void main(String[] args) {
        System.out.println("Hello world!");
        content = SpringApplication.run(Main.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        producer.start();
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.info("Shutdown complete,start stop Application.");
        try {
            Thread.sleep(60*1000);
        }
        catch (Exception e)
        {
            logger.error("stop Application occurs error.",e);
        }
        logger.info("stop Application.");
        System.exit(SpringApplication.exit(content));

    }
}