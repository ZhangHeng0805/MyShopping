package com.zhangheng.myshopping;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.zhangheng.myshopping.base.BaseFragment;
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.fragment.ChatFragment;
import com.zhangheng.myshopping.fragment.HomeFragment;
import com.zhangheng.myshopping.fragment.MyFragment;
import com.zhangheng.myshopping.getphoneMessage.PhoneSystem;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.GetPhoneInfo;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class MainActivity extends FragmentActivity {
    private String[] permissions = {
            Manifest.permission.INTERNET,//网络
            Manifest.permission.ACCESS_COARSE_LOCATION,//粗略定位
            Manifest.permission.ACCESS_FINE_LOCATION,//精确定位
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,//后台定位
            Manifest.permission.ACCESS_NETWORK_STATE,//网络状态
            Manifest.permission.ACCESS_WIFI_STATE,//wifi状态
            Manifest.permission.WRITE_EXTERNAL_STORAGE,//写外部存储器
            Manifest.permission.READ_EXTERNAL_STORAGE,//写外部存储器
            Manifest.permission.READ_PHONE_STATE,//读取手机状态
    };
    private SharedPreferences sharedPreferences;
    private AlertDialog.Builder builder;
    private RadioGroup rg_main;
    private List<BaseFragment> mBaseFragment;
    private int position=0;   //选中的fragment对应的位置
    private Fragment mContext;     //上次切换的Fragment
    private static final String TAG =MainActivity.class.getSimpleName() ;
    private String getPhone,model,sdk,release,versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initFragment();//初始化Fragment
        setListener();
        rg_main.check(R.id.main_rb_home_fragme);

        model = android.os.Build.MODEL; // 手机型号
        sdk = android.os.Build.VERSION.SDK; // SDK号
        release = "Android" + android.os.Build.VERSION.RELEASE; // android系统版本号
        String s = model + "\t" + sdk + "\t" + release;
        //获取自己手机号码
        TelephonyManager phoneManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        if (phoneManager.getLine1Number()==null){

        } else if (phoneManager.getLine1Number().startsWith("+86")){
            getPhone=phoneManager.getLine1Number().replace("+86","");
        }else {
            getPhone = phoneManager.getLine1Number();//得到电话号码
        }
        versionCode= PhoneSystem.getVersionCode(this);
        getupdatelist();
    }

    private void setListener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.main_rb_home_fragme:
                        position=0;
                        break;
                    case R.id.main_rb_chat_fragme:
                        position=1;
                        break;
                    case R.id.main_rb_my_fragme:
                        position=2;
                        break;
                    default:
                        position=0;
                        break;
                }

                BaseFragment to=getFragment();
                switchFragment(mContext,to);
                Log.d(TAG, "onCheckedChanged: "+position);
            }
        });
    }
    /*
     * from:刚刚显示的fragment，马上要被隐藏
     * to:马上要被替换的fragment，一会儿要显示
     * */
    private void switchFragment(Fragment from,Fragment to) {
        if (from!=to){
            mContext=to;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //才切换
            //判断有没有添加
            if(!to.isAdded()){
                //to没有被添加
                //from隐藏
                if (from!=null){
                    ft.hide(from);
                }
                //to添加
                if (to!=null){
                    ft.add(R.id.main_fl_content,to).commit();
                }
            }else {
                //to已经被添加了
                //from隐藏
                if (from!=null){
                    ft.hide(from);
                }
                //to展示
                if (to!=null){
                    ft.show(to).commit();
                }
            }
        }
    }
    private BaseFragment getFragment() {
        BaseFragment fragment = mBaseFragment.get(position);
        return fragment;
    }
    private void initFragment() {
        mBaseFragment=new ArrayList<>();
        mBaseFragment.add(new HomeFragment());
        mBaseFragment.add(new ChatFragment());
        mBaseFragment.add(new MyFragment());
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        rg_main=findViewById(R.id.main_rg_main);
    }

    //检查应用更新
    public void getupdatelist(){
        String url=getResources().getString(R.string.zhangheng_url)
                +"fileload/updatelist/"+getResources().getString(R.string.update_name);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("User-Agent", GetPhoneInfo.getHead(getApplicationContext()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(MainActivity.this,"错误："+error,Toast.LENGTH_SHORT).show();
//                        DialogUtil.dialog(getApplicationContext(),"错误",error);
                        Log.e("错误：",e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Message msg=null;
                        Gson gson=new Gson();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){

                        }
                        if (response.indexOf("WEB服务器没有运行")<1) {
                            if (msg != null) {
                                Toast.makeText(MainActivity.this, "服务器已连接", Toast.LENGTH_SHORT).show();
//                                m3_tv_service.setText("服务器已连接");
//                                m3_tv_service.setTextColor(getColor(R.color.black));
                                if (msg.getCode()==200) {
                                    if (msg.getTitle().equals(getResources().getString(R.string.app_name))) {
                                        sharedPreferences = getSharedPreferences("乐购update", MODE_PRIVATE);
                                        String urlname = sharedPreferences.getString("urlname", "");
                                        if (!urlname.equals(msg.getMessage())) {
                                            //如果版本不一致则提示更细
                                            if (!versionCode.equals(appversion(msg.getMessage()))){
                                                showUpdate(msg.getMessage());
                                            }
                                        } else {
                                            Log.d("忽略更新", "urlname与更新地址一致");
                                        }
                                    } else {
                                        Log.d("title", "title与应用的名称不一致");
                                    }
                                } else {
                                    Log.d("title", "title为null");
                                }
                            } else {
                                Log.d("resuilt", "resuilt为空");
                            }
                        }else {
                            DialogUtil.dialog(getApplicationContext(),"错误","WEB服务器没有运行");
                        }
                        Log.d("更新：",response);
                    }
                });
    }
    //获取APP版本
    public String appversion(String name){
        String[] strings=name.split("/");
        String appname=strings[strings.length-1].replace(".apk","");
        String[] s = appname.split("_");
        String app;
        if (s.length>1){
            app=s[s.length-1];
        }else {
            app=s[0];
        }
        return app;
    }
    //弹窗提示更新
    public void showUpdate(final String name){
        String app=appversion(name);
        builder=new AlertDialog.Builder(this)
                .setTitle("更新")
                .setMessage("有新的版本《"+app+"》可以更新，是否去下载更新包？" +
                        "\n如果更新后还弹出更新，可以选忽略")
                .setPositiveButton("去更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String url = getResources().getString(R.string.zhangheng_url)
                                +"fileload/"+name;
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("忽略此版本", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity.this)
                                .setTitle("提示")
                                .setMessage("忽略此版本代表<相同版本>的更新不在提示，如果有其他版本，还会继续提示更新")
                                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sharedPreferences=getSharedPreferences("update",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString("urlname",name);
                                        editor.apply();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                        builder1.create().show();
                    }
                });
        builder.create().show();
    }

    //权限检查
    //是否需要检测后台定位权限，设置为true时，如果用户没有给予后台定位权限会弹窗提示
    private boolean needCheckBackLocation = false;
    //如果设置了target > 28，需要增加这个权限，否则不会弹出"始终允许"这个选择框
    private static String BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION";

    private static final int PERMISSON_REQUESTCODE = 0;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        try{
            super.onResume();
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(permissions);
                }
            }
            checkPermission();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    //检查申请权限
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
//                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    isNeedCheck = true;
                }else {

                }
            }
        }
    }
    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try{
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                if (null != needRequestPermissonList
                        && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try{
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        if(!needCheckBackLocation
                                && BACK_LOCATION_PERMISSION.equals(perm)) {
                            continue;
                        }
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try{
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try{
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        showMissingPermissionDialog();
                        isNeedCheck = false;
                    }
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。\n\n请点击\"设置\"-\"权限\"-打开所需权限");
            builder.setCancelable(false);

            // 拒绝, 退出应用
            builder.setNegativeButton("退出应用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try{
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("去设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try{
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void finish() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("退出");
        dialog.setMessage("确定是否要离开？");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "取消退出", Toast.LENGTH_LONG).show();
            }
        });
        if (dialog != null) {
            dialog.show();
        }
    }
}
