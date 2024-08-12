package com.cz.controller;

import com.cz.servide.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-08-12
 */
@EnableAsync
@Controller
@Slf4j
public class TestController {
    @Resource
    private IUserService userService;
    @GetMapping("doTest")
    public String doTest(@RequestParam("name") String name) throws InterruptedException {
        log.info("入参 name={}",name);
        testTrace();
        log.info("调用结束 name={}",name);
        return "Hello,"+name;
    }
    private void testTrace() throws InterruptedException {
        log.info("这是一行info日志");
        log.error("这是一行error日志");
        testTrace2();
    }
//    private void testTrace2(){
//        log.info("这也是一行info日志");
//
//    }
    private void testTrace2() throws InterruptedException {
        log.info("这也是一行info日志");
        userService.add();
    }
}
