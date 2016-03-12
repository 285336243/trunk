package com.mzs.guaji.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mzs.guaji.R;

/**
 * Created by wlanjie on 14-1-14.
 */
public class GiveUpEditingDialog {

    public static void showGiveUpEditingDialog(final Activity mActivity, final String content) {
        if(!"".equals(content)) {
            final Dialog mDialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
            mDialog.setContentView(R.layout.dialog_view);
            mDialog.setCanceledOnTouchOutside(false);
            TextView mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
            mDialogTitle.setText("提示");
            TextView mInfoText = (TextView) mDialog.findViewById(R.id.update_info);
            mInfoText.setText("是否放弃编辑该内容");
            Button mConfirmButton = (Button) mDialog.findViewById(R.id.update_dialog_ok);
            mConfirmButton.setText("继续编辑");
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
            Button mCancelButton = (Button) mDialog.findViewById(R.id.update_dialog_cancel);
            mCancelButton.setText("放弃编辑");
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
            if(!mDialog.isShowing()) {
                mDialog.show();
            }
        }else {
            mActivity.finish();
        }
    }

    public static void showGiveUpEditingDialog(final Activity mActivity) {
            final Dialog mDialog = new Dialog(mActivity, android.R.style.Theme_Translucent_NoTitleBar);
            mDialog.setContentView(R.layout.dialog_view);
            mDialog.setCanceledOnTouchOutside(false);
            TextView mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
            mDialogTitle.setText("提示");
            TextView mInfoText = (TextView) mDialog.findViewById(R.id.update_info);
            mInfoText.setText("是否放弃编辑该内容");
            Button mConfirmButton = (Button) mDialog.findViewById(R.id.update_dialog_ok);
            mConfirmButton.setText("继续编辑");
            mConfirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
            Button mCancelButton = (Button) mDialog.findViewById(R.id.update_dialog_cancel);
            mCancelButton.setText("放弃编辑");
            mCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.finish();
                }
            });
            if(!mDialog.isShowing()) {
                mDialog.show();
            }
    }

    public static void showCopyDialog(final Context context, final String title, final String copyTitleText, final String copyContentText) {
        View view = LayoutInflater.from(context).inflate(R.layout.copy_layout, null);
        final TextView titleText = (TextView) view.findViewById(R.id.copy_title_text);
        titleText.setText(title);
        final TextView copyText = (TextView) view.findViewById(R.id.copy_text);
        final TextView contentText = (TextView) view.findViewById(R.id.copy_content_text);
        final View v = view.findViewById(R.id.copy_line);
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(view);
        if (!dialog.isShowing()) {
            dialog.show();
        }
        if (TextUtils.isEmpty(copyContentText)) {
            v.setVisibility(View.GONE);
            contentText.setVisibility(View.GONE);
        } else {
            copyText.setText("标题");
            contentText.setText("正文");
        }
        contentText.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View mView) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setText(copyContentText);
                } else {
                    ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText("", copyContentText));
                }
            }
        });
        copyText.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View mView) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager manager = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setText(copyTitleText);
                } else {
                    ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    manager.setPrimaryClip(ClipData.newPlainText("", copyTitleText));
                }
            }
        });
    }
}
