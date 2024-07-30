package com.coding.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-04-08
 */
public class QuartzConfig {
    public static void main(String[] args) throws SchedulerException {
        // 创建任务
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob", "group1")
                .build();

        // 创建触发器
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow() // 任务立即开始
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(5) // 每隔5秒执行一次
                        .repeatForever())
                .build();

        // 创建调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 将任务和触发器绑定到调度器
        scheduler.scheduleJob(jobDetail, trigger);

        // 启动调度器
        scheduler.start();
    }
}