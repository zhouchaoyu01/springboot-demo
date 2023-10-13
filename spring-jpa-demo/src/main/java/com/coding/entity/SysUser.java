package com.coding.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @description <SysUser实体类>
 * @author: <zhouchaoyu>
 * @Date: 2023/8/14 14:56
 */
@Entity
@Table(name = "sys_user")
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
    @Id
    @Column(name = "user_id", unique=true, nullable=false)
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
