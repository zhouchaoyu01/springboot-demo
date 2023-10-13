package com.coding.service;

import com.coding.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description <SysUserService接口>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:41
 */

public interface SysUserService {

    List<SysUser> getAllUsers();

    List<SysUser> getUsersByUserName(String userName);

}
