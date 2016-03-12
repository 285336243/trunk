

package com.shengzhish.xyj.persionalcore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.google.inject.Inject;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.Log;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ThirdPartyShareActivity;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.persionalcore.entity.Login;
import com.shengzhish.xyj.share.tencent.TencentOAuthThirdParty;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import roboguice.inject.InjectView;

/**
 * user login page
 */
public class LoginActivity extends ThirdPartyShareActivity {

    private static final String USER_LOGIN = "USER_LOGIN_INFORMATION";

    @Inject
    private Activity activity;

    @Inject
    UserLoginService service;

    @InjectView(R.id.back_imageview)
    private View backImageView;
    @InjectView(R.id.user_registrtion)
    private View userResgistrtion;
    @InjectView(R.id.user_login_email)
    private EditText userLoginEmail;
    @InjectView(R.id.user_login_secret)
    private EditText userLoginSecret;
    @InjectView(R.id.user_login_button)
    private View userLoginButton;

    @InjectView(R.id.sina_weibo)
    private View sinaWeiboView;

    @InjectView(R.id.tencnet_acount)
    private View tencentAccountView;
    @InjectView(R.id.viewText)
    private View viewText;
    private Timer timer;
    private boolean isInit = true;
    private int viewTextY;
    private static final int Start_NOTIFIER = 0x103;
    private int exit;
//    private WeiBoBroadcastReceiver weiBoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_layout);
        exit = getIntent().getIntExtra("exit", 10);
        if (exit == 25) {
            quit();
        }
        backImageView.setOnClickListener(onClickListeners);
        userResgistrtion.setOnClickListener(onClickListeners);
        userLoginButton.setOnClickListener(onClickListeners);

        setSinaWeiboClickListener();
        setTencentAccountClickListener();

//        weiBoReceiver = new WeiBoBroadcastReceiver();
//        registerReceiver(weiBoReceiver, new IntentFilter(IConstant.WEIBO_SUCCESEE));

        timer = new Timer();
        timer.scheduleAtFixedRate(new MyShedule(), 500, 200);
    }

    private void setSinaWeiboClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSinaAuthorize(new DoShare() {
                    @Override
                    public void share() {

                    }
                });
            }
        };
        sinaWeiboView.setOnClickListener(clickListener);
    }

    private void setTencentAccountClickListener() {
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doQQAuthorize(new DoShare() {
                    @Override
                    public void share() {

                    }
                });
            }
        };
        tencentAccountView.setOnClickListener(clickListener);
    }

    private View.OnClickListener onClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_imageview:
                    if (exit == 25)
                        sendLogoutBroadcast();
                    finish();
                    break;
                case R.id.user_registrtion:
                    startActivity(new Intent(LoginActivity.this, RegisTrationActivity.class));
                    finish();
                    break;
                case R.id.user_login_button:
                    commitLoginInformation();
                    break;
            }

        }
    };

    private void commitLoginInformation() {
        String userEmail = userLoginEmail.getText().toString().trim();
        String userSecret = userLoginSecret.getText().toString().trim();
        if (TextUtils.isEmpty(userEmail)) {
            ToastUtils.show(this, "用户名不能为空");
            return;
        } else if (TextUtils.isEmpty(userSecret)) {
            ToastUtils.show(this, "密码不能为空");
            return;
        }
        final Map<String, String> loginPara = new HashMap<String, String>();
        loginPara.put("account", userEmail);
        loginPara.put("password", userSecret);

        new ProgressDialogTask<Login>(this) {
            @Override
            protected Login run(Object data) throws Exception {
                return (Login) HttpUtils.doRequest(service.createLoginRequest(loginPara)).result;
            }

            @Override
            protected void onSuccess(Login loginbean) throws Exception {
                super.onSuccess(loginbean);
                if (loginbean != null) {
                    if (loginbean.getResponseCode() == 0) {
                        saveUserInfo(loginbean);
                        setResult(RESULT_OK);
                        sendLoginBroadcast();
                        finish();
                    } else {
                        ToastUtils.show(LoginActivity.this, loginbean.getResponseMessage());
                    }
                }
            }
        }.start("正在登陆");
    }

    private void saveUserInfo(Login mLogin) {
        LoginUtilSh.saveUserId(this, mLogin.getUser().getUserId());
        LoginUtilSh.saveLoginState(this, mLogin.getResponseCode());
        LoginUtilSh.saveUserNickName(this, mLogin.getUser().getNickname());
        LoginUtilSh.saveUserAvatar(this, mLogin.getUser().getAvatar());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    private class MyShedule extends TimerTask {
        @Override
        public void run() {
            Message message = new Message();
            message.what = Start_NOTIFIER;
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Start_NOTIFIER:
                    int[] location = new int[2];
                    viewText.getLocationOnScreen(location);
                    if (isInit) {
                        viewTextY = location[1];
                        isInit = false;
                    }
                    if (location[1] < viewTextY) {
                        userResgistrtion.setVisibility(View.GONE);
                    } else {
                        userResgistrtion.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            if (exit == 25)
                sendLogoutBroadcast();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sendLoginBroadcast() {
        Intent eIntent = new Intent();
//        Log.v("person", "发送广播");
        eIntent.setAction(IConstant.PERSON_LOGIN);
        sendBroadcast(eIntent);

    }

    private void sendLogoutBroadcast() {
        Intent mIntent = new Intent();
        mIntent.setAction(IConstant.ACTIVITY_LOCATION);
        sendBroadcast(mIntent);
    }

/*    private class WeiBoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ((IConstant.WEIBO_SUCCESEE).equals(intent.getAction())) {
                    finish();
                }
            }
        }
    }*/
}
