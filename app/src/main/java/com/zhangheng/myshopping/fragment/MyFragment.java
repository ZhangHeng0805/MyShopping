package com.zhangheng.myshopping.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.base.BaseFragment;
import com.zhangheng.myshopping.bean.Const;
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.bean.shopping.Customer;
import com.zhangheng.myshopping.fragment.MyActivity.Location_Activity;
import com.zhangheng.myshopping.fragment.MyActivity.Login_Activity;
import com.zhangheng.myshopping.fragment.MyActivity.OrderActivity;
import com.zhangheng.myshopping.fragment.MyActivity.SettingActivity;
import com.zhangheng.myshopping.fragment.MyActivity.UserInfoActivity;
import com.zhangheng.myshopping.getphoneMessage.PhoneSystem;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.EncryptUtil;
import com.zhangheng.myshopping.util.GetPhoneInfo;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static android.content.Context.MODE_PRIVATE;

public class MyFragment extends BaseFragment {
    private RelativeLayout main_fragment_my_RL_user;
    private static final String TAG= MyFragment.class.getSimpleName();
    private TextView main_fragment_my_txt_username,main_fragment_my_txt_useraddress,main_fragment_my_txt_phone;
    private ImageView main_fragment_my_iv_usericon,main_fragment_my_iv_setting;
    private SharedPreferences preferences;
    private String phone,name,password,address,versionCode;
    private Button main_fragment_my_btn_exit;
    private ListView main_fragment_my_listview;
    private ServerSetting setting;

    @Override
    protected View initView() {
        Log.e(TAG,"????????????Fragment?????????????????????");
        setting=new ServerSetting(getContext());
        View view = View.inflate(mContext, R.layout.main_fragment_my, null);
        main_fragment_my_RL_user=view.findViewById(R.id.main_fragment_my_RL_user);
        main_fragment_my_txt_username=view.findViewById(R.id.main_fragment_my_txt_username);
        main_fragment_my_txt_phone=view.findViewById(R.id.main_fragment_my_txt_phone);
        main_fragment_my_txt_useraddress=view.findViewById(R.id.main_fragment_my_txt_useraddress);
        main_fragment_my_iv_usericon=view.findViewById(R.id.main_fragment_my_iv_usericon);
        main_fragment_my_iv_setting=view.findViewById(R.id.main_fragment_my_iv_setting);
        main_fragment_my_btn_exit=view.findViewById(R.id.main_fragment_my_btn_exit);
        main_fragment_my_listview=view.findViewById(R.id.main_fragment_my_listview);
        Listener();
        return view;
    }
    @Override
    protected void initData() {
        Log.e(TAG,"????????????Fragment?????????????????????");
        super.initData();
        versionCode=PhoneSystem.getVersionCode(getContext());
        ListAdapter listAdapter = new ListAdapter(getContext());
        main_fragment_my_listview.setAdapter(listAdapter);
        start();
    }

    @Override
    public void onResume() {
        super.onResume();
        start();
    }

    private void Listener() {
        main_fragment_my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null){
                            Intent intent = new Intent(getContext(), Location_Activity.class);
                            startActivity(intent);
                        }else {
                            DialogUtil.dialog(getContext(),"?????????????????????","??????????????????????????????????????????");
                        }
                        break;
                    case 1:
                        String html=setting.getMainUrl()+"regist_merchantsPage";
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(html));
                        startActivity(intent);
                        break;
                    case 2:
                        getPreferences();
                        if (phone!=null&&name!=null&&password!=null){
                            Intent intent2 = new Intent(getContext(), OrderActivity.class);
                            startActivity(intent2);
                        }else {
                            DialogUtil.dialog(getContext(),"?????????????????????","??????????????????????????????????????????");
                        }
                        break;
                    case 3://????????????
                        String html1= Const.contact_url;
                        Intent intent1=new Intent();
                        intent1.setAction(Intent.ACTION_VIEW);
                        intent1.setData(Uri.parse(html1));
                        startActivity(intent1);
                        break;
                    case 4:
                        getupdatelist();
                        break;
                }
            }
        });
        main_fragment_my_RL_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                if (phone!=null&&name!=null&&password!=null){
                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(getActivity(), Login_Activity.class);
                    startActivity(intent);
                }
            }
        });
        main_fragment_my_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPreferences();
            }
        });
        main_fragment_my_iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingActivity.class);
                startActivity(intent);
            }
        });
    }
    //????????????
    public void getupdatelist(){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("????????????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        SharedPreferences preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        String name = preferences.getString("name", null);
        if (name!=null){
            name="?????????:"+name;
        }else {
            name="";
        }
        String url=setting.getMainUrl()
                +"fileload/updatelist/"+getResources().getString(R.string.update_name);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("notice",name)
                .addHeader("User-Agent", GetPhoneInfo.getHead(getContext()))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        Toast.makeText(getContext(),"?????????"+error,Toast.LENGTH_SHORT).show();
                        Log.e("?????????",e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Message msg=new Message();
                        Gson gson=new Gson();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){

                        }
                        progressDialog.dismiss();
                        if (response.indexOf("WEB?????????????????????")<1) {
                            if (msg != null) {
                                if (msg.getCode()==200) {
                                    if (msg.getTitle().equals(getResources().getString(R.string.app_name))) {
                                       SharedPreferences sharedPreferences = getContext().getSharedPreferences("??????update", MODE_PRIVATE);
                                        String urlname = sharedPreferences.getString("urlname", "");
                                        if (!urlname.equals(msg.getMessage())) {
                                            //????????????????????????????????????
                                            if (!versionCode.equals(appversion(msg.getMessage()))) {
                                                showUpdate(msg.getMessage());
                                            }else {
                                                DialogUtil.dialog(getContext(),"????????????????????????????????????","???????????????"+msg.getMessage().substring(urlname.indexOf("_")+1,urlname.lastIndexOf(".")));
                                            }
                                        } else {
                                            Log.d("????????????", "urlname?????????????????????");
                                            DialogUtil.dialog(getContext(),"???????????????????????????","????????????????????????["+urlname.substring(urlname.indexOf("_")+1,urlname.lastIndexOf("."))+"]???????????????");

                                        }
                                    } else {
                                        Log.d("title", "title???????????????????????????"+msg.getTitle());
                                        DialogUtil.dialog(getContext(),"????????????","??????????????????????????????????????????");

                                    }
                                } else {
                                    Log.d("title", "title???null");
                                    DialogUtil.dialog(getContext(),"????????????",msg.getMessage());
                                }
                            } else {
                                Log.d("resuilt", "resuilt??????");
                                DialogUtil.dialog(getContext(),"??????","???????????????????????????");
                            }
                        }else {
                           DialogUtil.dialog(getContext(),"??????","WEB?????????????????????");
                        }
                        Log.d("?????????",response);
                    }
                });
    }
    //??????App?????????
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
    //??????????????????
    public void showUpdate(final String name){
        String app=appversion(name);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext())
                .setTitle("??????")
                .setMessage("??????????????????"+app+"?????????????????????????????????????????????" +
                        "\n????????????????????????????????????????????????")
                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent=new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        String url = setting.getMainUrl()
                                +"fileload/"+name;
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setNeutralButton("???????????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AlertDialog.Builder builder1=new AlertDialog.Builder(getContext())
                                .setTitle("??????")
                                .setMessage("?????????????????????<????????????>????????????????????????????????????????????????????????????????????????")
                                .setPositiveButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences sharedPreferences=getContext().getSharedPreferences("??????update",MODE_PRIVATE);
                                        SharedPreferences.Editor editor=sharedPreferences.edit();
                                        editor.putString("urlname",name);
                                        editor.apply();
                                    }
                                })
                                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
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

    //??????????????????
    private void getCustomer(Customer customer){
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/getCustomer";
        String json=new Gson().toJson(customer);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("customerJson",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                        d.setTitle("????????????");
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
                        Gson gson = new Gson();
                        Customer customer = new Customer();
                        try {
                            customer = gson.fromJson(response, Customer.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(getContext(),"??????",e.getMessage());
                            }else {
                                DialogUtil.dialog(getContext(),"??????",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (customer!=null&&customer.getPhone()!=null){
                            String url=setting.getMainUrl()+"fileload/show/"+customer.getIcon();
                            Glide.with(getContext()).load(url).into(main_fragment_my_iv_usericon);
                            main_fragment_my_txt_useraddress.setText(customer.getAddress());
                            SharedPreferences sharedPreferences=getContext().getSharedPreferences("customeruser",MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("address",customer.getAddress());
                            editor.commit();
                        }else {
                            DialogUtil.dialog(getContext(),"????????????","??????????????????????????????????????????");
                            exitState();
                            AlertDialog.Builder d=new AlertDialog.Builder(getContext());
                            d.setTitle("????????????????????????");
                            d.setMessage("");
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

                    }
                });

    }
    private void start(){
        getPreferences();
        if (phone!=null&&name!=null&&password!=null){
            main_fragment_my_btn_exit.setVisibility(View.VISIBLE);
            Customer customer = new Customer();
            customer.setPhone(phone);
            customer.setPassword(EncryptUtil.getMyMd5(password));
            getCustomer(customer);
            main_fragment_my_txt_username.setText(name);
            StringBuffer p=new StringBuffer(phone);
            p=p.replace(3,7,"****");
            main_fragment_my_txt_phone.setText(p);

            main_fragment_my_txt_useraddress.setText(address);
        }else {
            main_fragment_my_btn_exit.setVisibility(View.GONE);
        }
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
    //?????????????????????
    private void clearPreferences(){
        if (name!=null||password!=null) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
            builder1.setTitle("????????????");
            builder1.setMessage("???????????????????????????????????????");
            builder1.setCancelable(false);
            builder1.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exitState();
                }
            });
            builder1.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder1.create().show();

        }else {
            DialogUtil.dialog(getContext(),"????????????","????????????????????????");
            exitState();
        }
    }
    //??????????????????????????????????????????
    private void exitState(){
        preferences = mContext.getSharedPreferences("customeruser", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = preferences.edit();
        editor1.clear();
        editor1.commit();
        main_fragment_my_btn_exit.setVisibility(View.GONE);
        main_fragment_my_iv_usericon.setImageResource(R.drawable.icon);
        main_fragment_my_txt_username.setText("???????????????????????????");
        main_fragment_my_txt_useraddress.setText("????????????");
        main_fragment_my_txt_phone.setText("***");
    }

    //????????????????????????
    class ListAdapter extends BaseAdapter {
        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        private String[] info={
                "????????????",
                "????????????",
                "????????????",
                "????????????",
                "??????????????????"+ PhoneSystem.getVersionCode(getContext()),
        };
        private Integer[] icon={
                R.drawable.location,
                R.drawable.zhuce,
                R.drawable.order,
                R.drawable.telephone,
                R.drawable.find,
        };

        @Override
        public int getCount() {
            return info.length;
        }

        @Override
        public Object getItem(int i) {
            return info.length;
        }

        @Override
        public long getItemId(int i) {
            return info.length;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Hodler hodler;
            if (view==null){
                hodler=new Hodler();
                view=View.inflate(context,R.layout.item_main_myfragment_list,null);
                hodler.iv_icon=view.findViewById(R.id.item_m15_myfragment_iv_icon);
                hodler.txt_info=view.findViewById(R.id.item_m15_myfragment_txt_info);
                view.setTag(hodler);
            }else {
                hodler= (Hodler) view.getTag();
            }
            hodler.iv_icon.setImageResource(icon[i]);
            hodler.txt_info.setText(info[i]);
            return view;
        }
        class Hodler{
            private ImageView iv_icon;
            private TextView txt_info;
        }
    }
}
