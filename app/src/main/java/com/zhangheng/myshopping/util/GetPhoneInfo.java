package com.zhangheng.myshopping.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.content.ContextCompat;

import com.zhangheng.myshopping.getphoneMessage.PhoneSystem;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELEPHONY_SERVICE;


/*获取手机信息*/
public class GetPhoneInfo {
    public static String getHead(Context context) {
        String head = "(model:" + model
                + ") (sdk:" + sdk
                + ") (release:" + release
                + ") (Appversion:" + versionCode(context)
                + ") (tel:" + phoneNum(context)
                + ") (ID:" + getID(context)
                + ")";
        return head;
    }

    public static String model = android.os.Build.MODEL; // 手机型号
    public static String sdk = android.os.Build.VERSION.SDK; // SDK号
    public static String release = "Android" + android.os.Build.VERSION.RELEASE; // android系统版本号

    public static String notice=null;

    public static String versionCode(Context context) {//应用版本
        String versionCode = "HappyShopping " + PhoneSystem.getVersionCode(context);
        return versionCode;
    }

    public static String phoneNum(Context context){//获取本机手机号码（有可能获取不到）
        String getPhone = null;
        PhoneInfoUtils phoneInfoUtils = new PhoneInfoUtils(context);
        TelephonyManager phoneManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        String line1Number=null;
        try {
            line1Number = phoneManager.getLine1Number();
        }catch (Exception e){
            e.printStackTrace();
        }

        if (line1Number == null) {
            getPhone=null;
        } else if (line1Number.startsWith("+86")) {
            getPhone = line1Number.replace("+86", "");
        } else {
            getPhone = line1Number;//得到电话号码
        }

        String providersName = phoneInfoUtils.getProvidersName();
        String nativePhoneNumber = phoneInfoUtils.getNativePhoneNumber();
        SharedPreferences preferences = context.getSharedPreferences("customeruser", MODE_PRIVATE);
        String name = preferences.getString("name", null);
        if (name!=null){
            notice="用户名："+ name;
        }
        if (nativePhoneNumber==null||nativePhoneNumber=="null"){
            //如果获取本机手机号失败，则获取登录用户的手机号
            nativePhoneNumber = preferences.getString("phone", null);
        }
        if (getPhone!=null){
            getPhone = "[" + providersName + "]" + getPhone;
        }else {
            getPhone = "[" + providersName + "]" + nativePhoneNumber;
        }

        return getPhone;
    }

    /**
     * 获取设备唯一ID
     * @param context
     * @return
     */
    public static String getID(Context context) {
        TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
        }
        String id=null;
        BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
        m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        id= android.os.Build.SERIAL;// 获取序列号
        if (id=="unknown"){
            id = m_BluetoothAdapter.getAddress();//蓝牙的MAC地址
        }
        if (id.equals("02:00:00:00:00:00")){
            id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);//获取ANDROID_ID
        }
        if (id==null) {
            id = "85" + //自己拼接构造IMEI Pseudo-Unique ID
                    Build.BOARD.length() % 10 +
                    Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 +
                    Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 +
                    Build.HOST.length() % 10 +
                    Build.ID.length() % 10 +
                    Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 +
                    Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 +
                    Build.TYPE.length() % 10 +
                    Build.USER.length() % 10; //13 digits
        }
        return id;
    }

}
