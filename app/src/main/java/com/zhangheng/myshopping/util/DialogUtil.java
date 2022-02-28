package com.zhangheng.myshopping.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.zhangheng.myshopping.R;

public class DialogUtil {
    private Context context;

    public DialogUtil(Context context) {
        this.context=context;
    }
    /*
    * 弹窗提示
    * */
    public static void dialog(Context context,String title, String message){
        AlertDialog.Builder d=new AlertDialog.Builder(context);
        AlertDialog dialog = d.create();
        View dialogView = View.inflate(context, R.layout.item_dialog_message, null);
        dialog.setView(dialogView);
        dialog.show();
        TextView tit=dialogView.findViewById(R.id.tv_dialog_title);
        TextView msg=dialogView.findViewById(R.id.tv_dialog_msg);
        if (title==null){
            tit.setVisibility(View.GONE);
        }
        if (message==null){
            msg.setVisibility(View.GONE);
        }
        tit.setText(title);
        msg.setText(message);
    }
    /*
     * 弹窗提示
     * */
    public void dialog(String title, String message){
        AlertDialog.Builder d=new AlertDialog.Builder(context);
        AlertDialog dialog = d.create();
        View dialogView = View.inflate(context, R.layout.item_dialog_message, null);
        dialog.setView(dialogView);
        dialog.show();
        TextView tit=dialogView.findViewById(R.id.tv_dialog_title);
        TextView msg=dialogView.findViewById(R.id.tv_dialog_msg);
        if (title==null){
            tit.setVisibility(View.GONE);
        }
        if (message==null){
            msg.setVisibility(View.GONE);
        }
        tit.setText(title);
        msg.setText(message);
    }
}
