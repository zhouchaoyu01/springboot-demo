package com.coding.redis.controller;


import com.coding.redis.entity.SysUser;
import com.coding.redis.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description <SysUserController控制器>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:47
 */
@RestController
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * http://localhost:8001/users
     * @return
     */
    @GetMapping("/users")
    public List<SysUser> getUsers() {
        return sysUserService.getAllUsers();
    }

    /**
     *
     * @param userId
     */
    @GetMapping("/setUserToRedis/{userId}")
    public void setUserToRedisByUserId(@PathVariable(name = "userId") String userId) {
        sysUserService.setUserToRedisByUserId(userId);
    }

    @GetMapping("/count")
    public String count() {
        Long hello = stringRedisTemplate.opsForValue().increment("hello", 1);
        return "访问了" + hello + "次";
    }


}
