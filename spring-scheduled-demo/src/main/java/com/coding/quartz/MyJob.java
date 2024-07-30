package com.coding.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-04-08
 */
public class MyJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 在这里编写你要执行的任务逻辑
        System.out.println("执行定时任务！");
    }
}