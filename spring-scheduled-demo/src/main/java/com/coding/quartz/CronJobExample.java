package com.coding.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-04-08
 */
public class CronJobExample {
    public static void main(String[] args) throws SchedulerException {
        // 创建任务
        JobDetail jobDetail = JobBuilder.newJob(MyJob.class)
                .withIdentity("cronJob", "group1")
                .build();

        // 创建触发器，使用Cron表达式
        String cronExpression = "0/5 * * * * ?"; // 每5秒执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("cronTrigger", "group1")
                .startNow() // 任务立即开始
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        // 创建调度器
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 将任务和触发器绑定到调度器
        scheduler.scheduleJob(jobDetail, trigger);

        // 启动调度器
        scheduler.start();

        // 为了不让程序立即退出，我们等待一段时间
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 最后，关闭调度器（在实际应用中，你可能不会立即关闭它，这里只是为了示例）
        scheduler.shutdown(true);
    }
}
