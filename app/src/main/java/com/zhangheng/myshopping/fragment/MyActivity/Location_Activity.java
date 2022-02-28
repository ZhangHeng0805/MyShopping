package com.zhangheng.myshopping.fragment.MyActivity;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.StreetNumber;
import com.google.gson.Gson;
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.bean.shopping.Customer;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class Location_Activity extends Activity implements GeocodeSearch.OnGeocodeSearchListener {

    private ServerSetting setting;
    private static final String TAG= Location_Activity.class.getSimpleName();
    private ImageView location_iv_back;
    private MapView mapView;
    private AMap aMap = null;
    private UiSettings mUiSettings;//定义一个UiSettings对象
    private  double latitude=0,longitude=0;
    private LatLonPoint latLng;
    private GeocodeSearch geocodeSearch;
    private TextView m15_myfragment_location_txt_locaton,m15_myfragment_location_txt_locatoninfo;
    private EditText m15_myfragment_location_et_locaton;
    private Button m15_myfragment_location_btn_save,m15_myfragment_location_btn_ref;
    private SharedPreferences preferences;
    private String phone,password;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_myfragment_activity_location);
        setting=new ServerSetting(this);
        mapView=findViewById(R.id.m15_myfragment_location_map);
        location_iv_back=findViewById(R.id.fragment_my_location_iv_back);
        m15_myfragment_location_txt_locaton=findViewById(R.id.m15_myfragment_location_txt_locaton);
        m15_myfragment_location_btn_save=findViewById(R.id.m15_myfragment_location_btn_save);
        m15_myfragment_location_et_locaton=findViewById(R.id.m15_myfragment_location_et_locaton);
        m15_myfragment_location_txt_locatoninfo=findViewById(R.id.m15_myfragment_location_txt_locatoninfo);
        m15_myfragment_location_btn_ref=findViewById(R.id.m15_myfragment_location_btn_ref);

        m15_myfragment_location_btn_ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude>0&&longitude>0) {
                    latLng = new LatLonPoint(latitude, longitude);
                    RegeocodeQuery query = new RegeocodeQuery(latLng, 200, GeocodeSearch.AMAP);
                    geocodeSearch.getFromLocationAsyn(query);
                    Log.d(TAG, "onClick: "+latLng.toString());
                }else {
                    DialogUtil.dialog(Location_Activity.this,"定位失败","位置获取失败，请开启定位或者到空旷的地方");
                }
            }
        });

        m15_myfragment_location_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                if (phone!=null){
                    String address=m15_myfragment_location_et_locaton.getText().toString();
                    if (address.length()>3){
                        Customer customer = new Customer();
                        customer.setPhone(phone);
                        customer.setPassword(password);
                        customer.setAddress(address);
                        myOkHttp(customer);
                    }else {
                        DialogUtil.dialog(Location_Activity.this,"地址不能为空或地址过短","输入的地址过短");
                    }

                }else {
                    AlertDialog.Builder d=new AlertDialog.Builder(Location_Activity.this);
                    d.setTitle("用户没有登录");
                    d.setMessage("请退出登录后,再来设置位置");
                    d.setCancelable(false);
                    d.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    d.show();
                }
            }
        });
        location_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        mUiSettings = aMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setCompassEnabled(true);//指南针
        mUiSettings.setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//设置logo位置
        aMap.showIndoorMap(true);     //true：显示室内地图；false：不显示；
        aMap.setTrafficEnabled(true);//显示实时路况图层，aMap是地图控制器对象。
        Location(3);
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude=location.getLatitude();//经度  
                longitude=location.getLongitude();//纬度  
                double altitude=location.getAltitude();//海拔 
                latLng = new LatLonPoint(latitude,longitude);
                m15_myfragment_location_txt_locaton.setText(
                        "经度:"+latitude
                                + "\t纬度:"+longitude
                                +"\t海拔:"+altitude
                );

            }
        });
    }
    private void myOkHttp(final Customer customer){
        final ProgressDialog progressDialog=new ProgressDialog(Location_Activity.this);
        progressDialog.setMessage("提交保存中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/setAddress";
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("json",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        DialogUtil.dialog(Location_Activity.this,"错误",error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Message msg=new Message();
                        Gson gson = new Gson();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(Location_Activity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(Location_Activity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (msg!=null){
                            if (msg.getCode()==200){
                                savePreferences(customer.getAddress());
                            }else {
                            }
                            DialogUtil.dialog(Location_Activity.this,msg.getTitle(),msg.getMessage());
                        }else {
                            AlertDialog.Builder d=new AlertDialog.Builder(Location_Activity.this);
                            d.setTitle("保存失败");
                            d.setMessage("网络加载失败");
                            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            d.show();
                        }
                    }
                });
    }
    private void getPreferences(){
        preferences = getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        password = preferences.getString("password", null);
        if (password!=null) {
            Resuilt resuilt = new Resuilt(password, 3);    //密码解密
            password = resuilt.getresuilt();
        }
    }
    private void savePreferences(String address){
        SharedPreferences preferences = getSharedPreferences("customeruser", MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("address",address);
        editor.commit();
    }
    private void Location(int i){
        MyLocationStyle myLocationStyle;
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）
        // 如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle = new MyLocationStyle();
        switch (i){
            case 1:
                //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                break;
            case 0:
                //定位一次，且将视角移动到地图中心点。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 2:
                //定位一次，且将视角移动到地图中心点。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 3:
                //连续定位、且将视角移动到地图中心点，地图依照设备方向旋转，定位点会跟随设备移动。（1秒1次定位）
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                break;
            case 4:
                //连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）默认执行此种模式。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                break;
            case 5:
                //连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
                break;
            case 6:
                //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
                break;
        }
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
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

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        if (i==1000) {
            RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
            String country = regeocodeAddress.getCountry();//国家
            String province = regeocodeAddress.getProvince();//省或直辖市
            String city = regeocodeAddress.getCity();//城市
            String township = regeocodeAddress.getTownship();//乡镇
            String neighborhood = regeocodeAddress.getNeighborhood();//社区名称。
            StreetNumber streetNumber = regeocodeAddress.getStreetNumber();//门牌信息。
            String formatAddress = regeocodeAddress.getFormatAddress();//格式化地址。
            m15_myfragment_location_txt_locatoninfo.setText(formatAddress);
            m15_myfragment_location_et_locaton.setText(formatAddress);
        }else if (i==1200){
            Toast.makeText(Location_Activity.this, "请检查定位服务是否开启", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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
}
