package com.zhangheng.myshopping.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myshopping.Main7Activity;
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.adapter.GoodsList_Adapter;
import com.zhangheng.myshopping.adapter.GoodsMeunList_Adapter;
import com.zhangheng.myshopping.base.BaseFragment;
import com.zhangheng.myshopping.bean.shopping.Goods;
import com.zhangheng.myshopping.bean.shopping.submitgoods.SubmitGoods;
import com.zhangheng.myshopping.bean.shopping.submitgoods.goods;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.myshopping.util.TimeUtil;
import com.zhangheng.myshopping.view.RefreshListView;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;


/*
* 主页框架的fragment
* */
public class HomeFragment extends BaseFragment implements  GeocodeSearch.OnGeocodeSearchListener, WeatherSearch.OnWeatherSearchListener{

    private Spinner main_fragment_home_spinner;
    private RefreshListView main_fragment_home_listview;
    private TextView main_fragment_home_txt_notic,main_fragment_home_txt_num
            ,main_fragment_home_txt_pice;
    private LinearLayout main_fragment_home_LL_meun,main_fragment_home_LL_weather;
    private Button main_fragment_home_btn_clearmeun,main_fragment_home_btn_submit;
    private ListView main_fragment_home_listview_meun;
    private CheckBox main_fragment_home_cb_switch;
    private int num=0;//已选商品的总数
    private double pice=0;//已选商品的总金额
    private static final String TAG=HomeFragment.class.getSimpleName();
    private SharedPreferences preferences;
    private String phone,name,password,address,submit_id,weather_address;
    private int spinner_position ;//下拉列表的选择位置
    private List<String> spinner_list = new ArrayList<String>();//下拉列表
    private List<Goods> goodsList;//所有的商品集合
    private List<Goods> g=new ArrayList<>();//购物车的商品集合
    private TextView main_fragment_home_txt_city,main_fragment_home_txt_temp;
    private ImageView main_fragment_home_iv_icon;
    private GeocodeSearch geocodeSearch;
    private AMap aMap = null;
    private MapView mapView;
    private  double latitude=0,longitude=0;
    private LatLonPoint latLng;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private Boolean flag1=true;
    private Boolean flag2=true;

    @Override
    protected View initView() {

        Log.e(TAG,"主页框架Fragment页面被初始化了");
        View view = View.inflate(mContext, R.layout.main_fragment_home, null);
        main_fragment_home_spinner=view.findViewById(R.id.main_fragment_home_spinner);
        main_fragment_home_listview=view.findViewById(R.id.main_fragment_home_listview);
        main_fragment_home_txt_notic=view.findViewById(R.id.main_fragment_home_txt_notic);
        main_fragment_home_txt_num=view.findViewById(R.id.main_fragment_home_txt_num);
        main_fragment_home_txt_pice=view.findViewById(R.id.main_fragment_home_txt_pice);
        main_fragment_home_LL_meun=view.findViewById(R.id.main_fragment_home_LL_meun);
        main_fragment_home_btn_clearmeun=view.findViewById(R.id.main_fragment_home_btn_clearmeun);
        main_fragment_home_btn_submit=view.findViewById(R.id.main_fragment_home_btn_submit);
        main_fragment_home_listview_meun=view.findViewById(R.id.main_fragment_home_listview_meun);
        main_fragment_home_cb_switch=view.findViewById(R.id.main_fragment_home_cb_switch);
        main_fragment_home_iv_icon=view.findViewById(R.id.main_fragment_home_iv_icon);
        main_fragment_home_txt_city=view.findViewById(R.id.main_fragment_home_txt_city);
        main_fragment_home_txt_temp=view.findViewById(R.id.main_fragment_home_txt_temp);
        main_fragment_home_LL_weather=view.findViewById(R.id.main_fragment_home_LL_weather);
        mapView=view.findViewById(R.id.main_fragment_home_amp);
        mapView.setVisibility(View.GONE);
        Listener();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void initData() {
        super.initData();
        start();
        getOkHttp("全部");
        //初始化地图控制器对象

    }

    @Override
    public void onStart() {
        super.onStart();
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        Location(3);
        geocodeSearch = new GeocodeSearch(getContext());
        geocodeSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude=location.getLatitude();//经度  
                longitude=location.getLongitude();//纬度  
                double altitude=location.getAltitude();//海拔 
                latLng = new LatLonPoint(latitude,longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLng, 200,GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);

            }
        });
    }

    /*
    * 控件的监听
    * */
    private void Listener(){
        //商品列表刷新监听
        main_fragment_home_listview.setRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (g.size()>0){
                    AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                    d.setTitle("是否刷新？");
                    d.setMessage("请注意，刷新会清空购物车");
                    d.setPositiveButton("刷新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getOkHttp(spinner_list.get(spinner_position));
                            close_meun();
                            clear_meun();
                            main_fragment_home_listview.onRefreshComplete();
                        }
                    });
                    d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            main_fragment_home_listview.onRefreshComplete();
                        }
                    });
                    d.show();
                }else {
                    getOkHttp(spinner_list.get(spinner_position));
                    main_fragment_home_listview.onRefreshComplete();
                    close_meun();
                    clear_meun();
                }
            }

            @Override
            public void onLoadMore() {
            }
        });
        //加载失败文字点击监听
        main_fragment_home_txt_notic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_list.size()>0){
                    getOkHttp(spinner_list.get(spinner_position));
                }else {
                    getOkHttp("全部");
                }
            }
        });
        /*
        * 菜单列表项点击监听
        * 作用：通过点击菜单里面的商品，可以快速显示该商品在商品清单中的位置
        * */
        main_fragment_home_listview_meun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Goods goods = g.get(i);
                int i1 = goodsList.indexOf(goods);
                if (i1>=0){
                    if (i1==goodsList.size()-1) {
                        main_fragment_home_listview.setSelection(i1+1);
                    }else {
                        main_fragment_home_listview.setSelection(i1+1);
                    }
                }
            }
        });
        //清空购物车按钮的点击监听
        main_fragment_home_btn_clearmeun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 清空购物车");
                clear_meun();
            }
        });
        //提交订单按钮的点击监听
        main_fragment_home_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                Log.d(TAG, "onClick: 提交订单");
                final SubmitGoods submitGoods = new SubmitGoods();
                List<goods> goodslist=new ArrayList<>();
                String[] strings = new String[g.size()+5];
                for (Goods goods:g){
                    com.zhangheng.myshopping.bean.shopping.submitgoods.goods goods1 = new goods();
                    goods1.setGoods_id(goods.getGoods_id());
                    goods1.setGoods_name(goods.getGoods_name());
                    goods1.setGoods_price(goods.getGoods_price());
                    goods1.setNum(goods.getNum());
                    goods1.setStore_id(goods.getStore_id());
                    goodslist.add(goods1);
                }
                for (int i=0;i<g.size();i++){
                    String s=g.get(i).getGoods_name()+" "+g.get(i).getGoods_price()+"元 × "+g.get(i).getNum();
                    strings[i]=s;
                }
                strings[g.size()+4]="已选商品："+num+"件:总金额："+pice+"元";
                if (phone!=null) {
                    strings[g.size() + 3] = "联系电话：" + phone;
                }else {
                    strings[g.size() + 3] = "联系电话：空";
                }
                if (address!=null&&!address.equals("地址为空")) {
                    strings[g.size()+2] = "地址：" + address;
                }else {
                    strings[g.size()+2] = "地址：空";
                }
                if (name!=null) {
                    strings[g.size()+1] = "收货人：" + name;
                }else {
                    strings[g.size()+1] = "收货人：空";
                }
                strings[g.size()]="----------------------------";
                submitGoods.setGoods_list(goodslist);
                submitGoods.setCount_price(pice);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("确认订单");
                builder.setCancelable(false);
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null) {
                            submitGoods.setName(name);
                            submitGoods.setPhone(phone);
                            if (address!=null&&!address.equals("地址为空")) {
                                submitGoods.setAddress(address);
                                submit_id=TimeUtil.getTimeString()+"_"+ UUID.randomUUID().toString().substring(0,8);
                                submitGoods.setTime(TimeUtil.getSystemTime());
                                submitGoods.setSubmit_id(submit_id);
                                OkHttp2(submitGoods);
                            }else {
                                DialogUtil.dialog(getContext(),"地址为空","请前往\"我的\"-\"设置地址\"进行设置后,再来操作");
                            }
                        }else {
                            DialogUtil.dialog(getContext(),"请登录后操作","请前往\"我的\"进行登录后,再来操作");
                        }
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        //购物车清单开关点击监听
        main_fragment_home_cb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (num!=0) {
                        main_fragment_home_LL_meun.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(getContext(),"购物车为空，请先选购商品",Toast.LENGTH_SHORT).show();
                        close_meun();
                    }
                }else {
                    main_fragment_home_LL_meun.setVisibility(View.GONE);
                }
            }
        });
        //定位天气点击监听
        main_fragment_home_LL_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if (weather_address!=null) {
                    intent.putExtra("address", weather_address);
                    intent.setClass(getContext(), Main7Activity.class);
                    startActivity(intent);
                }
            }
        });

    }
    //获取存储在手机里面的账户信息
    private void getPreferences(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        if (password!=null) {
            Resuilt resuilt = new Resuilt(password, 3);    //密码解密
            password = resuilt.getresuilt();
        }
        address = preferences.getString("address", null);
        Log.d(TAG, "getPreferences: "+phone+name);
    }
    /*
    * 获取商品列表
    * */
    private void getOkHttp(String type){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("加载中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Goods/allgoodslist";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("GoodsType",type)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progressDialog.dismiss();
                        final AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                        main_fragment_home_listview.setVisibility(View.GONE);
                        main_fragment_home_txt_notic.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        progressDialog.dismiss();
                        Gson gson=new Gson();
                        try {
                            goodsList = gson.fromJson(response, new TypeToken<List<Goods>>() {
                            }.getType());
                        }catch (Exception e){
                            Log.e(TAG, "onResponse: "+e.getMessage() );
                            if (OkHttpMessageUtil.response(response)==null) {
                                DialogUtil.dialog(getContext(), "错误", e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(), "错误", OkHttpMessageUtil.response(response));
                            }
                        }

                        if (goodsList!=null) {
                            main_fragment_home_txt_notic.setVisibility(View.GONE);
                            main_fragment_home_listview.setVisibility(View.VISIBLE);
                            for (Goods g : goodsList) {
                                g.setGoods_image(getResources().getString(R.string.zhangheng_url)
                                        + "downloads/show/" + g.getGoods_image());
                                g.setNum(0);
                                if (spinner_list.contains(g.getGoods_type())) {
                                } else {
                                    spinner_list.add(g.getGoods_type());
                                }
                            }

                            final BaseAdapter adapter = new GoodsList_Adapter(mContext, goodsList);
                            main_fragment_home_listview.setAdapter(adapter);
                            Toast.makeText(getContext(),"商品数量："+goodsList.size(),Toast.LENGTH_SHORT).show();
                            ((GoodsList_Adapter) adapter).setMyOnClickNum(new GoodsList_Adapter.MyOnClickNum() {
                                @Override
                                public void myNumClick(int position, int operation) {
                                    switch (operation) {
                                        case 1:
                                            num += 1;
                                            double p1 = goodsList.get(position).getGoods_price();
                                            pice += p1;
                                            int indexAdd = g.indexOf(goodsList.get(position));
                                            if (indexAdd >= 0) {
                                                g.get(indexAdd).setNum(g.get(indexAdd).getNum());
                                                GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                main_fragment_home_listview_meun.setAdapter(goodsMeunList_adapter);
                                            } else {
                                                g.add(goodsList.get(position));
                                                GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                main_fragment_home_listview_meun.setAdapter(goodsMeunList_adapter);
                                            }
                                            open_meun();
                                            break;
                                        case 2:
                                            if (num > 0) {
                                                num -= 1;
                                                double p2 = goodsList.get(position).getGoods_price();
                                                pice -= p2;
                                                int indexSub = g.indexOf(goodsList.get(position));
                                                if (indexSub >= 0) {
                                                    if (g.get(indexSub).getNum() > 0) {
                                                        g.get(indexSub).setNum(g.get(indexSub).getNum());
                                                        GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                        main_fragment_home_listview_meun.setAdapter(goodsMeunList_adapter);
                                                    } else {
                                                        g.remove(indexSub);
                                                        GoodsMeunList_Adapter goodsMeunList_adapter = new GoodsMeunList_Adapter(getContext(), g);
                                                        main_fragment_home_listview_meun.setAdapter(goodsMeunList_adapter);
                                                    }
                                                }
                                            }
                                            break;
                                    }
                                    if (num == 0) {
                                        close_meun();
                                    }
                                    main_fragment_home_txt_num.setText(String.valueOf(num) + "件");
                                    BigDecimal bg = new BigDecimal(pice);
                                    pice = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                    main_fragment_home_txt_pice.setText(String.valueOf(pice) + "元");

                                }
                            });
                        }else {
                            main_fragment_home_listview.setVisibility(View.GONE);
                            main_fragment_home_txt_notic.setVisibility(View.VISIBLE);
                        }
                        if (spinner_list.size()>0){
                            main_fragment_home_spinner.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getContext(), R.layout.item_list_text, spinner_list);
                            main_fragment_home_spinner.setAdapter(spinner_adapter);
                            main_fragment_home_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                    String s = spinner_list.get(i);
//                                    List<Goods> glist=new ArrayList<>();
                                    if (spinner_position!=i) {//防止重复刷新
                                        refresh_GoodList(i);
                                        Log.d(TAG, "onItemSelected: "+s);
                                        spinner_position=i;
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }

                            });
                            main_fragment_home_spinner.setSelection(spinner_position);

                        }

                    }
                });
    }

    //开始控件状态
    private void start(){
        main_fragment_home_txt_notic.setVisibility(View.GONE);
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_spinner.setVisibility(View.GONE);
        spinner_position=0;
        spinner_list.add("全部");
    }
    //关闭购物车菜单
    private void close_meun(){
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_cb_switch.setChecked(false);
    }
    //打开购物车菜单
    private void open_meun(){
        main_fragment_home_LL_meun.setVisibility(View.VISIBLE);
        main_fragment_home_cb_switch.setChecked(true);
    }
    //清空购物车菜单
    private void clear_meun(){
        GoodsList_Adapter adapter=new GoodsList_Adapter(mContext,goodsList);
        adapter.clear_num();
        num=0;
        pice=0;
        main_fragment_home_txt_num.setText(String.valueOf(num)+"件");
        main_fragment_home_txt_pice.setText(String.valueOf(pice)+"元");
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_cb_switch.setChecked(false);
        g.clear();
    }
    //刷新商品列表
    private void refresh_GoodList(int i){
        if (g.size()>0){
            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
            d.setTitle("是否切换商品列表？");
            d.setMessage("请注意，切换商品列表会清空当前购物车");
            d.setPositiveButton("切换", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getOkHttp(spinner_list.get(spinner_position));
                    close_meun();
                    clear_meun();

                }
            });
            d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            d.show();
        }else {
            getOkHttp(spinner_list.get(i));
            close_meun();
            clear_meun();
        }
    }
    //提交商品购买清单
    private void OkHttp2(SubmitGoods submitGoods){
        final ProgressDialog progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setMessage("订单提交中。。。");
        progressDialog1.setIndeterminate(true);
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Goods/submitgoodslist";
        Gson gson = new Gson();
        String json = gson.toJson(submitGoods);
        Map<String,String> map=new HashMap<>();
        map.put("submitGoodsList",json);
        OkHttpUtils
                .post()
                .params(map)
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progressDialog1.dismiss();
                        final AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("订单提交错误");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, "onResponse: "+response);
                        progressDialog1.dismiss();
                        if (response.equals("成功")){
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("订单提交成功");
                            d.setMessage("订单编号："+submit_id+"\t\n本次消费："+pice+"元"+"\t\n请前往\"我的\"-\"订单列表\"进行查看订单");
                            d.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    clear_meun();
                                    getOkHttp(spinner_list.get(spinner_position));
                                }
                            });
                            d.show();
                        }else {
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("订单提交失败");
                            d.setMessage("消费金额异常！请重试");
                            d.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            d.show();
                        }
                    }
                });
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
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String city = regeocodeAddress.getCity();//城市
        main_fragment_home_txt_city.setText(city);
        Log.e(TAG, "onRegeocodeSearched: "+city );
//检索参数为城市和天气类型，实况天气为WEATHER_TYPE_LIVE、天气预报为WEATHER_TYPE_FORECAST
        mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch=new WeatherSearch(getContext());
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //异步搜索
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
        if (i==1000){
            if (localWeatherLiveResult != null&&localWeatherLiveResult.getLiveResult() != null) {
                LocalWeatherLive liveResult = localWeatherLiveResult.getLiveResult();

                String city = liveResult.getCity();//城市
                String reportTime = liveResult.getReportTime()+"发布";//发布时间
                String temperature = liveResult.getTemperature()+"℃";//温度"°"
                String weather = liveResult.getWeather();//天气
                String wind=liveResult.getWindDirection()+"风    "+liveResult.getWindPower()+"级";
                String humidity = "湿度："+liveResult.getHumidity()+"%";
                String adCode = liveResult.getWeather();
                Log.e(TAG, "onWeatherLiveSearched: "+city);

                weather_address=city;
                main_fragment_home_txt_temp.setText(weather+"\t"+temperature);
            }
        }else {
            if (i==1200){
                if (flag1) {
                    Toast.makeText(getContext(), "请检查定位服务是否开启", Toast.LENGTH_SHORT).show();
                    DialogUtil.dialog(getContext(),"建议打开定位","打开定位可以获取天气信息");
                    flag1=!flag1;
                }
            }else {
                if (flag2) {
                    Toast.makeText(getContext(), "天气错误：" + i, Toast.LENGTH_SHORT).show();
                    DialogUtil.dialog(getContext(),"天气查询错误","天气错误码："+i);
                    flag2=!flag2;
                }
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }
}
