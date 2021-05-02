package com.zhangheng.myshopping.util;
/*
* 处理OkHttpUtils返回的错误信息
* */
public class OkHttpMessageUtil {

    public static String error(Exception e){
        String msg=null;
        if (e.getMessage().indexOf("404")>1||e.getMessage().indexOf("not found")>1){
            msg="服务器未连接";
        }else if (e.getMessage().startsWith("Unable to resolve host")){
            msg="网络异常";
        }else if (e.getMessage().indexOf("timeout")>=0){
            msg="服务器连接超时";
        }else if (e.getMessage().startsWith("failed to connect to")||e.getMessage().startsWith("Failed to connect to")){
            msg="服务器连接失败";
        }else if (e.getMessage().startsWith("unexpected end of stream on")){
            msg="找不到服务器";
        }
        else {
            msg=e.getMessage();
        }
        return msg;
    }
    public static String response(String response){
        String msg="";
        if (response.indexOf("WEB服务器没有运行")>1){
            msg="WEB服务器没有运行";
        }else {
            msg=null;
        }
        return msg;
    }
}
