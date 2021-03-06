package com.zhangheng.myshopping.fragment.MyActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.zhangheng.myshopping.R;
import com.zhangheng.myshopping.setting.ServerSetting;
import com.zhangheng.myshopping.util.DialogUtil;

public class SettingActivity extends Activity {

    private ImageView setting_iv_back;
    private RelativeLayout setting_RL_server;
    private ServerSetting setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setting=new ServerSetting(this);
        initControl();

        Listener();

    }

    /**
     * 初始化控件
     */
    private void initControl(){
        setting_iv_back=findViewById(R.id.setting_iv_back);
        setting_RL_server=findViewById(R.id.setting_RL_server);
    }

    /**
     * 监听事件
     */
    private void Listener(){
        //返回图标
        setting_iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //服务器设置
        setting_RL_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
                final AlertDialog dialog=builder.create();
                View dialogView = View.inflate(SettingActivity.this, R.layout.item_update_server, null);
                dialog.setView(dialogView);


                final EditText mainUrl=dialogView.findViewById(R.id.et_update_server_main_url);
                final EditText chatPort=dialogView.findViewById(R.id.et_update_server_chat_port);

                mainUrl.setText(setting.getMainUrl());
                chatPort.setText(setting.getChatPort());

                Button submit=dialogView.findViewById(R.id.btn_update_server_submit);
                Button cancel=dialogView.findViewById(R.id.btn_update_server_cancel);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String main_url = mainUrl.getText().toString();
                        String chat_port = chatPort.getText().toString();
                        if (main_url.length()>0&&chat_port.length()>0){
                            if (!setting.getMainUrl().equals(main_url)||!setting.getChatPort().equals(chat_port)){
                                if (setting.setMainUrl(main_url)){
                                    if (setting.setChatPort(chat_port)){
                                        DialogUtil.dialog(SettingActivity.this,"服务器配置设置成功","请重启App使设置生效!");
                                        dialog.dismiss();;
                                    }else {
                                        Toast.makeText(getApplicationContext(),"聊天服务查询端口设置失败！",Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(getApplicationContext(),"服务器地址设置失败！",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext(),"请先修改内容！",Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(getApplicationContext(),"输入框不能为空！",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }
}
