package com.zhangheng.myshopping.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.WeatherActivity;
import com.zhangheng.myshopping.adapter.GoodsList_Adapter;
import com.zhangheng.myshopping.adapter.GoodsMeunList_Adapter;
import com.zhangheng.myshopping.base.BaseFragment;
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.bean.shopping.Goods;
import com.zhangheng.myshopping.bean.shopping.submitgoods.SubmitGoods;
import com.zhangheng.myshopping.bean.shopping.submitgoods.goods;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.GetPhoneInfo;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.myshopping.util.TimeUtil;
import com.zhangheng.myshopping.view.RefreshListView;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;


/*
* ???????????????fragment
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
    private int num=0;//?????????????????????
    private double pice=0;//????????????????????????
    private static final String TAG=HomeFragment.class.getSimpleName();
    private SharedPreferences preferences;
    private String phone,name,password,address,submit_id,weather_address,search_name="";
    private int spinner_position ;//???????????????????????????
    private List<String> spinner_list = new ArrayList<String>();//????????????
    private List<Goods> goodsList;//?????????????????????
    private List<Goods> g=new ArrayList<>();//????????????????????????
    private TextView main_fragment_home_txt_city,main_fragment_home_txt_temp;
    private ImageView main_fragment_home_iv_search;
    private EditText main_fragment_home_et_search;
    private GeocodeSearch geocodeSearch;
    private AMap aMap = null;
    private MapView mapView;
    private  double latitude=0,longitude=0;
    private LatLonPoint latLng;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private Boolean flag1=true;
    private Boolean flag2=true;
    private ServerSetting setting;
    @Override
    protected View initView() {
        Log.e(TAG,"????????????Fragment?????????????????????");
        setting=new ServerSetting(getContext());
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
        main_fragment_home_txt_city=view.findViewById(R.id.main_fragment_home_txt_city);
        main_fragment_home_txt_temp=view.findViewById(R.id.main_fragment_home_txt_temp);
        main_fragment_home_LL_weather=view.findViewById(R.id.main_fragment_home_LL_weather);
        main_fragment_home_iv_search=view.findViewById(R.id.main_fragment_home_iv_search);
        main_fragment_home_et_search=view.findViewById(R.id.main_fragment_home_et_search);

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
        Location(0);
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
        getOkHttp("??????",search_name);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        Location(0);
        geocodeSearch = new GeocodeSearch(getContext());
        geocodeSearch.setOnGeocodeSearchListener(this);
        aMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                latitude=location.getLatitude();//??????????
                longitude=location.getLongitude();//??????????
                double altitude=location.getAltitude();//????????
                latLng = new LatLonPoint(latitude,longitude);
                RegeocodeQuery query = new RegeocodeQuery(latLng, 200,GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(query);

            }
        });
    }

    /*
    * ???????????????
    * */
    private void Listener(){
        //????????????????????????
        main_fragment_home_listview.setRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (g.size()>0){
                    AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                    d.setTitle("???????????????");
                    d.setMessage("????????????????????????????????????");
                    d.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getOkHttp(spinner_list.get(spinner_position),search_name);
                            close_meun();
                            clear_meun();
                            main_fragment_home_listview.onRefreshComplete();
                        }
                    });
                    d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            main_fragment_home_listview.onRefreshComplete();
                        }
                    });
                    d.show();
                }else {
                    getOkHttp(spinner_list.get(spinner_position),search_name);
                    main_fragment_home_listview.onRefreshComplete();
                    close_meun();
                    clear_meun();
                }
            }

            @Override
            public void onLoadMore() {
            }
        });
        //????????????????????????
        main_fragment_home_txt_notic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinner_list.size()>0){
                    getOkHttp(spinner_list.get(spinner_position),search_name);
                }else {
                    getOkHttp("??????",search_name);
                }
            }
        });
        /*
        * ???????????????????????????
        * ???????????????????????????????????????????????????????????????????????????????????????????????????
        * */
        main_fragment_home_listview_meun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Goods goods = g.get(i);
                final int i1 = goodsList.indexOf(goods);
                Log.d("?????????????????????",i1+"/"+goodsList.size());
                if (i1>=0){
                    //???????????????????????????
                    if (i1==goodsList.size()-1) {
                        main_fragment_home_listview.post(new Runnable() {
                            @Override
                            public void run() {
                                main_fragment_home_listview.smoothScrollToPosition(main_fragment_home_listview.getBottom());
                                main_fragment_home_listview.onRefreshComplete();
                            }
                        });
                    }else {
                        main_fragment_home_listview.post(new Runnable() {
                            @Override
                            public void run() {
                                main_fragment_home_listview.smoothScrollToPosition(i1+1);
                                main_fragment_home_listview.onRefreshComplete();
                            }

                        });
                    }

                }
            }
        });
        //????????????????????????????????????
        main_fragment_home_btn_clearmeun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ???????????????");
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("?????????????????????");
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clear_meun();
                    }
                });
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });
        //?????????????????????????????????
        main_fragment_home_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                Log.d(TAG, "onClick: ????????????");
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
                    String s=g.get(i).getGoods_name()+" \t "+g.get(i).getGoods_price()+"??? ?? "+g.get(i).getNum();
                    strings[i]=s;
                }
                strings[g.size()]="----------------------------";
                strings[g.size()+4]="???????????????"+num+"??????????????????"+pice+"???";
                if (phone!=null) {
                    strings[g.size() + 3] = "???????????????" + phone;
                }else {
                    strings[g.size() + 3] = "??????????????????";
                }
                if (address!=null&&!address.equals("????????????")) {
                    strings[g.size()+2] = "?????????" + address;
                }else {
                    strings[g.size()+2] = "????????????";
                }
                if (name!=null) {
                    strings[g.size()+1] = "????????????" + name;
                }else {
                    strings[g.size()+1] = "???????????????";
                }
                submitGoods.setGoods_list(goodslist);
                submitGoods.setCount_price(pice);
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setTitle("??????????????????");
                builder.setCancelable(false);
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null) {
                            submitGoods.setName(name);
                            submitGoods.setPhone(phone);
                            if (address!=null&&!address.equals("????????????")) {
                                submitGoods.setAddress(address);
                                Date date = new Date();
                                submit_id=TimeUtil.getTimeString(date)+"_"+ UUID.randomUUID().toString().substring(0,8);
                                submitGoods.setTime(TimeUtil.getSystemTime(date));
                                submitGoods.setSubmit_id(submit_id);
                                OkHttp2(submitGoods);
                            }else {
                                DialogUtil.dialog(getContext(),"????????????","?????????\"??????\"-\"????????????\"???????????????,????????????");
                            }
                        }else {
                            DialogUtil.dialog(getContext(),"??????????????????","?????????\"??????\"???????????????,????????????");
                        }
                    }
                });
                builder.setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });
        //????????????????????????????????????
        main_fragment_home_cb_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (num!=0) {
                        main_fragment_home_LL_meun.setVisibility(View.VISIBLE);
                    }else {
                        Toast.makeText(getContext(),"????????????????????????????????????",Toast.LENGTH_SHORT).show();
                        close_meun();
                    }
                }else {
                    main_fragment_home_LL_meun.setVisibility(View.GONE);
                }
            }
        });
        //????????????????????????
        main_fragment_home_LL_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                if (weather_address!=null) {
                    intent.putExtra("address", weather_address);
                    intent.setClass(getContext(), WeatherActivity.class);
                    startActivity(intent);
                }
            }
        });
        /**
         * ?????????????????????????????????
         */
        main_fragment_home_et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                search_name=editable.toString().trim();
            }
        });
        /**
         * ?????????????????????????????????
         */
        main_fragment_home_iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refresh_GoodList(spinner_position,"?????????????????????","????????????????????????????????????????????????????????????");

            }
        });

    }
    //??????????????????????????????????????????
    private void getPreferences(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        if (password!=null) {
            Resuilt resuilt = new Resuilt(password, 3);    //????????????
            password = resuilt.getresuilt();
        }
        address = preferences.getString("address", null);
        Log.d(TAG, "getPreferences: "+phone+name);
    }
    /*
    * ??????????????????
    * */
    private void getOkHttp(String type,String name){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Goods/allgoodslist";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("GoodsType",type)
                .addParams("Name",name)
                .addHeader("User-Agent", GetPhoneInfo.getHead(getContext()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                        progressDialog.dismiss();
                        final AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("????????????");
                        String error = OkHttpMessageUtil.error(e);
                        d.setMessage(error);
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FragmentActivity activity = getActivity();
                                activity.finish();
                            }
                        });
                        d.show();
                        main_fragment_home_listview.setVisibility(View.GONE);
                        setNotice(error+"\n???????????????????????????????????????",500);
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
                                DialogUtil.dialog(getContext(), "??????", e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(), "??????", OkHttpMessageUtil.response(response));
                            }
                        }

                        if (goodsList!=null) {
                            if (goodsList.size()>0) {
                                main_fragment_home_txt_notic.setVisibility(View.GONE);
                                main_fragment_home_listview.setVisibility(View.VISIBLE);
                                for (Goods g : goodsList) {
                                    g.setGoods_image(setting.getMainUrl()
                                            + "fileload/show/" + g.getGoods_image());
                                    g.setNum(0);
                                    if (spinner_list.contains(g.getGoods_type())) {
                                    } else {
                                        spinner_list.add(g.getGoods_type());
                                    }
                                }

                                final BaseAdapter adapter = new GoodsList_Adapter(mContext, goodsList);
                                main_fragment_home_listview.setAdapter(adapter);
                                Toast.makeText(getContext(), "???????????????" + goodsList.size(), Toast.LENGTH_SHORT).show();
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
                                        main_fragment_home_txt_num.setText(String.valueOf(num) + "???");
                                        BigDecimal bg = new BigDecimal(pice);
                                        pice = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                                        main_fragment_home_txt_pice.setText(String.valueOf(pice) + "???");

                                    }
                                });
                            }else {
                                main_fragment_home_listview.setVisibility(View.GONE);
                                setNotice("(????????????)?????????\n??????????????????????????????",404);
                            }
                        }else {
                            main_fragment_home_listview.setVisibility(View.GONE);
                            setNotice("????????????????????????????????????",404);
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
                                    if (spinner_position!=i) {//??????????????????
                                        refresh_GoodList(i,"???????????????????????????","??????????????????????????????????????????????????????");
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

    //??????????????????
    private void start(){
        main_fragment_home_txt_notic.setVisibility(View.GONE);
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_spinner.setVisibility(View.GONE);
        spinner_position=0;
        spinner_list.add("??????");
    }
    //?????????????????????
    private void close_meun(){
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_cb_switch.setChecked(false);
    }
    //?????????????????????
    private void open_meun(){
        main_fragment_home_LL_meun.setVisibility(View.VISIBLE);
        main_fragment_home_cb_switch.setChecked(true);
    }
    //?????????????????????
    private void clear_meun(){
        GoodsList_Adapter adapter=new GoodsList_Adapter(mContext,goodsList);
        adapter.clear_num();
        num=0;
        pice=0;
        main_fragment_home_txt_num.setText(String.valueOf(num)+"???");
        main_fragment_home_txt_pice.setText(String.valueOf(pice)+"???");
        main_fragment_home_LL_meun.setVisibility(View.GONE);
        main_fragment_home_cb_switch.setChecked(false);
        g.clear();
    }
    //??????????????????
    private void refresh_GoodList(int i,String title,String msg){
        if (g.size()>0){
            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
            d.setTitle(title);
            d.setMessage(msg);
            d.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getOkHttp(spinner_list.get(spinner_position),search_name);
                    close_meun();
                    clear_meun();
                }
            });
            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            d.show();
        }else {
            getOkHttp(spinner_list.get(i),search_name);
            close_meun();
            clear_meun();
        }
    }
    //????????????????????????
    private void OkHttp2(SubmitGoods submitGoods){
        final ProgressDialog progressDialog1 = new ProgressDialog(getContext());
        progressDialog1.setMessage("????????????????????????");
        progressDialog1.setIndeterminate(true);
        progressDialog1.setCancelable(false);
        progressDialog1.show();
        String url=setting.getMainUrl()+"Goods/submitgoodslist";
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
                        d.setTitle("??????????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                        if (response!=null) {
                            Message msg = new Gson().fromJson(response, Message.class);
                            progressDialog1.dismiss();
                            if (msg.getCode()==200) {
                                AlertDialog.Builder d = new AlertDialog.Builder(getContext());
                                d.setTitle("??????????????????");
                                d.setMessage("???????????????" + submit_id + "\t\n???????????????" + pice + "???" + "\t\n?????????\"??????\"-\"????????????\"??????????????????");
                                d.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        clear_meun();
                                        getOkHttp(spinner_list.get(spinner_position),search_name);
                                    }
                                });
                                d.show();
                            } else {
                                DialogUtil.dialog(getContext(),msg.getTitle(),msg.getMessage());
                            }
                        }else {
                            DialogUtil.dialog(getContext(),"??????null","???????????????????????????");
                        }
                    }
                });
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

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String city = regeocodeAddress.getCity();//??????
        main_fragment_home_txt_city.setText(city);
        Log.e(TAG, "onRegeocodeSearched: "+city );
//??????????????????????????????????????????????????????WEATHER_TYPE_LIVE??????????????????WEATHER_TYPE_FORECAST
        mquery = new WeatherSearchQuery(city, WeatherSearchQuery.WEATHER_TYPE_LIVE);
        mweathersearch=new WeatherSearch(getContext());
        mweathersearch.setOnWeatherSearchListener(this);
        mweathersearch.setQuery(mquery);
        mweathersearch.searchWeatherAsyn(); //????????????
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

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
                String wind=liveResult.getWindDirection()+"???("+liveResult.getWindPower()+"???)";
                String humidity = "?????????"+liveResult.getHumidity()+"%";
                String adCode = liveResult.getWeather();
                Log.e(TAG, "onWeatherLiveSearched: "+city);

                weather_address=city;
                main_fragment_home_txt_temp.setText(weather+" "+temperature);
            }
        }else {
            if (i==1200){
                if (flag1) {
                    Toast.makeText(getContext(), "?????????????????????????????????", Toast.LENGTH_SHORT).show();
                    DialogUtil.dialog(getContext(),"??????????????????","????????????????????????????????????");
                    flag1=!flag1;
                }
            }else {
                if (flag2) {
                    Toast.makeText(getContext(), "???????????????" + i, Toast.LENGTH_SHORT).show();
                    DialogUtil.dialog(getContext(),"??????????????????","??????????????????"+i);
                    flag2=!flag2;
                }
            }
        }
    }

    @Override
    public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {

    }

    private void setNotice(String msg,int state){
        main_fragment_home_txt_notic.setText(msg);
        switch (state){
            case 200:
                main_fragment_home_txt_notic.setTextColor(getResources().getColor(R.color.skyblue));
                break;
            case 404:
                main_fragment_home_txt_notic.setTextColor(getResources().getColor(R.color.yellow));
                break;
            case 500:
                main_fragment_home_txt_notic.setTextColor(getResources().getColor(R.color.red));
                break;
                default:
                    main_fragment_home_txt_notic.setTextColor(getResources().getColor(R.color.black_80));
                    break;
        }
        main_fragment_home_txt_notic.setVisibility(View.VISIBLE);
    }

}
