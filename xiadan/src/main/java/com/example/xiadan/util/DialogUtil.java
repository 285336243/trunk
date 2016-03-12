package com.example.xiadan.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.xiadan.R;

/**
 * Created by 51wanh on 2015/3/5.
 */
public class DialogUtil {

    private  CallBack callback;

    public  DialogUtil setLisener(CallBack callback){
         this.callback=callback;
        return this;
    }

    private static DialogUtil dialog = new DialogUtil();
    private DialogUtil() {

    }
    public static DialogUtil getInstance() {

        return dialog;
    }

    public DialogUtil showCancelDialog(Context context, String title) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        ((TextView)dialog.findViewById(R.id.title_text)).setText(title);
        TextView mConfirm=(TextView)dialog.findViewById(R.id.edit_cancel_view);
        mConfirm.setText("确定");
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                callback.setConfirm();

            }
        });
        TextView mCancel=(TextView)dialog.findViewById(R.id.edit_continue_view);
        mCancel.setText("取消");
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
        return this;
    }

    public interface CallBack {
       abstract void setConfirm();
    }
}
