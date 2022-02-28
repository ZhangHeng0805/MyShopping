package com.zhangheng.myshopping.setting;

import android.content.Context;
import android.content.SharedPreferences;

import com.zhangheng.myshopping.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * 服务器设置
 */
public class ServerSetting {
    private Context context;
    private SharedPreferences preferences;


    public ServerSetting(Context context) {
        this.context = context;
        preferences= context.getSharedPreferences("Server_Setting", MODE_PRIVATE);
    }

    /**
     * 设置服务地址
     * @param main_url
     * @return
     */
    public boolean setMainUrl(String main_url){
        if (main_url==null){
            main_url=context.getResources().getString(R.string.zhangheng_url);
        }
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("main_url",main_url);
        boolean commit = editor.commit();
        return commit;
    }

    /**
     * 设置服务器地址
     * @param chat_port
     * @return
     */
    public boolean setChatPort(String chat_port){
        if (chat_port==null){
            chat_port=context.getResources().getString(R.string.chat_local_port);
        }
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("chat_port",chat_port);
        boolean commit = editor.commit();
        return commit;
    }

    /**
     * 获取服务器地址
     * @return
     */
    public String getMainUrl(){

        String main_url = preferences.getString("main_url", null);
        if (main_url==null){
            main_url=context.getResources().getString(R.string.zhangheng_url);
        }
        return main_url;
    }

    /**
     * 获取聊天服务的查询端口
     * @return
     */
    public String getChatPort(){
        String chat_port = preferences.getString("chat_port", null);
        if (chat_port==null){
            chat_port=context.getResources().getString(R.string.chat_local_port);
        }
        return chat_port;
    }

}
