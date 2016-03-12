package com.socialtv.personcenter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.socialtv.GuideActivity;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.UpdateResponse;
import com.socialtv.util.HttpboLis;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;

import java.io.File;

import roboguice.inject.InjectView;

/**
 * app设置
 */
public class SettingActivity extends DialogFragmentActivity {
    @Inject
    private PersonalUrlService service;

    @InjectView(R.id.clear_catch_layout)
    private View clearCatchLayout;

    @InjectView(R.id.sign_out_layout)
    private View signOutLayout;

    @InjectView(R.id.check_update_layout)
    private View checkUpdateLayout;

    @InjectView(R.id.feed_back_layout)
    private View feedBackLayout;

    @InjectView(R.id.version_show_text)
    private TextView textVersin;

    @InjectView(R.id.setting_using_help)
    private View usingHelpView;

    private ProgressDialog progressdialog;
    private Handler mHandler = new Handler();
    private int code;

    private View.OnClickListener clickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.clear_catch_layout:
                    mHandler.removeCallbacks(mRunnable);
                    progressdialog.setMessage("正在清除缓存,请稍候...");
                    progressdialog.setCancelable(false);
                    progressdialog.show();
                    clearCatch();
                    mHandler.postDelayed(mRunnable, 1000);
                    break;
                case R.id.sign_out_layout:
                    signOut();
                    break;
                case R.id.check_update_layout:
                    checkUpdateMethd();
                    break;
                case R.id.feed_back_layout:
                    startActivity(new Intent(SettingActivity.this, FeedBackActivity.class));
                    break;
                default:
                    break;
            }
        }
    };

    private void checkUpdateMethd() {

        try {
            code = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String url = "system/version.json?platform=android";
        HttpboLis.getInstance().getHttpDialog(this, UpdateResponse.class, url, "正在检查版本", new HttpboLis.OnCompleteListener<UpdateResponse>() {
            @Override
            public void onComplete(UpdateResponse response) {
                if (response.getResponseCode() == 0) {
                    if (response.getVersionCode() > code) {
                        UpgradeVersonDialog.createUpdateDialog(SettingActivity.this, response.getUpgradeUrl(),
                                response.getVersionNo(), response.getUpgradeMsg());
                    } else {
                        UpgradeVersonDialog.needNotUpdate(SettingActivity.this);
                    }
                } else {
                    ToastUtils.show(SettingActivity.this, response.getResponseMessage());
                }
            }
        });

    }

    private void signOut() {
        new AbstractRoboAsyncTask<Response>(SettingActivity.this) {
            @Override
            protected Response run(Object data) throws Exception {

                return (Response) HttpUtils.doRequest(service.creatExitRequest()).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        clearUserInfor();
                        sendBroadcast(new Intent(IConstant.SETTING_EXIT));
                        finish();
                    } else
                        ToastUtils.show(SettingActivity.this, response.getResponseMessage());
                }
            }
        }.execute();

    }

    private void clearUserInfor() {
        LoginUtil.clear(this);
        HttpUtils.clearCookie();
    }

    private void clearCatch() {
        new Thread(new Runnable() {
            public void run() {
                clearFile(new File(Environment.getExternalStorageDirectory(), "Wala"));
                ImageLoader.getInstance().clearDiskCache();
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_setting_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("设置");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
        progressdialog = new ProgressDialog(this);
        clearCatchLayout.setOnClickListener(clickListeners);
        signOutLayout.setOnClickListener(clickListeners);
        checkUpdateLayout.setOnClickListener(clickListeners);
        feedBackLayout.setOnClickListener(clickListeners);
        try {
            textVersin.setText("当前版本为：" + getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        usingHelpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intents(SettingActivity.this, GuideActivity.class).add(IConstant.IS_HIDE_TITLE, false).add(IConstant.IS_GUIDE_BUTTON_HIDE, true).toIntent());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != progressdialog || progressdialog.isShowing())
                progressdialog.dismiss();
            ToastUtils.show(SettingActivity.this, "缓存清除完成");
        }
    };

    private void clearFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    clearFile(f);
                }
            }
            file.delete();
        }
    }
}


