package com.coding.repository;

import com.coding.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description <SysUserDao数据库交互层>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 15:35
 */
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, String> {

    public abstract List<SysUser> findByUserNameContainingIgnoreCase(String userName);

    //用jpa这个方法findByUserNameLike为什么不能模糊查询
    //当你使用Like查询进行模糊查询时，你可以在查询字符串中使用通配符 % 来匹配任意数量的字符
    //GET http://localhost:8080/users/search?userName=john%
    //在浏览器中可能出错
    // 显示MissingServletRequestParameterException: Required request parameter 'userName' for method parameter type String is not present]
}
