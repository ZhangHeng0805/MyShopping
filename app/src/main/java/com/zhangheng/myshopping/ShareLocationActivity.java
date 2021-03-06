package com.zhangheng.myshopping;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.geocoder.StreetNumber;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myshopping.bean.location.ShareLocation;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.PhoneNumUtil;
import com.zhangheng.myshopping.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class ShareLocationActivity extends Activity implements View.OnClickListener, GeocodeSearch.OnGeocodeSearchListener, WeatherSearch.OnWeatherSearchListener {

    private ServerSetting setting;
    private GeocodeSearch geocodeSearch;
    private MapView mapView;
    private AMap aMap = null;
    private UiSettings mUiSettings;//????????????UiSettings??????
    private RadioGroup m12_rg_mapstyle,m12_rg_locationtype;
    private CheckBox m12_cb_lukuang;
    private LinearLayout m12_LL_mapstyle,m12_LL_locationtype,m12_LL_message;
    private Button m12_btn_mapstyle,m12_btn_downloadmap,m12_btn_locationtype,m12_btn_refresh;
    private TextView m12_tv_location;
    private LatLonPoint latLng;
    private boolean f=true;
    private int screenWidth,screenHeight;
    private static final int PERMISSON_REQUESTCODE = 0;
     // ????????????????????????????????????????????????
    private boolean isNeedCheck = true;
    private String username=null,phone;
    private  double latitude=0,longitude=0;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private TextView m12_tv_city,m12_tv_weather,m12_tv_temperature,
            m12_tv_wind,m12_tv_humidity,m12_tv_reportTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelocation);

        setting=new ServerSetting(this);
        m12_tv_reportTime=findViewById(R.id.m12_tv_reportTime);
        m12_tv_humidity=findViewById(R.id.m12_tv_humidity);
        m12_tv_wind=findViewById(R.id.m12_tv_wind);
        m12_tv_temperature=findViewById(R.id.m12_tv_temperature);
        m12_tv_weather=findViewById(R.id.m12_tv_weather);
        m12_tv_city=findViewById(R.id.m12_tv_city);
        m12_rg_mapstyle=findViewById(R.id.m12_rg_mapstyle);
        m12_cb_lukuang=findViewById(R.id.m12_cb_lukuang);
        m12_LL_mapstyle=findViewById(R.id.m12_LL_mapstyle);
        m12_btn_mapstyle=findViewById(R.id.m12_btn_mapstyle);
        m12_btn_downloadmap=findViewById(R.id.m12_btn_downloadmap);
        m12_btn_downloadmap.setOnClickListener(this);
        m12_btn_mapstyle.setOnClickListener(this);
        m12_LL_locationtype=findViewById(R.id.m12_LL_locationtype);
        m12_btn_locationtype=findViewById(R.id.m12_btn_locationtype);
        m12_btn_locationtype.setOnClickListener(this);
        m12_rg_locationtype=findViewById(R.id.m12_rg_locationtype);
        m12_tv_location=findViewById(R.id.m12_tv_location);
        m12_LL_message=findViewById(R.id.m12_LL_message);
        m12_btn_refresh=findViewById(R.id.m12_btn_refresh);
        m12_btn_refresh.setOnClickListener(this);

        Intent intent=getIntent();
        username = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");

        checkPermission();

        screenWidth = getWindowManager().getDefaultDisplay().getWidth(); // ?????????
        screenHeight = getWindowManager().getDefaultDisplay().getHeight(); // ?????????
        //??????????????????????????????
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) m12_tv_location.getLayoutParams();
        //???????????????
        params.width= (int) (screenWidth*0.7);
        m12_tv_location.setLayoutParams(params);

        mapView=findViewById(R.id.m12_map);
        mapView.onCreate(savedInstanceState);

        //??????????????????????????????
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude=location.getLatitude();//??????????
                longitude=location.getLongitude();//??????????
                double altitude=location.getAltitude();//????????
                latLng = new LatLonPoint(latitude,longitude);
                m12_tv_location.setText(
                        "??????:"+latitude
                                + "\t??????:"+longitude
                        +"\t??????:"+altitude
                );
                RegeocodeQuery query = new RegeocodeQuery(latLng, 100, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);

            }
        });


        mUiSettings = aMap.getUiSettings();//?????????UiSettings?????????
        mUiSettings.setCompassEnabled(true);//?????????
        mUiSettings.setScaleControlsEnabled(true);//?????????????????????????????????
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT);//??????logo??????
        aMap.showIndoorMap(true);     //true????????????????????????false???????????????

        aMap.setMapType(AMap.MAP_TYPE_NORMAL);//???????????????????????????
        m12_rg_mapstyle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m12_rb_01:
                        //????????????
                        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        setbg_black_10();
                        break;
                    case R.id.m12_rb_02:
                        //????????????
                        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                        setbg_white_50();
                        break;
                    case R.id.m12_rb_03:
                        //????????????
                        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        setbg_white_50();;
                        break;
                    case R.id.m12_rb_04:
                        //????????????
                        aMap.setMapType(AMap.MAP_TYPE_NAVI);
                        setbg_black_10();
                        break;
                }
            }
        });

        aMap.setTrafficEnabled(true);//???????????????????????????aMap???????????????????????????
        m12_cb_lukuang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    aMap.setTrafficEnabled(true);//???????????????????????????aMap???????????????????????????
                }else {
                    aMap.setTrafficEnabled(false);//???????????????????????????aMap???????????????????????????
                }
            }
        });

        Location(4);//??????????????????
        m12_rg_locationtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.m12_rb_locationtype_01:
                        Location(3);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_02:
                        Location(5);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_03:
                        Location(1);
                        closeView();
                        break;
                    case R.id.m12_rb_locationtype_04:
                        Location(4);
                        closeView();
                        break;

                }
            }
        });
        closeView();
    }

    public void setbg_white_50(){
        m12_LL_mapstyle.setBackground(getDrawable(R.drawable.bg_white_50));
        m12_btn_locationtype.setBackground(getDrawable(R.drawable.bg_white_50));
        m12_btn_downloadmap.setBackground(getDrawable(R.drawable.bg_white_50));
        m12_btn_mapstyle.setBackground(getDrawable(R.drawable.bg_white_50));
        m12_btn_refresh.setBackground(getDrawable(R.drawable.bg_white_50));
    }
    public void setbg_black_10(){
        m12_LL_mapstyle.setBackground(getDrawable(R.drawable.bg_black_10));
        m12_btn_locationtype.setBackground(getDrawable(R.drawable.bg_black_10));
        m12_btn_downloadmap.setBackground(getDrawable(R.drawable.bg_black_10));
        m12_btn_mapstyle.setBackground(getDrawable(R.drawable.bg_black_10));
        m12_btn_refresh.setBackground(getDrawable(R.drawable.bg_black_10));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView!=null) {
            mapView.onDestroy();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        exitMap();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView!=null) {
            mapView.onResume();
        }
        refreshMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView!=null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView!=null){
            mapView.onSaveInstanceState(outState);
        }
    }

    private void Location(int i){
        MyLocationStyle myLocationStyle;
        //??????????????????????????????myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1????????????
        // ???????????????myLocationType????????????????????????????????????
        myLocationStyle = new MyLocationStyle();
        switch (i){
            case 1:
                //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                break;
            case 0:
                //??????????????????????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 2:
                //??????????????????????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
                break;
            case 3:
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);
                break;
            case 4:
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????1???1???????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
                break;
            case 5:
                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);
                break;
            case 6:
                //??????????????????????????????????????????????????????????????????????????????????????????
                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
                break;
        }
        myLocationStyle.interval(2000); //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        aMap.setMyLocationStyle(myLocationStyle);//?????????????????????Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//?????????????????????????????????????????????????????????
        aMap.setMyLocationEnabled(true);// ?????????true?????????????????????????????????false??????????????????????????????????????????????????????false???
    }

    private void closeView(){
        m12_LL_mapstyle.setVisibility(View.GONE);
        m12_btn_mapstyle.setTextColor(getColor(R.color.black));
        m12_btn_mapstyle.setText("????????????");

        m12_LL_locationtype.setVisibility(View.GONE);
        m12_btn_locationtype.setTextColor(getColor(R.color.black));
        m12_btn_locationtype.setText("????????????");

        if(username!=null){
            m12_btn_refresh.setVisibility(View.VISIBLE);
        }else {
            m12_btn_refresh.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m12_btn_downloadmap:
                //???Activity????????????startActvity????????????????????????
                startActivity(new Intent(this.getApplicationContext(),
                        com.amap.api.maps.offlinemap.OfflineMapActivity.class));
                break;
            case R.id.m12_btn_mapstyle:
                if (m12_LL_mapstyle.getVisibility()==View.GONE){
                    m12_btn_mapstyle.setText("????????????");
                    m12_btn_mapstyle.setTextColor(getColor(R.color.colorAccent));
                    m12_LL_mapstyle.setVisibility(View.VISIBLE);
                }else {
                    closeView();
                }
                break;
            case R.id.m12_btn_locationtype:
                if (m12_LL_locationtype.getVisibility()==View.GONE){
                    m12_btn_locationtype.setText("????????????");
                    m12_btn_locationtype.setTextColor(getColor(R.color.colorAccent));
                    m12_LL_locationtype.setVisibility(View.VISIBLE);
                    m12_LL_locationtype.setBackground(getDrawable(R.drawable.bg_blue));
                }else {
                    closeView();
                }
                break;
            case R.id.m12_btn_refresh:
                refreshMap();
                break;

        }
    }
    private void refreshMap(){
        ShareLocation location1=new ShareLocation();
        if (username!=null) {
            if (latitude != 0&&longitude!=0) {
                if (aMap == null) {
                    aMap = mapView.getMap();
                } else {
                    aMap.clear();
                }
                String time=TimeUtil.getSystemTime(new Date());
                if (!PhoneNumUtil.isMobile(phone) ||username==null){
                    getPreferences();
                }
                location1.setPhone(phone);
                location1.setUsername(username);
                location1.setLatitude(String.valueOf(latitude));
                location1.setLongitude(String.valueOf(longitude));
                location1.setTime(time);
                location1.setState("t");//t?????????f??????
                locationList(location1);
            }
        }
    }
    //??????????????????????????????????????????
    private void getPreferences(){
        SharedPreferences preferences = this.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        username=preferences.getString("name",null);
    }
    private void exitMap(){
        ShareLocation location1=new ShareLocation();
        if (username!=null) {
            if (latitude != 0&&longitude!=0) {
                if (aMap == null) {
                    aMap = mapView.getMap();
                } else {
                    aMap.clear();
                }
                String time=TimeUtil.getSystemTime(new Date());
                location1.setPhone(phone);
                location1.setUsername(username);
                location1.setLatitude(String.valueOf(latitude));
                location1.setLongitude(String.valueOf(longitude));
                location1.setTime(time);
                location1.setState("f");//t?????????f??????
                locationList(location1);
            }
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String country = regeocodeAddress.getCountry();//??????
        String province = regeocodeAddress.getProvince();//???????????????
        String city = regeocodeAddress.getCity();//??????
        String township = regeocodeAddress.getTownship();//??????
        String neighborhood = regeocodeAddress.getNeighborhood();//???????????????
        StreetNumber streetNumber = regeocodeAddress.getStreetNumber();//???????????????
        String formatAddress = regeocodeAddress.getFormatAddress();//??????????????????
        m12_tv_location.setText(formatAddress/*+"\n"+m12_tv_location.getText().toString()*/);

        //??????????????????????????????????????????????????????WEATHER_TYPE_LIVE??????????????????WEATHER_TYPE_FORECAST
        mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch=new WeatherSearch(this);
        mweathersearch.setOnWeatherSearchListener(ShareLocationActivity.this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //????????????
    }
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    //??????????????????
    private void checkPermission(){
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
            };
            //????????????????????????
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //????????????
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }else {

                }
            }
        }
    }
    //??????????????????
    private void showMissingPermissionDialog() {
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("??????");
            builder.setMessage("?????????????????????????????????\\n\\n?????????\\\"??????\\\"-\\\"??????\\\"-??????????????????");

            // ??????, ????????????
            builder.setNegativeButton("??????",
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

            builder.setPositiveButton("??????",
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
    //?????????????????????
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
    /**
     * ??????????????????????????????????????????
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

    //????????????????????????
    private void locationList(ShareLocation location){
        List<ShareLocation> list=new ArrayList<>();
        String url=setting.getMainUrl()+"Customer/share_location";
        Gson gson = new Gson();
        String json = gson.toJson(location);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("location",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        f=false;
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response!=null){
                            Gson gson=new Gson();
                            List<ShareLocation> l = gson.fromJson(response, new TypeToken<List<ShareLocation>>() {
                            }.getType());
                            if (l.size()>0){
                                f=true;
                                for (ShareLocation loc:l) {
                                    LatLng latLng = new LatLng(Double.valueOf(loc.getLatitude()), Double.valueOf(loc.getLongitude()));
                                    MarkerOptions markerOption = new MarkerOptions();
                                    markerOption.position(latLng);
                                    markerOption.title(loc.getUsername());
                                    markerOption.snippet(m12_tv_location.getText().toString()+"\n"+loc.getTime());
                                    if (loc.getState()!=null) {
                                        switch (loc.getState()) {
                                            case "t":
                                                markerOption.visible(true);
                                                break;
                                            case "f":
                                                markerOption.visible(false);
                                                break;
                                            default:
                                                markerOption.visible(true);
                                                break;
                                        }
                                    }
//                                    Log.d("???????????????",loc.getState());
                                    Marker marker = aMap.addMarker(markerOption);
                                    marker.setClickable(true);
                                    if (marker.isVisible()) {
                                    if (loc.getUsername().equals(username)) {
                                            marker.showInfoWindow();
                                        }
                                    }

                                }
                            }else {
                                f=false;
                            }
                        }else {
                            f=false;
                        }
                    }
                });


    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i==1000){
            if (localWeatherLiveResult != null&&localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive liveResult = localWeatherLiveResult.getLiveResult();

                String city = liveResult.getCity();//??????
                String reportTime = liveResult.getReportTime()+"??????";//????????????
                String temperature = liveResult.getTemperature()+"???";//??????"??"
                String weather = liveResult.getWeather();//??????
                String wind=liveResult.getWindDirection()+"??????"+liveResult.getWindPower()+"??????";
                String humidity = "?????????"+liveResult.getHumidity()+"%";
                m12_tv_city.setText(city);
                m12_tv_reportTime.setText(reportTime);
                m12_tv_temperature.setText(temperature);
                m12_tv_weather.setText(weather);
                m12_tv_wind.setText(wind);
                m12_tv_humidity.setText(humidity);

            }
        }else {
            if (i==1200){
                Toast.makeText(ShareLocationActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            }else if (i==1804){
                Toast.makeText(ShareLocationActivity.this, "?????????????????????????????????", Toast.LENGTH_SHORT).show();
            }else if (i==1806){
                Toast.makeText(ShareLocationActivity.this, "?????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }else if (i==1802){
                Toast.makeText(ShareLocationActivity.this, "????????????????????????????????????", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(ShareLocationActivity.this, "???????????????" + i, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
