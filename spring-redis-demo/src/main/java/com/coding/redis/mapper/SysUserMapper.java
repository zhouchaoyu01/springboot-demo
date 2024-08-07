package com.coding.redis.mapper;


import com.coding.redis.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description <SysUserDao数据库交互层>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:35
 */
@Mapper
public interface SysUserMapper {

    List<SysUser> findAllUsers();

    SysUser getUserById(String userId);

}
