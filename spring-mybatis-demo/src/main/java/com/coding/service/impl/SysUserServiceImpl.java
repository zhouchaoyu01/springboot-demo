package com.coding.service.impl;

import com.coding.entity.SysUser;
import com.coding.mapper.SysUserMapper;
import com.coding.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

//    @Override
//    public List<SysUser> getAllUsers() {
//        return sysUserMapper.findAllUsers();
//    }

}
