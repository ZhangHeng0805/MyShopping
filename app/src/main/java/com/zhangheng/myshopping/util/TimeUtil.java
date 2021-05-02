package com.zhangheng.myshopping.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
* 获取时间
* */
public class TimeUtil {
    public static String getSystemTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");//设置日期格式
        String time=df.format(new Date());// new Date()为获取当前系统时间
        return time;
    }
    public static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        String time=df.format(new Date());// new Date()为获取当前系统时间

        return time;
    }
    public static String getTimeString(){
        SimpleDateFormat time_sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String s=time_sdf.format(new Date());
        return s;
    }
}
