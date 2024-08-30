package com.dataSwitch.service;


import com.dataSwitch.base.bean.DatabaseConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by sunlei on 2020/11/17.
 */
public interface DbDirectService {

    Connection getDBConnection() throws Exception;

    Connection getDBConnection(DatabaseConfig databaseConfig) throws Exception;

    String getDBCurrentDateTime(Connection conn,String driver) throws Exception;

    PreparedStatement getDBRecords(Connection conn, String start, String next, String tabName, String queryColum, String driver)throws Exception;

    void closeConnection(Connection connection, PreparedStatement ps, ResultSet rs);

    Map<String,Integer> getRecordCount(String dsName, String tabName, String queryColum, String startTime, String endTIme, String compareColum, String driver) throws Exception;
}
