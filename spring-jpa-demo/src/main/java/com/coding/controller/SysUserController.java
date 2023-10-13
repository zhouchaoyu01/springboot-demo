package com.coding.controller;

import com.coding.entity.SysUser;
import com.coding.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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



    @GetMapping("/users")
    public List<SysUser> getUsers(){
        return sysUserService.getAllUsers();
    }

    @GetMapping("/users/search")
    public List<SysUser> searchUsers(@RequestParam String userName) {
        return sysUserService.getUsersByUserName(userName);
    }
}
