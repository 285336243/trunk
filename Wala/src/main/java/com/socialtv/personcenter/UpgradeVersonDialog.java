package com.socialtv.personcenter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socialtv.R;
import com.socialtv.services.NotificationDownloadService;


public class UpgradeVersonDialog {


    /**
     * 创建升级时的对话框
     */
    public static void createUpdateDialog(final Context context, final String updateUrl, final String versionNo, final String updateMessage) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.update_version_view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        TextView updateButton = (TextView) mDialog.findViewById(R.id.update_dialog_ok);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotificationDownloadService.class);
                intent.putExtra("id", 1);
                intent.putExtra("url", updateUrl);
                context.startService(intent);
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        TextView cancelButton = (TextView) mDialog.findViewById(R.id.update_dialog_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });

        TextView mVersionNoText = (TextView) mDialog.findViewById(R.id.update_version);
        mVersionNoText.setText("版本 : " + versionNo);
        TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
        mUpdateMessageText.setText(updateMessage);
    }

    /**
     * 不用更新时调用的对话框
     */
    public static void needNotUpdate(Context context) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.update_version_view);
        mDialog.setCanceledOnTouchOutside(false);
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
        TextView updateButton = (TextView) mDialog.findViewById(R.id.update_dialog_ok);
        updateButton.setText("确定");
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.rightMargin = 16;
//        params.leftMargin = 16;
//        updateButton.setLayoutParams(params);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        TextView cancelButton = (TextView) mDialog.findViewById(R.id.update_dialog_cancel);
        cancelButton.setVisibility(View.GONE);
//        mDialog.findViewById(R.id.heng_view).setVisibility(View.GONE);
        mDialog.findViewById(R.id.shu_view).setVisibility(View.GONE);

        TextView mVersionNoText = (TextView) mDialog.findViewById(R.id.update_version);
        mVersionNoText.setText(getLocalVersion(context));
        TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
        mUpdateMessageText.setText("当前已经是最新版本");
    }

    /**
     * 获取本地应用版本号
     *
     * @return
     */
    public static String getLocalVersion(Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo mInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            return mInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取本地应用版本号
     *
     * @return
     */
    public static int getLocalVersionName(Context context) {
        try {
            PackageManager mPackageManager = context.getPackageManager();
            PackageInfo mInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            return mInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
