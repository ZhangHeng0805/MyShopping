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
import com.zhangheng.myshopping.bean.Message;
import com.zhangheng.myshopping.bean.shopping.Customer;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.DialogUtil;
import com.zhangheng.myshopping.util.EncryptUtil;
import com.zhangheng.myshopping.util.OkHttpMessageUtil;
import com.zhangheng.myshopping.util.TimeUtil;
import com.zhangheng.zh.ASCII;
import com.zhangheng.zh.Resuilt;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;

public class UserInfoActivity extends Activity {

    private List<String> iconlist;
    private Customer customer;
    private Spinner userinfo_sp_usericon;
    private TextView userinfo_txt_username,userinfo_txt_time;
    private String icon,phone,password,address,name,new_pwd;
    private SharedPreferences preferences;
    private ImageView login_iv_back;
    private RelativeLayout userinfo_RL_password,userinfo_RL_userusername;
    private Button userinfo_btn_usericon;
    private ServerSetting setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_myfragment_activity_user_info);

        setting=new ServerSetting(this);
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
        //????????????
        login_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //?????????
        userinfo_RL_userusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name!=null) {
                    final AlertDialog.Builder alert = new AlertDialog.Builder(UserInfoActivity.this);
                    final AlertDialog dialog = alert.create();
                    View dialogView = View.inflate(UserInfoActivity.this, R.layout.item_update_username, null);
                    dialog.setView(dialogView);
                    dialog.show();

                    final EditText et_new_username = dialogView.findViewById(R.id.et_new_username);
                    et_new_username.setText(name);

                    final Button btn_submit = dialogView.findViewById(R.id.btn_update_username_submint);
                    final Button btn_cancel = dialogView.findViewById(R.id.btn_update_username_cancel);

                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String value = et_new_username.getText().toString();
                            if (value.length() > 0 && value.length() <= 10) {
                                if (!value.equals(name)) {
                                    Log.d("???????????????", "onClick: " + value);
                                    Customer customer = new Customer();
                                    customer.setPhone(phone);
                                    customer.setPassword(EncryptUtil.getMyMd5(password));
                                    customer.setUsername(value);
                                    setUserName(customer);//???????????????
                                    dialog.dismiss();
                                } else {
                                    DialogUtil.dialog(UserInfoActivity.this,"???????????????", "?????????????????????????????????????????????");
                                }
                            } else {
                                DialogUtil.dialog(UserInfoActivity.this,"????????????", "??????????????????????????????????????????10?????????");
                            }
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    DialogUtil.dialog(UserInfoActivity.this,"????????????","????????????????????????");
                }
            }
        });
        //????????????
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
        //?????????????????????
        userinfo_btn_usericon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("????????????", "onItemSelected: "+icon);
                final AlertDialog.Builder alert = new AlertDialog.Builder(UserInfoActivity.this);
                alert.setTitle("????????????");
                alert.setMessage("?????????????????????????????????????????????");
                alert.setCancelable(false);
                alert.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Customer customer = new Customer();
                        customer.setPhone(phone);
                        customer.setPassword(EncryptUtil.getMyMd5(password));
                        customer.setIcon(icon);
                        setIcon(customer);//????????????
                    }
                });
                alert.setNegativeButton("??????", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub

                    }
                });
                alert.show();
            }
        });
        //????????????
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
                            new_pwd = et_new_pwd.getText().toString();
                            String new_pwd1 = et_new_pwd1.getText().toString();
                            if (TextUtils.isEmpty(old_pwd) || TextUtils.isEmpty(new_pwd)||TextUtils.isEmpty(new_pwd1)) {
                                Toast.makeText(UserInfoActivity.this, "?????????????????????!", Toast.LENGTH_SHORT).show();
                            }else {
                                if (new_pwd.length()>=6&&new_pwd1.length()<=18) {
                                    if (new_pwd.equals(new_pwd1)) {
                                        if (!old_pwd.equals(new_pwd)) {
                                            if (old_pwd.equals(password)) {
                                                Customer customer = new Customer();
                                                customer.setPhone(phone);
                                                customer.setPassword(EncryptUtil.getMyMd5(new_pwd));//???????????????
                                                customer.setUsername(name);
                                                setPassword(customer);
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(UserInfoActivity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                                            }
                                        }else {
                                            Toast.makeText(UserInfoActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(UserInfoActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(UserInfoActivity.this, "?????????????????????6~18???", Toast.LENGTH_SHORT).show();
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
                    DialogUtil.dialog(UserInfoActivity.this,"????????????","????????????????????????");
                }
            }
        });

    }
    //??????????????????????????????????????????
    private void getPreferences(){
        preferences = getSharedPreferences("customeruser", MODE_PRIVATE);
        phone = preferences.getString("phone", null);
        name=preferences.getString("name",null);
        userinfo_txt_username.setText(name);
        password = preferences.getString("password", null);
        if (password!=null) {
            Resuilt resuilt = new Resuilt(password, 3);    //????????????
            password = resuilt.getresuilt();
        }
        address = preferences.getString("address", null);
    }

    //??????????????????
    private void getUser(Customer cus){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/getCustomer";
        Gson gson = new Gson();
        String json = gson.toJson(cus);
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
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                        progressDialog.dismiss();
                        if (response!=null) {
                            try {
                                customer = gson.fromJson(response, Customer.class);
                            } catch (Exception e) {
                                if (OkHttpMessageUtil.response(response) == null) {
                                    DialogUtil.dialog(UserInfoActivity.this, "??????", e.getMessage());
                                } else {
                                    DialogUtil.dialog(UserInfoActivity.this, "??????", OkHttpMessageUtil.response(response));
                                }
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("????????????????????????");
                            d.setMessage("");
                            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            });
                            d.show();
                        }
                        if (customer!=null){
                            int i = iconlist.indexOf(customer.getIcon());
                            if (i>=0){
                                userinfo_sp_usericon.setSelection(i);
                            }
                            userinfo_txt_time.setText("????????????"+ TimeUtil.daysDifference(TimeUtil.getSystemTime(new Date()),customer.getTime())+"???");
                            userinfo_txt_username.setText(customer.getUsername());

                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("??????????????????");
                            d.setMessage("");
                            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
    //??????????????????
    private void getImage(){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/customericonlist";
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
                        builder.setTitle("????????????");
                        builder.setMessage("?????????"+error);
                        builder.setPositiveButton("??????",new DialogInterface.OnClickListener() {
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
                                Toast.makeText(getApplicationContext(), "??????:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getApplicationContext(), "??????:"+OkHttpMessageUtil.response(response),Toast.LENGTH_SHORT).show();
                            }
                        }
                        List<String> data=new ArrayList<>();
                        progressDialog.dismiss();
                        if (iconlist!=null) {
                            Customer customer = new Customer();
                            customer.setPhone(phone);
                            customer.setPassword(EncryptUtil.getMyMd5(password));
                            getUser(customer);
                            for (String s : iconlist) {
                                data.add(setting.getMainUrl() + "fileload/show/" + s);
                            }

                            ImageAdapter imageAdapter = new ImageAdapter(getApplicationContext(), data);
                            userinfo_sp_usericon.setAdapter(imageAdapter);
                            icon = iconlist.get(0);

                        }else {
                            AlertDialog.Builder builder=new AlertDialog.Builder(UserInfoActivity.this);
                            builder.setCancelable(false);
                            builder.setTitle("????????????");
                            builder.setMessage("????????????????????????");
                            builder.setPositiveButton("??????",new DialogInterface.OnClickListener() {
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
    //???????????????
    private void setUserName(Customer cus){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/updateUsername";
        Gson gson = new Gson();
        String json = gson.toJson(cus);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("customerJSON",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                        Message msg = new Message();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"??????",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"??????",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (msg!=null){
                           if (msg.getCode()==200){
                               dialog(msg.getTitle(),"?????? "+msg.getMessage()+" ??????????????????");
                               SharedPreferences sharedPreferences=getSharedPreferences("customeruser", MODE_PRIVATE);
                               SharedPreferences.Editor editor=sharedPreferences.edit();
                               editor.putString("name",msg.getMessage());
                               editor.commit();
                               getPreferences();
                           }else {
                               dialog(msg.getTitle(),msg.getMessage());
                           }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("??????????????????");
                            d.setMessage("");
                            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
    //??????????????????
    private void setIcon(Customer customer){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/updateIcon";
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        OkHttpUtils
                .post()
                .url(url)
                .addParams("cus",json)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        progressDialog.dismiss();
                        String error = OkHttpMessageUtil.error(e);
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                        Message msg = new Message();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"??????",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"??????",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (msg!=null){
                            if (msg.getCode()==200){
                                dialog(msg.getTitle(),"?????? "+name+" ??????????????????");
                                userinfo_sp_usericon.setSelection(iconlist.indexOf(msg.getMessage()));
                                userinfo_btn_usericon.setVisibility(View.GONE);
                            }else {
                                dialog(msg.getTitle(),msg.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("??????????????????");
                            d.setMessage("");
                            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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

    //??????????????????
    private void setPassword(Customer customer){
        final ProgressDialog progressDialog=new ProgressDialog(UserInfoActivity.this);
        progressDialog.setMessage("??????????????????");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        String url=setting.getMainUrl()+"Customer/updatePassWord";
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
                        android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                        d.setTitle("????????????");
                        d.setMessage(OkHttpMessageUtil.error(e));
                        d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                        Message msg = new Message();
                        try {
                            msg = gson.fromJson(response, Message.class);
                        }catch (Exception e){
                            if (OkHttpMessageUtil.response(response)==null){
                                DialogUtil.dialog(UserInfoActivity.this,"??????",e.getMessage());
                            }else {
                                DialogUtil.dialog(UserInfoActivity.this,"??????",OkHttpMessageUtil.response(response));
                            }
                        }
                        progressDialog.dismiss();
                        if (msg!=null){
                            if (msg.getCode()==200){
                                android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                                d.setTitle("??????????????????");
                                d.setCancelable(false);
                                d.setMessage("???????????????????????????????????????????????????");
                                d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                d.show();
                                SharedPreferences sharedPreferences=getSharedPreferences("customeruser", MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                ASCII ascii = new ASCII(new_pwd, 3);
                                editor.putString("password",ascii.getresuilt());
                                editor.commit();

                            }else {
                                dialog(msg.getTitle(),msg.getMessage());
                            }
                        }else {
                            android.app.AlertDialog.Builder d=new android.app.AlertDialog.Builder(UserInfoActivity.this);
                            d.setTitle("??????????????????");
                            d.setMessage("");
                            d.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
     * ??????????????????????????????
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
