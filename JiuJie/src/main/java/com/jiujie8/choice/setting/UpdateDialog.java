package com.jiujie8.choice.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jiujie8.choice.BlurDialog;
import com.jiujie8.choice.R;
import com.jiujie8.choice.services.NotificationDownloadService;

/**
 * 更新对话框
 */
public class UpdateDialog extends BlurDialog {


    private boolean isUpdate;
    private String updateUrl;
    private String versionNo;
    private String updateMessage;

/*    public UpdateDialog(String updateUrl, String versionNo, String updateMessage,boolean isUpdate) {
        this.updateUrl = updateUrl;
        this.updateMessage = updateMessage;
        this.versionNo = versionNo;
        this.isUpdate=isUpdate;
    }*/

    public static UpdateDialog newInstance(Bundle bundle) {
        UpdateDialog fragment = new UpdateDialog();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBlurEngine.setBlurRadius(4);
        mBlurEngine.setDownScaleFactor(4);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            isUpdate = args.getBoolean("isUpdate");
            updateUrl = args.getString("updateUrl");
            versionNo = args.getString("versionNo");
            updateMessage = args.getString("updateMessage");
        }
        if (isUpdate) {
            final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            mDialog.setContentView(R.layout.update_version_view);
//            mDialog.setCancelable(false);
//            mDialog.setCanceledOnTouchOutside(false);
            if (!mDialog.isShowing()) {
                mDialog.show();
            }
            TextView updateButton = (TextView) mDialog.findViewById(R.id.update_dialog_ok);
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NotificationDownloadService.class);
                    intent.putExtra("id", 1);
                    intent.putExtra("url", updateUrl);
                    getActivity().startService(intent);
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
            return mDialog;
        } else {
            final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            mDialog.setContentView(R.layout.update_version_view);
//            mDialog.setCanceledOnTouchOutside(false);
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
            mVersionNoText.setText(getLocalVersion(getActivity()));
            TextView mUpdateMessageText = (TextView) mDialog.findViewById(R.id.update_info);
            mUpdateMessageText.setText("当前已经是最新版本");
            return mDialog;
        }
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
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

}
