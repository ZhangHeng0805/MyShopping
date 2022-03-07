package com.zhangheng.myshopping.fragment.MyActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.bean.shopping.Customer;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.EncryptUtil;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.myshopping.util.PhoneNumUtil;
import com.zhangheng.myshopping.util.TimeUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class Registered_Activity extends Activity {
    private static final String TAG=Registered_Activity.class.getSimpleName();
    private Spinner registered_sp_usericon;
    private EditText registered_et_password1
            ,registered_et_password,registered_et_username
            ,registered_et_phone;
    private ImageView registered_iv_back;
    private RadioGroup registered_RG_sex;
    private List<String> iconlist;
    private Button m15_myfragment_login_btn_submit;
    private String sex="男",icon;
    private ServerSetting setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_myfragment_activity_registered);
        setting=new ServerSetting(this);
        registered_iv_back=findViewById(R.id.registered_iv_back);
        registered_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        registered_sp_usericon=findViewById(R.id.registered_sp_usericon);
        registered_et_password1=findViewById(R.id.registered_et_password1);
        registered_et_password=findViewById(R.id.registered_et_password);
        registered_et_username=findViewById(R.id.registered_et_username);
        registered_et_phone=findViewById(R.id.registered_et_phone);
        registered_RG_sex=findViewById(R.id.registered_RG_sex);
        registered_RG_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d(TAG, "onCheckedChanged: "+i);
                switch (i){
                    case R.id.registered_rb_boy:
                        sex="男";
                        break;
                    case R.id.registered_rb_gilr:
                        sex="女";
                        break;
                }
                Log.d(TAG, "onCheckedChanged: "+sex);
            }
        });
        m15_myfragment_login_btn_submit=findViewById(R.id.m15_myfragment_login_btn_submit);
        m15_myfragment_login_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=registered_et_phone.getText().toString();
                String username=registered_et_username.getText().toString();
                String password=registered_et_password.getText().toString();
                String password1=registered_et_password1.getText().toString();
                if (phone.length()==11){
                    if (PhoneNumUtil.isMobile(phone)) {
                        if (username.length() > 0 && username.length() <= 10) {
                            if (password.length() >= 6 && password.length() <= 18) {
                                if (password.equals(password1)) {
                                    Customer customer = new Customer();
                                    customer.setPhone(phone);
                                    customer.setUsername(username);
                                    customer.setPassword(EncryptUtil.getMyMd5(password));//使用自制的Md5加密
                                    customer.setSex(sex);
                                    customer.setIcon(icon);
                                    customer.setTime(TimeUtil.getSystemTime(new Date()));
                                    customer.setAddress("地址为空");
                                    customer.setState(0);
//                                    Log.d(TAG, "onClick: " + customer.toString());
                                    submit(customer);
                                } else {
                                    dialog("输入错误", "两次密码输入不一致");
                                    registered_et_password1.setText("");
                                }
                            } else {
                                dialog("输入错误", "密码不能为空，且限制6~18个字符以内");
                            }
                        } else {
                            dialog("输入错误", "用户名不能为空，且限制10个字符以内");
                        }
                    }else {
                        dialog("输入错误", "非法手机号，请输入正确的手机号码");
                    }
                }else {
                    dialog("输入错误","请输入11位格式的手机号");
                }
            }
        });
        getImage();

    }

    //注册提交
    private void submit(Customer customer){
        final ProgressDialog progressDialog=new ProgressDialog(Registered_Activity.this);
        progressDialog.setMessage("注册中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/register";
        Gson gson = new Gson();
        String json = gson.toJson(customer);
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
                        dialog("注册错误",error);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Message msg=new Message();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                dialog("错误",e.getMessage());
                            }else {
                                dialog("错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (msg!=null) {
                            if (msg.getCode()==200){
                                AlertDialog.Builder builder=new AlertDialog.Builder(Registered_Activity.this);
                                builder.setCancelable(false);
                                builder.setTitle(msg.getTitle());
                                builder.setMessage(msg.getMessage());
                                builder.setPositiveButton("去登录",new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                builder.create().show();
                            }else {
                                dialog(msg.getTitle(), msg.getMessage());
                            }
                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(Registered_Activity.this);
                            builder.setCancelable(false);
                            builder.setTitle("注册失败");
                            builder.setMessage("无法进行注册");
                            builder.setPositiveButton("退出",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
    }
    //获取顾客头像集合
    private void getImage(){
        String url=setting.getMainUrl()+"Customer/customericonlist";

        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String error = OkHttpMessageUtil.error(e);
                        Log.e(TAG, "onError: "+e.getMessage() );
                        AlertDialog.Builder builder=new AlertDialog.Builder(Registered_Activity.this);
                        builder.setCancelable(false);
                        builder.setTitle("加载失败");
                        builder.setMessage(error+"，无法进行注册");
                        builder.setPositiveButton("退出",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        builder.create().show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        try {
                            iconlist = gson.fromJson(response, new TypeToken<List<String>>() {
                            }.getType());
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                Toast.makeText(getApplicationContext(), "错误:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "错误:"+OkHttpMessageUtil.response(response),Toast.LENGTH_SHORT).show();
                            }
                        }

                        List<String> data=new ArrayList<>();
                        if (iconlist!=null) {
                            for (String s : iconlist) {
                                data.add(setting.getMainUrl() + "fileload/show/" + s);
                            }
                            Log.d(TAG, "onResponse: " + data.size());
                            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), data);
                            registered_sp_usericon.setAdapter(imageAdapter);
                            icon = iconlist.get(0);
                            registered_sp_usericon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    icon = iconlist.get(i);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });
                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(Registered_Activity.this);
                            builder.setCancelable(false);
                            builder.setTitle("加载失败");
                            builder.setMessage("无法进行注册");
                            builder.setPositiveButton("退出",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
    }

    private void dialog(String title,String message){
        AlertDialog.Builder d=new AlertDialog.Builder(Registered_Activity.this);
        d.setTitle(title);
        d.setMessage(message);
        d.create().show();
    }

    /*
    * 头像下拉选择的适配器
    * */
    class ImageAdapter extends BaseAdapter{
        private Context context;
        private List<String> data;
        private Holder holder;

        public ImageAdapter(Context context, List<String> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return data.size();
        }

        @Override
        public long getItemId(int i) {
            return data.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            String s = data.get(i);

            if (view==null){
                holder=new Holder();
                view = View.inflate(context, R.layout.item_list_image, null);
                holder.imageView=view.findViewById(R.id.item_image);
                view.setTag(holder);
            }else {
                holder= (Holder) view.getTag();
            }
            Glide.with(context).load(s).into(holder.imageView);
            return view;
        }
        class Holder{
            private ImageView imageView;
        }
    }
}
