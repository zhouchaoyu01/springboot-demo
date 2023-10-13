package com.coding.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * @description <定时任务>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/15 14:29
 */


@Component
public class MyScheduledTasks {

    //@Scheduled(fixedRate = 5000) // 每隔5秒执行一次
    @Scheduled(cron = "0/10 * * * * ?")
    public void performTask() {
        // 定时任务逻辑
        System.out.println("定时任务执行了。");
    }
}
