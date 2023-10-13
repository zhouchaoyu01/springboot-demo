package com.coding.redis.service;



import com.coding.redis.entity.SysUser;

import java.util.List;

/**
 * @description <SysUserService接口>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:41
 */

public interface SysUserService {

    List<SysUser> getAllUsers();


    void setUserToRedisByUserId(String userId);
}
