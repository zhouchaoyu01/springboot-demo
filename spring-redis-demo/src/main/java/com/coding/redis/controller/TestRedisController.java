package com.coding.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2023-08-31
 */
@RestController
@RequestMapping("/mm")
public class TestRedisController {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/count")
    public String count() {
        Long hello = stringRedisTemplate.opsForValue().increment("hello", 1);
        return "访问了" + hello + "次";
    }
}
