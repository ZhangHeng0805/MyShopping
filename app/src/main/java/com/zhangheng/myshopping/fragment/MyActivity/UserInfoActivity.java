package com.zhangheng.myshopping.fragment.MyActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.bean.Result;
import com.zhangheng.myshopping.bean.shopping.Customer;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.zh.ASCII;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class UserInfoActivity extends Activity {

    private List<String> iconlist;
    private Customer customer;
    private Spinner userinfo_sp_usericon;
    private TextView userinfo_txt_username,userinfo_txt_time;
    private String icon,phone,password,address,name;
    private SharedPreferences preferences;
    private ImageView login_iv_back;
    private RelativeLayout userinfo_RL_password,userinfo_RL_userusername;
    private Button userinfo_btn_usericon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_myfragment_activity_user_info);

        userinfo_sp_usericon=findViewById(R.id.userinfo_sp_usericon);
        userinfo_txt_username=findViewById(R.id.userinfo_txt_username);
        userinfo_txt_time=findViewById(R.id.userinfo_txt_time);
        login_iv_back=findViewById(R.id.login_iv_back);
        userinfo_RL_password=findViewById(R.id.userinfo_RL_password);
        userinfo_RL_userusername=findViewById(R.id.userinfo_RL_userusername);
        userinfo_btn_usericon=findViewById(R.id.userinfo_btn_usericon);

        getPreferences();
        getImage();
        Listener();
    }


    private void Listener(){
        //返回图标
        login_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //用户名
        userinfo_RL_userusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(UserInfoActivity.this);
                alert.setTitle("修改用户名");
                alert.setMessage("请输入新的用户名:");
                alert.setCancelable(false);
                final EditText input = new EditText(UserInfoActivity.this);
                input.setHint("请输入新的用户名：");
                alert.setView(input);
                alert.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        if (value.length()>0&&value.length()<=10) {
                            if (!value.equals(name)) {
                                Log.d("修改用户名", "onClick: " + value);
                                setUserName(value);
                            }else {
                                dialog("用户名相同","新的用户名不能和旧的用户名相同");
                            }
                        }else {
                            dialog("输入限制","用户名不能为空，且长度限制在10字符内");
                        }
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                });
                alert.show();
            }
        });
        //头像选择
        userinfo_sp_usericon.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               icon = iconlist.get(i);
               if (customer!=null) {
                   if (customer.getIcon().equals(icon)) {
                       userinfo_btn_usericon.setVisibility(View.GONE);
                   } else {
                       userinfo_btn_usericon.setVisibility(View.VISIBLE);
                   }
               }else {
                   userinfo_btn_usericon.setVisibility(View.GONE);
               }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //修改头像的按钮
        userinfo_btn_usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("修改头像", "onItemSelected: "+icon);
                final AlertDialog.Builder alert = new AlertDialog.Builder(UserInfoActivity.this);
                alert.setTitle("修改头像");
                alert.setMessage("是否选择此头像作为你的新头像？");
                alert.setCancelable(false);
                alert.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        setIcon(icon);
                    }
                });
                alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
                alert.show();
            }
        });
        userinfo_RL_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPreferences();
                if (phone!=null&&password!=null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
                    final AlertDialog dialog = builder.create();
                    View dialogView = View.inflate(UserInfoActivity.this, R.layout.item_update_password, null);
                    dialog.setView(dialogView);
                    dialog.show();

                    final EditText et_old_pwd = dialogView.findViewById(R.id.et_old_password);
                    final EditText et_new_pwd = dialogView.findViewById(R.id.et_new_password);
                    final EditText et_new_pwd1 = dialogView.findViewById(R.id.et_new_password1);

                    final Button btn_submit = dialogView.findViewById(R.id.btn_login);
                    final Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);

                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String old_pwd = et_old_pwd.getText().toString();
                            String new_pwd = et_new_pwd.getText().toString();
                            String new_pwd1 = et_new_pwd1.getText().toString();
                            if (TextUtils.isEmpty(old_pwd) || TextUtils.isEmpty(new_pwd)||TextUtils.isEmpty(new_pwd1)) {
                                Toast.makeText(UserInfoActivity.this, "输入框不能为空!", Toast.LENGTH_SHORT).show();
                            }else {
                                if (new_pwd.length()>=6&&new_pwd1.length()<=18) {
                                    if (new_pwd.equals(new_pwd1)) {
                                        if (!old_pwd.equals(new_pwd)) {
                                            if (old_pwd.equals(password)) {
                                                setPassword(new_pwd);
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(UserInfoActivity.this, "旧密码输入错误", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(UserInfoActivity.this, "新旧密码不能相同", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(UserInfoActivity.this, "新密码两次输入不一致", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(UserInfoActivity.this, "新密码长度限制6~18位", Toast.LENGTH_SHORT).show();
                                }
                            }

//                            Toast.makeText(UserInfoActivity.this, "", Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                }else {
                    DialogUtil.dialog(UserInfoActivity.this,"没有登录","请登录后再来操作");
                }
            }
        });

    }
    //获取存储在手机里面的账户信息
    private void getPreferences(){
        preferences = getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        password = preferences.getString("password", null);
        if (password!=null) {
            Resuilt resuilt = new Resuilt(password, 3);    //密码解密
            password = resuilt.getresuilt();
        }
        address = preferences.getString("address", null);
    }

    private void getUser(){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("刷新中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/getCustomer";
        Map<String,String> map=new HashMap<>();
        map.put("username",phone);
        map.put("password",password);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
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

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        try {
                            customer = gson.fromJson(response, Customer.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (customer!=null){
                            int i = iconlist.indexOf(customer.getIcon());
                            if (i>=0){
                                userinfo_sp_usericon.setSelection(i);
                            }
                            userinfo_txt_time.setText(customer.getTime().substring(0,customer.getTime().indexOf(" ")));
                            userinfo_txt_username.setText(customer.getUsername());

                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
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
    private void getImage(){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("刷新中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"RegisterCustomer/customericonlist";
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        AlertDialog.Builder builder=new AlertDialog.Builder(UserInfoActivity.this);
                        builder.setCancelable(false);
                        builder.setTitle("加载失败");
                        builder.setMessage("错误："+error);
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
                        progressDialog.dismiss();
                        if (iconlist!=null) {
                            getUser();
                            for (String s : iconlist) {
                                data.add(getResources().getString(R.string.zhangheng_url) + "downloads/show/" + s);
                            }

                            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), data);
                            userinfo_sp_usericon.setAdapter(imageAdapter);
                            icon = iconlist.get(0);

                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(UserInfoActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("加载失败");
                            builder.setMessage("无法进行加载头像");
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
    private void setUserName(String username){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("修改中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/updateUsername";
        Map<String,String> map=new HashMap<>();
        map.put("username",phone);
        map.put("password",username);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
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

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Result resuilt = new Result();
                        try {
                            resuilt = gson.fromJson(response, Result.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                           if (resuilt.getTitle().equals("用户名修改成功")){
                               dialog(resuilt.getTitle(),"恭喜 "+resuilt.getMessage()+" 喜得新名字！");
                               SharedPreferences sharedPreferences=getSharedPreferences("customeruser", MODE_PRIVATE);
                               SharedPreferences.Editor editor=sharedPreferences.edit();
                               editor.putString("name",resuilt.getMessage());
                               editor.commit();
                               getPreferences();
                           }else {
                               dialog(resuilt.getTitle(),resuilt.getMessage());
                           }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
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
    private void setIcon(String myicon){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("修改中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/updateIcon";
        Map<String,String> map=new HashMap<>();
        map.put("username",phone);
        map.put("password",myicon);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
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

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Result resuilt = new Result();
                        try {
                            resuilt = gson.fromJson(response, Result.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                            if (resuilt.getTitle().equals("头像修改成功")){
                                dialog(resuilt.getTitle(),"恭喜 "+name+" 喜得新头像！");
                                userinfo_sp_usericon.setSelection(iconlist.indexOf(resuilt.getMessage()));
                                userinfo_btn_usericon.setVisibility(View.GONE);
                            }else {
                                dialog(resuilt.getTitle(),resuilt.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
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
    private void setPassword(String mypassword){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("修改中。。。");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=getResources().getString(R.string.zhangheng_url)+"Customer/updatePassWord";
        Map<String,String> map=new HashMap<>();
        map.put("username",phone);
        map.put("password",mypassword);
        OkHttpUtils
                .post()
                .url(url)
                .params(map)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("加载失败");
                        d.setMessage(OkHttpMessageUtil.error(e));
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

                    @Override
                    public void onResponse(String response, int id) {
                        Gson gson = new Gson();
                        Result resuilt = new Result();
                        try {
                            resuilt = gson.fromJson(response, Result.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"错误",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"错误",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (resuilt!=null){
                            if (resuilt.getTitle().equals("密码修改成功")){
                                dialog("密码修改成功","请牢记新的密码！");
                                SharedPreferences sharedPreferences=getSharedPreferences("customeruser", MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                ASCII ascii = new ASCII(resuilt.getMessage(), 3);
                                editor.putString("password",ascii.getresuilt());
                                editor.commit();
                                getPreferences();
                            }else {
                                dialog(resuilt.getTitle(),resuilt.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("网络加载失败");
                            d.setMessage("");
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
    /*
     * 头像下拉选择的适配器
     * */
    class ImageAdapter extends BaseAdapter {
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

    private void dialog(String title,String message){
        AlertDialog.Builder d=new AlertDialog.Builder(UserInfoActivity.this);
        d.setTitle(title);
        d.setMessage(message);
        d.create().show();
    }
}
