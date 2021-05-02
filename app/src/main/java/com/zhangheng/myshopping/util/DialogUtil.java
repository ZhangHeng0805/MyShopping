package com.zhangheng.myshopping.util;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtil {
    /*
    * 弹窗提示
    * */
    public static void dialog(Context context,String title, String message){
        AlertDialog.Builder d=new AlertDialog.Builder(context);
        d.setTitle(title);
        d.setMessage(message);
        d.create().show();
    }
}
