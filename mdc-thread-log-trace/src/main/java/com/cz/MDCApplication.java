package com.cz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description <mdc 线程日志追踪>
 *
 *  [MDC工具统计一次请求中的所有日志，包括异步](https://mp.weixin.qq.com/s?__biz=MzIwNjg4MzY4NA==&mid=2247511308&idx=1&sn=6a06529676fd71662b00e707da180a23&chksm=97183307a06fba1108c74ee2842ec370636f7d85750990dcc78ff744370f3ecf669f61f1ba1a&scene=178&cur_album_id=2127107948546981892#rd)
 *
 * [SpringBoot+MDC实现全链路日志跟踪](https://mp.weixin.qq.com/s?__biz=MzIwNjg4MzY4NA==&mid=2247514826&idx=1&sn=b76789771d70d34a25448b1638e3d0ba&chksm=971824c1a06fadd7f844a3bbdd8721b5b9f96928ed17dc0ada544bc2445d927aed183c9f492d&scene=178&cur_album_id=2127107948546981892#rd)
 *
 * [MDC全局链路追踪原理与实现 - 掘金 (juejin.cn)](https://juejin.cn/post/6901227625188950030)
 *
 * @author: zhouchaoyu
 * @Date: 2024-08-12
 */
@SpringBootApplication
public class MDCApplication {
    public static void main(String[] args) {

        SpringApplication.run(MDCApplication.class, args);
        System.out.println("Hello world!");
    }
}