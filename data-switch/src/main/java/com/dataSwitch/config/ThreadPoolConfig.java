package com.dataSwitch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sunlei on 2020/11/12.
 */
@Configuration
public class ThreadPoolConfig {

    //生产主线程，一直存在，
    @Bean("producerThreadPool")
    public ThreadPoolTaskExecutor paymentThreadPool()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(100);
        //最大线程数
        executor.setMaxPoolSize(500);
        //队列中最大长度 >=mainExecutor.maxSize
        //由于核心线程数满了以后，只有队列满了才会创建新线程，所以这里队列长度不能太大，否则队列不满会有等待
        executor.setQueueCapacity(1);
        //线程池维护线程所允许的空闲时间（秒），这个时间不能太短否则会频繁的做gc
        executor.setKeepAliveSeconds(30);
        //线程名称前缀
        executor.setThreadNamePrefix("main-producer-threadPool");
        //线程池满了以后的拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        //初始化加载
        executor.initialize();
        return executor;
    }

    //异常断点处理线程，队列设置大点，允许等待，不能抛出异常，考虑到比对任务，丢弃最老的一条
    @Bean("exceptionThreadPool")
    public ThreadPoolTaskExecutor exceptionThreadPool()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(50);
        //最大线程数
        executor.setMaxPoolSize(200);
        //队列中最大长度 >=mainExecutor.maxSize
        //由于核心线程数满了以后，只有队列满了才会创建新线程，所以这里队列长度不能太大，否则队列不满会有等待
        executor.setQueueCapacity(100);
        //线程池维护线程所允许的空闲时间（秒），这个时间不能太短否则会频繁的做gc
        executor.setKeepAliveSeconds(30);
        //线程名称前缀
        executor.setThreadNamePrefix("exception-thread-pool");
        //线程池满了以后的拒绝策略,丢弃最旧的一条
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        //初始化加载
        executor.initialize();
        return executor;
    }

    //任务比对线程，允许等待，可以抛弃最新的一条等下次执行
    @Bean("compareThreadPool")
    public ThreadPoolTaskExecutor compareThreadPool()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(20);
        //最大线程数
        executor.setMaxPoolSize(50);
        //队列中最大长度 >=mainExecutor.maxSize
        //由于核心线程数满了以后，只有队列满了才会创建新线程，所以这里队列长度不能太大，否则队列不满会有等待
        executor.setQueueCapacity(2);
        //线程池维护线程所允许的空闲时间（秒），这个时间不能太短否则会频繁的做gc
        executor.setKeepAliveSeconds(30);
        //线程名称前缀
        executor.setThreadNamePrefix("compare-thread-pool");
        //线程池满了以后的拒绝策略,拒绝最新的任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        //初始化加载
        executor.initialize();
        return executor;
    }

    //异步处理redis回写处理线程，队列设置大点，允许等待，不能抛出异常，丢弃最旧的一条
    @Bean("timeReloadThreadPool")
    public ThreadPoolTaskExecutor timeReloadThreadPool()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(100);
        //最大线程数
        executor.setMaxPoolSize(200);
        //队列中最大长度 >=mainExecutor.maxSize
        //由于核心线程数满了以后，只有队列满了才会创建新线程，所以这里队列长度不能太大，否则队列不满会有等待
        executor.setQueueCapacity(100);
        //线程池维护线程所允许的空闲时间（秒），这个时间不能太短否则会频繁的做gc
        executor.setKeepAliveSeconds(30);
        //线程名称前缀
        executor.setThreadNamePrefix("time-reload-threadPool");
        //线程池满了以后的拒绝策略,丢弃最旧的一条
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        //初始化加载
        executor.initialize();
        return executor;
    }

}
