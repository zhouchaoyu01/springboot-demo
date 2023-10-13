package com.coding.redis.service.impl;


import com.coding.redis.entity.SysUser;
import com.coding.redis.mapper.SysUserMapper;
import com.coding.redis.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SysUser> getAllUsers() {
        return sysUserMapper.findAllUsers();
    }

    @Override
    public void setUserToRedisByUserId(String userId) {
        SysUser user = sysUserMapper.getUserById(userId);
        redisTemplate.opsForValue().set("userinfo", user);
        System.out.println("=====保存成功======");
    }

}
