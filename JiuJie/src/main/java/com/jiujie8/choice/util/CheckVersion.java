package com.jiujie8.choice.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.setting.UpdateDialog;
import com.jiujie8.choice.setting.entity.ClientVersion;
import com.jiujie8.choice.setting.entity.UpdateBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查版本
 */
public class CheckVersion {
    private static int code;
    private static final String UPDATE_VERSION_URL = "identity/client/update";


    public static void check_update(final Context context) {
        ((ChoiceApplication) context.getApplicationContext()).isUpdate = false;
        try {
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<String, String>();
//        map.put("client_version",String.valueOf(code));
        map.put("client_platform", "android");
        HttpHelp.getInstance().getHttp(context, UpdateBean.class, UPDATE_VERSION_URL, map, new HttpHelp.OnCompleteListener<UpdateBean>() {
            @Override
            public void onComplete(UpdateBean response) {
                if (response.getCode().equals((IConstant.STATE_OK))) {
                    ClientVersion version = response.getEntity().getClientVersion();
                    Bundle bundle = new Bundle();
                    bundle.putString("updateUrl", version.getUpdateUrl());
                    bundle.putString("versionNo", version.getVersion());
                    bundle.putString("updateMessage", version.getUpdateNote());

                    UpdateDialog mDialog;
                    if (version.getVersionCode() > code) {
                        bundle.putBoolean("isUpdate", true);

                        mDialog = UpdateDialog.newInstance(bundle);
                        mDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "blur");
                    }
                } else {
//                    ToastUtils.show(context, response.getMessage());
                    Toast.makeText(context, response.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}
