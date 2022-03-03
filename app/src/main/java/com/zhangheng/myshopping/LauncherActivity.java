package com.zhangheng.myshopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhangheng.myshopping.setting.ServerSetting;

import java.util.Timer;
import java.util.TimerTask;

public class LauncherActivity extends Activity {
    private Button launcher_btn_exit;
    private ServerSetting setting;
    private int i=4;//倒计时为i-1秒
    private TextView launcher_txt_title,launcher_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        setting=new ServerSetting(this);
        launcher_btn_exit=findViewById(R.id.launcher_btn_exit);
        launcher_txt_title=findViewById(R.id.launcher_txt_title);
        launcher_server=findViewById(R.id.launcher_server);
        launcher_server.setText(setting.getMainUrl());
//        launcher_txt_title.setTypeface(FontType.getFont(getApplicationContext(),"assets/fonts/cat.ttf"));
        countDown();
    }
    private void countDown(){

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //在主线程中执行
                Intent intent = new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        handler.postDelayed(runnable, (i-1)*1000);

        Timer timer=new Timer();
        TimerTask task=new TimerTask() {
            @Override
            public void run() {
                LauncherActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        launcher_btn_exit.setText("跳转 "+String.valueOf(i));
                    }
                });
                i--;
            }
        };
        timer.schedule(task,0,1000);

        launcher_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LauncherActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                handler.removeCallbacks(runnable);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK) {
                return true;
        }else if (keyCode==KeyEvent.KEYCODE_MOVE_HOME){
            return true;
        }else if (keyCode==KeyEvent.KEYCODE_HOME){
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
