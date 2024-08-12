package com.cz.servide;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-12
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService{
    @Async("MyExecutor")
    @Override
    public void add() throws InterruptedException {
        sleep(2000);
        log.info("正在执行新增user....");
    }
}
