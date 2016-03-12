package com.mzs.guaji.util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.service.NotificationDownloadService;

/**
 * Created by sunjian on 13-12-25.
 */
public class CommonDialog {

    private final Dialog mDialog ;

    private OnOKPressListener onOKPressListener;

    public CommonDialog(final Context context, final String okButtonText, final String cancelButtonText, final String title, final String message) {
        mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.dialog_view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);

        TextView titleTextView = (TextView)mDialog.findViewById(R.id.dialog_title);
        titleTextView.setText(title);
        Button updateButton = (Button) mDialog.findViewById(R.id.update_dialog_ok);
        updateButton.setText(okButtonText);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onOKPressListener!=null){
                    onOKPressListener.onOkClick();
                }
                mDialog.dismiss();
            }
        });
        Button cancelButton = (Button) mDialog.findViewById(R.id.update_dialog_cancel);
        cancelButton.setText(cancelButtonText);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onOKPressListener!=null){
                    onOKPressListener.onCancelClick();
                }
                mDialog.dismiss();
            }
        });

        TextView mVersionNoText = (TextView) mDialog.findViewById(R.id.update_version);
        mVersionNoText.setVisibility(View.GONE);
        TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
        mUpdateMessageText.setText(message);
    }

    public void show(){
        if(!isShow()){
            mDialog.show();
        }
    }

    public boolean isShow(){
        return mDialog.isShowing();
    }

    public void setOnOKPressListener(OnOKPressListener onOKPressListener){
        this.onOKPressListener = onOKPressListener;
    }

    public static interface OnOKPressListener{
        public void onOkClick();

        public void onCancelClick();
    }

}
