package com.zhangheng.myshopping.util;

import android.content.Context;

import com.zhangheng.myshopping.getphoneMessage.PhoneSystem;


/*获取手机信息*/
public class GetPhoneInfo {
    public static String getHead(Context context) {
        String head = "(model:" + model
                + ") (sdk:" + sdk
                + ") (release:" + release
                + ") (Appversion:" + versionCode(context)
                + ") (phoneNum:" + phoneNum(context)
                + ")";
        return head;
    }

    public static String model = android.os.Build.MODEL; // 手机型号
    public static String sdk = android.os.Build.VERSION.SDK; // SDK号
    public static String release = "Android" + android.os.Build.VERSION.RELEASE; // android系统版本号

    public static String versionCode(Context context) {//应用版本
        String versionCode = "HappyShopping "+PhoneSystem.getVersionCode(context);
        return versionCode;
    }

    public static String phoneNum(Context context) {//获取本机手机号码（有可能获取不到）
        String getPhone = null;
/*        TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context,Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        if (phoneManager.getLine1Number() == null) {
            getPhone="phonenum=null";
        } else if (phoneManager.getLine1Number().startsWith("+86")) {
            getPhone = phoneManager.getLine1Number().replace("+86", "");
        } else {
            getPhone = phoneManager.getLine1Number();//得到电话号码
        }*/
        PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(context);
        getPhone="["+phoneInfoUtils.getProvidersName()+"]"+phoneInfoUtils.getNativePhoneNumber();

        return getPhone;
    }

}
