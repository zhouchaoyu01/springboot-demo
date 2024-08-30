package com.dataSwitch.utils;


import com.dataSwitch.base.bean.DataSwitchControl;
import com.dataSwitch.base.common.DataSwitchException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sunlei on 2020/11/17.
 */
public class DataUtil {

    private static Log logger = LogFactory.getLog(DataUtil.class);
    public static String generateUrl(String driver, String host, int port, String dsName, String schema)
    {
        if(DataSwitchConstants.DB_DRIVER_DB2.equals(driver))
        {
            return String.format("jdbc:db2://%s:%d/%s:currentSchema=%s;", host, port, dsName, schema);
        }
        else if(DataSwitchConstants.DB_DRIVER_MYSQL.equals(driver))
        {
            String url = String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=utf8", host, port, dsName);
            url = url + "&serverTimezone=GMT%2B8";
            return url;
        }
        else if(DataSwitchConstants.DB_DRIVER_ORACLE.equals(driver))
        {
            return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, dsName);
        }
        return "";
    }

    /**
     * 修复配置的开始时间错误问题
     * 正确时间格式为yyyy-mm-dd HH:mm:ss.SSS
     * @param timestr
     * @return
     */
    public static String fixTimeString(String timestr){
        String strtmp = null;
        StringBuffer stringBuffer = new StringBuffer();
        if(timestr.length() == 19){
            stringBuffer.append(timestr + ".");
            for(int i=0;i<3;i++){
                stringBuffer.append("0");
            }
            strtmp = stringBuffer.toString();
        }else if((timestr.length() > 19) && (timestr.length() < 23)){
            stringBuffer.delete(0, stringBuffer.length());
            stringBuffer.append(timestr);
            for(int i=0;i<(23-timestr.length());i++){
                stringBuffer.append("0");
            }
            strtmp = stringBuffer.toString();
        }else if(timestr.length() < 19){
            return null;
        }else{
            return timestr;
        }
        return strtmp;
    }

    /**
     * 根据步进计算结束时间
     *
     * @param start
     * @param step
     * @return
     * @throws ParseException
     */
    public static String nextDatetime(String start, int step, String unit) {
        DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        try {
            Date startDate = f1.parse(start);
            Calendar cl = Calendar.getInstance();
            cl.setTime(startDate);
            switch (unit) {
                case "s":
                    cl.add(Calendar.SECOND, step);
                    break;
                case "ms":
                    cl.add(Calendar.MILLISECOND, step);
                    break;
                default:
                    cl.add(Calendar.SECOND, step);
                    break;
            }
            Date next = cl.getTime();
            return f1.format(next);
        } catch (ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

    /**
     * 记录同步时间
     *
     * @param start
     * @return
     */
    public static boolean writeSyncPoint(String start) {
        String content = "start.time=" + start;
        try {
            File file = new File(System.getProperty("user.dir") + "/checkpoint");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsolutePath());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 与数据库当前时间比较 开始时间快于数据库时间，则认为异常，退出程序;接近数据库时间，则等待直到慢于2倍的forward值，固定时间片;慢于数据库时间，则先保持较快的追补步进，直到慢于2倍左右的forward值
     * 如果开始时间远大于数据库时间，用较快的追补步进，直到接近于数据库时间，这个接近于数据库的时间点间隔是FORWARD_STEP
     * START_TIME：查询区间开始时间
     * WAIT_TRANS_TIME：交易允许误区等待时间
     * CATCH_STEP > FORWARD_STEP > WAIT_TRANS_TIME
     * dbTime：数据库当前时间
     * offset：START_TIME - dbTime  查询时间和当前数据库时间的差距
     * 如果offset>CATCH_STEP+WAIT_TRANS_TIME,查询区间为（START_TIME，START_TIME+CATCH_STEP）；
     * 如果FORWARD_STEP+WAIT_TRANS_TIME<offset<CATCH_STEP+WAIT_TRANS_TIME, 查询区间为（START_TIME，START_TIME+offset-WAIT_TRANS_TIME）；
     * 如果0<offset<FORWARD_STEP+WAIT_TRANS_TIME,等待一段时间，等到offset=FORWARD_STEP+WAIT_TRANS_TIME，查询区间为（START_TIME，START_TIME+FORWARD_STEP）；
     * @param timestart 开始查询时间
     * @param nowDBDateString 当前数据库时间
     * @param dataSwitchControl
     * @return
     */
    public static String calculateNextTime(String timestart, String nowDBDateString, DataSwitchControl dataSwitchControl) throws Exception
    {
        long stepDefaulInteger =dataSwitchControl.getCatchStep();
        long stepDynamic = stepDefaulInteger;
        long waitTransTime = dataSwitchControl.getWaitTransTime();
        long forwardStep = dataSwitchControl.getForwardStep();
        long latency = forwardStep + waitTransTime;
        long catchLatency = stepDefaulInteger + waitTransTime;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        Date startDate = dateFormat.parse(timestart.substring(0, 23));
        Date nowDBDate = dateFormat.parse(nowDBDateString.substring(0, 23));
        long startOffset = nowDBDate.getTime() - startDate.getTime();

        String nextStr = null;
        if (startOffset < 0) {
            logger.error("比较同步的记录开始时间: "+timestart+" 快于数据库当前时间: "+nowDBDateString+" 请检查配置或数据库时钟%n");
            throw new DataSwitchException("","开始时间快于数据库当前时间");
        } else if ((startOffset >= 0) && (startOffset < latency)) {
            logger.debug("比较同步的记录开始时间: "+timestart+" 接近于数据库当前时间: "+nowDBDateString+" 差距: "+startOffset+",等待: "+ (latency-startOffset));
            Thread.sleep(latency-startOffset);
            stepDynamic = forwardStep;
            nextStr = nextDatetime(timestart, (int) stepDynamic, "ms");
            logger.debug("计算结束时间后调整步进为:"+stepDynamic);
            // continue;
        } else {
            if(startOffset >= catchLatency){
                stepDynamic = stepDefaulInteger;
            }else if(startOffset > latency && startOffset < catchLatency){
                stepDynamic = startOffset - waitTransTime;
            }else{
                stepDynamic = forwardStep;
            }
            logger.debug("比较同步的记录开始时间: "+timestart+" 慢于数据库当前时间: "+nowDBDateString+" 差距:"+startOffset+",调整步进为%dms%n"+stepDynamic);
            nextStr = nextDatetime(timestart, (int) stepDynamic, "ms");
        }

        logger.info("本次查询区间("+timestart+","+nextStr+")");

        return nextStr;
    }

    public static void main(String[] args) throws Exception {
        DataSwitchControl dataSwitchControl = new DataSwitchControl();
        dataSwitchControl.setCatchStep(10);
        dataSwitchControl.setForwardStep(10);
        dataSwitchControl.setWaitTransTime(10);
        String s = calculateNextTime("2021-04-27 11:03:13.621", "2023-04-27 11:03:13.621", dataSwitchControl);
        System.out.println(s);
    }
    /**
     * 获取当前时间前后的时间
     * @param startTime
     * @param calendarUnit Calendar.MINUTE
     * @param catchStep
     * @param compareTime
     * @return
     * @throws Exception
     */
    public static String getNextTime(String startTime,int calendarUnit, int catchStep,String compareTime) throws Exception
    {
        String nextTime = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        Date startDatetime = sdf.parse(startTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDatetime);
        // 获取当前前后10分钟的时间点
        calendar.add(calendarUnit, catchStep);
        Date nextDatetime = calendar.getTime();
        nextTime = sdf.format(nextDatetime);
        if(StringUtils.isNotBlank(compareTime))
        {
            Date compare = sdf.parse(compareTime);
            if(nextDatetime.compareTo(compare)<1)
            {
                nextTime = sdf.format(nextDatetime);
            }
            else {
                nextTime = compareTime;
            }
        }
        return nextTime;
    }

    /**
     * 获取当前时间前后的时间
     * @param startTime
     * @param calendarUnit Calendar.MINUTE
     * @param catchStep
     * @return
     * @throws Exception
     */
    public static Date calculateTime(String startTime,int calendarUnit, int catchStep) throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA);
        Date startDatetime = sdf.parse(startTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDatetime);
        // 获取当前前后10分钟的时间点
        calendar.add(calendarUnit, catchStep);
        Date nextDatetime = calendar.getTime();
        return nextDatetime;
    }
}
