package com.coding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @description <定时任务>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/15 14:24
 */
@SpringBootApplication
@EnableScheduling
public class ScheduledDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ScheduledDemoApplication.class,args);
    }
}