package com.zhangheng.myshopping.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
* 获取时间
* */
public class TimeUtil {
    public static String getSystemTime(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//设置日期格式
        String time=df.format(date);// new Date()为获取当前系统时间
        return time;
    }
    public static String getTime(Date date){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        String time=df.format(date);// new Date()为获取当前系统时间

        return time;
    }
    public static String getTimeString(Date date){
        SimpleDateFormat time_sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String s=time_sdf.format(date);
        return s;
    }
    //计算两个时间之间的时间差（天数差）[yyyy年MM月dd日 HH:mm:ss]
    public static int daysDifference(String time1,String time2){
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date fromDate = null;
        Date toDate =null;
        int days=-1;
        try {
            fromDate = simpleFormat.parse(time1);
            toDate= simpleFormat.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (fromDate!=null&&toDate!=null) {
            long from = fromDate.getTime();
            long to = toDate.getTime();
            days = (int) ((to - from)*1.0 / (1000*60*60*24));
            days=Math.abs(days);
        }
        return days;
    }
}
