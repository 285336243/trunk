package com.jiujie8.choice.setting;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiujie8.choice.R;

/**
 * 动画进度条
 */
public class CustomProgressDialog extends Dialog {
    private Context context = null;
    private static CustomProgressDialog customProgressDialog = null;
    public CustomProgressDialog(Context context) {
        super(context);
        this.context=context;
    }

    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }


    public static CustomProgressDialog createDialog(Context context){
        customProgressDialog = new CustomProgressDialog(context,android.R.style.Theme_Translucent_NoTitleBar);
        customProgressDialog.setContentView(R.layout.customprogressdialog);
//        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
        customProgressDialog.setCanceledOnTouchOutside(false);
        return customProgressDialog;
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (customProgressDialog == null){
            return;
        }
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
    public CustomProgressDialog setTitile(String strTitle){
        return customProgressDialog;
    }


    public CustomProgressDialog setMessage(String strMessage){
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);

        if (tvMsg != null){
            tvMsg.setText(strMessage);
        }

        return customProgressDialog;
    }
}
