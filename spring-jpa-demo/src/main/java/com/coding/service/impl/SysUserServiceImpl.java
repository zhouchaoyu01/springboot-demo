package com.coding.service.impl;

import com.coding.entity.SysUser;
import com.coding.repository.SysUserRepository;
import com.coding.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description <功能描述>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:42
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserRepository sysUserDao;


    @Override
    public List<SysUser> getAllUsers() {
        return sysUserDao.findAll();
    }

    @Override
    public List<SysUser> getUsersByUserName(String userName) {
        return sysUserDao.findByUserNameContainingIgnoreCase(userName);
    }
}
