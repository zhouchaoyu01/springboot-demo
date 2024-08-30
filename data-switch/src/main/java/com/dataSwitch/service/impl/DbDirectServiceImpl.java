package com.dataSwitch.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dataSwitch.base.bean.DatabaseConfig;
import com.dataSwitch.base.common.DataSwitchException;
import com.dataSwitch.service.DbDirectService;
import com.dataSwitch.config.ds.DbContextHolder;
import com.dataSwitch.config.ds.DynamicDataSource;
import com.dataSwitch.utils.DataSwitchConstants;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunlei on 2020/11/17.
 */
@Service
public class DbDirectServiceImpl implements DbDirectService {
    private static Log logger = LogFactory.getLog(DbDirectServiceImpl.class);
    public static final int CONNECTION_TIME_OUT = 1;

    @Autowired
    @Qualifier("dynamicDataSource")
    private DynamicDataSource dynamicDataSource;

    public Connection getDBConnection() throws Exception{
        Connection conn = null;
        try {
            conn = dynamicDataSource.getConnection();
            logger.info("已获取数据库连接...");
        } catch (Exception e) {
            logger.error("getDBConnection occurs error.",e);
            throw new DataSwitchException("数据库连接失败",e.getCause());
        }
        return conn;
    }

    public Connection getDBConnection(DatabaseConfig databaseConfig) throws Exception{
        Connection conn = null;
        try {
            /*Class.forName(databaseConfig.getDbDriver());
            DriverManager.setLoginTimeout(CONNECTION_TIME_OUT);
            String addr = DataUtil.generateUrl(databaseConfig.getDbHost(), databaseConfig.getDbPort(), databaseConfig.getDbName(), databaseConfig.getDbSchema());
            String username = databaseConfig.getUserName();
            String password = databaseConfig.getPassword();
            conn = DriverManager.getConnection(addr, username, password);*/
            conn = dynamicDataSource.getConnection();
            logger.info("已获取数据库连接...");
        } catch (Exception e) {
            throw new DataSwitchException("数据库连接失败",e.getCause());
        }
        return conn;
    }


    public String getDBCurrentDateTime(Connection conn,String driver) throws Exception{
        // 获取数据库当前时间
        String nowDBDateString = "";
        int dbretrys=0;
        while(StringUtils.isBlank(nowDBDateString))
        {
            nowDBDateString = getDBDateTime(conn,driver);
            if (StringUtils.isBlank(nowDBDateString)) {
                logger.info("获取数据库时间失败，等待5s重试%n");
                dbretrys += 1;
                if (dbretrys > 3) {
                    logger.error("获取数据库时间失败，重试3次均失败%n");
                    throw new DataSwitchException("","获取数据库时间失败");
                }
                Thread.sleep(5000);
            }
        }
        return nowDBDateString;
    }

    private String getDBDateTime(Connection conn,String driver) {
        String dbDate = null;
        String sqlString = "";
        if(DataSwitchConstants.DB_DRIVER_DB2.equals(driver))
        {
            sqlString = "select current timestamp from sysibm.dual";
        }
        else if(DataSwitchConstants.DB_DRIVER_MYSQL.equals(driver)) {
            sqlString = "select current_timestamp from dual";
        }else if(DataSwitchConstants.DB_DRIVER_ORACLE.equals(driver))
        {
            sqlString = "select systimestamp from dual";
        }
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sqlString,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            rs = ps.executeQuery();
            if(rs.first()){
                /**
                 * 这里有陷阱，转换后会忽略掉毫秒后面多余的0
                 * 需要保持.SSS的格式才能进行相同格式时间的转换比较，必须补0
                 */
                // dbDate = rs.getString("systimestamp");
                Timestamp curr = rs.getTimestamp(1);
                dbDate = curr.toString();
                StringBuffer sb = new StringBuffer();
                sb.append(dbDate);
                if(dbDate.length() < 23){
                    for(int i=0;i<(23-dbDate.length());i++){
                        sb.append("0");
                    }
                }
                dbDate = sb.toString();
            }
            return dbDate;
        } catch (SQLException e) {
            logger.error("getDBDateTime occures error.",e);
            return null;
        }
        finally {
            if(null!= rs)
            {
                try {
                    rs.close();
                }catch (Exception e)
                {
                    logger.error(e);
                }
            }
            if(null!= ps)
            {
                try {
                    ps.close();
                }catch (Exception e)
                {
                    logger.error(e);
                }
            }
        }

    }

    public PreparedStatement getDBRecords(Connection conn, String start, String next, String tabName, String queryColum, String driver) throws Exception{
        PreparedStatement ps = null;
        try {
            String sql ="";
            if(DataSwitchConstants.DB_DRIVER_DB2.equals(driver))
            {
                sql = "select * from " + tabName + " where " + queryColum + " >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and " + queryColum+ " < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')";
            }
            else if(DataSwitchConstants.DB_DRIVER_MYSQL.equals(driver))
            {
                sql = "select * from " + tabName + " where " + queryColum + " >= STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s') and " + queryColum+ " < STR_TO_DATE(?, '%Y-%m-%d %H:%i:%s')";
            }else if(DataSwitchConstants.DB_DRIVER_ORACLE.equals(driver))
            {
                sql = "select * from " + tabName + " where " + queryColum + " >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and " + queryColum+ " < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')";
            }
            //ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ps = conn.prepareStatement(sql,ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            ps.setFetchSize(50);
            ps.setFetchDirection(ResultSet.FETCH_REVERSE);
            //ps.clearParameters();
            ps.setString(1, start);
            ps.setString(2, next);
            logger.info("sql:"+sql);
        } catch (Exception e) {
            throw new Exception();
        }
        return ps;
    }

    public JSONArray resultSetToJson(ResultSet rs) {
        JSONArray array = new JSONArray();
        try{
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
            rs.beforeFirst();
            while (rs.next()) {
                JSONObject jsonObj = new JSONObject();
                for (int i = 0; i < columnCount; i++) {
                    String colName = rsm.getColumnName(i+1);
                    String colType = rsm.getColumnTypeName(i+1);
                    String value = rs.getString(colName);
                    if(value == null){
                        value = "";
                    }
                    jsonObj.put(colName+"|"+colType, value);
                }
                array.add(jsonObj);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
        return array;
    }


    /**
     * 获取数据库比对数据,考虑到有可能queryColum为空的情况，为空直接count总量
     *
     * @param dsName
     * @param tabName
     * @param compareColum
     * @return
     * @throws Exception
     */
    public Map<String,Integer> getRecordCount(String dsName, String tabName, String queryColum, String startTime, String endTIme, String compareColum, String driver) throws Exception{
        DbContextHolder.setDbType(dsName);
        Connection conn =null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String,Integer> jsonObj = new HashMap<>();
        try {
            conn = getDBConnection();
            StringBuffer sqlbf = new StringBuffer();
            if(DataSwitchConstants.DB_DRIVER_DB2.equals(driver))
            {
                sqlbf.append("select count(1) as cout,").append(StringUtils.isBlank(compareColum)?" 'all'":compareColum).append(" from ").append(tabName).append(" where ").append(queryColum)
                        .append(" >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and ").append(queryColum).append(" < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')").append(StringUtils.isBlank(compareColum)?"":" group by ")
                        .append(StringUtils.isBlank(compareColum)?"":compareColum);
                //sql = "select count(1) as cout,"+compareColum+" from " + tabName + " where " + queryColum + " >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and " + queryColum+ " < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')"+" group by " + compareColum ;
            }
            else if(DataSwitchConstants.DB_DRIVER_MYSQL.equals(driver))
            {
                sqlbf.append("select count(1) as cout,").append(StringUtils.isBlank(compareColum)?" 'all'":compareColum).append(" from ").append(tabName).append(" where ").append(queryColum)
                        .append(" >= timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and ").append(queryColum).append(" < timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')").append(StringUtils.isBlank(compareColum)?"":" group by ")
                        .append(StringUtils.isBlank(compareColum)?"":compareColum);
                //sql = "select count(1) as cout,"+compareColum+" from " + tabName + " where " + queryColum + " >= timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and " + queryColum+ " < timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')"+" group by " + compareColum ;
            }
            else if(DataSwitchConstants.DB_DRIVER_ORACLE.equals(driver))
            {
                sqlbf.append("select count(1) as cout,").append(StringUtils.isBlank(compareColum)?" 'all'":compareColum).append(" from ").append(tabName).append(" where ").append(queryColum)
                        .append(" >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and ").append(queryColum).append(" < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')").append(StringUtils.isBlank(compareColum)?"":" group by ")
                        .append(StringUtils.isBlank(compareColum)?"":compareColum);
                //sql = "select count(1) as cout,"+compareColum+" from " + tabName + " where " + queryColum + " >= to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff') and " + queryColum+ " < to_timestamp(?, 'yyyy-mm-dd hh24:mi:ss.ff')"+" group by " + compareColum ;
            }
            String sql = sqlbf.toString();
            logger.info("getRecordCount sql:"+sql);
            ps = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ps.setString(1,startTime);
            ps.setString(2,endTIme);
            rs = ps.executeQuery();
            if(rs.first())
            {
                rs.beforeFirst();
                int total =0;
                while (rs.next()) {
                    //TODO 此处是否要加上状态，加上状态可能各个数据库状态的类型不一样，是否需要处理
                    int value = rs.getInt("cout");
                    String key = rs.getString(StringUtils.isBlank(compareColum)?"all":compareColum);
                    jsonObj.put(key,value);
                    total += value;
                }
                jsonObj.put("total",total);
            }
        } catch (Exception e) {
            logger.error("",e);
        }
        finally {
            closeConnection(conn,ps,rs);
            DbContextHolder.clearDbType();
        }
        return jsonObj;
    }

    @Override
    public void closeConnection(Connection connection, PreparedStatement ps, ResultSet rs) {
        if(null!=rs)
        {
            try {
                rs.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(null!=ps)
        {
            try {
                ps.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if(null!=connection)
        {
            try {
                connection.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
