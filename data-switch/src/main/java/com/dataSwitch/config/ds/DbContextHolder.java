package com.dataSwitch.config.ds;

/**
 * Created by sunlei on 2020/11/16.
 */
public class DbContextHolder {

    private static ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDbType(String dbType)
    {
        contextHolder.set(dbType);
    }

    public static String getDbType()
    {
        return contextHolder.get();
    }

    public static void clearDbType()
    {
        contextHolder.remove();
    }
}
