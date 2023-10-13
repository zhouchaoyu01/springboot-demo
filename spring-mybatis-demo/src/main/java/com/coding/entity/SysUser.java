package com.coding.entity;


/**
 * @description <SysUser实体类>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 14:56
 */

public class SysUser {

    private String userId;

    private String userName;

    private String password;

    public SysUser() {
    }

    public SysUser(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
