package com.mzs.guaji.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.ExternalAccounts;
import com.mzs.guaji.entity.Login;
import com.mzs.guaji.share.OAuthThirdParty;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.tencent.weibo.sdk.android.api.util.Util;

public class LoginActivity extends DefaultThirdPartyShareActivity {

    private Context context = LoginActivity.this;
    private RelativeLayout mForGetPasswordLayout;
    private RelativeLayout mRegisterLayout;
    private LinearLayout sinaLogin;
    private LinearLayout qqLogin;
    private Button mLoginButton;
    private EditText mUserAccountEdit;
    private EditText mUserPasswordEdit;
    private CacheRepository mRepository;
    private boolean isScan;
    private String code;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.login_layout);
        isScan = getIntent().getBooleanExtra("scan", false);
        code = getIntent().getStringExtra("code");
        LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.login_title_back);
        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                setResult(1);
                finish();
            }
        });
        mUserAccountEdit = (EditText) findViewById(R.id.login_user_name);
        mUserPasswordEdit = (EditText) findViewById(R.id.login_user_pwd);
        mForGetPasswordLayout = (RelativeLayout) findViewById(R.id.forget_password);
        mForGetPasswordLayout.setOnClickListener(mForGetPasswordListener);
        mRegisterLayout = (RelativeLayout) findViewById(R.id.register_layout);
        mRegisterLayout.setOnClickListener(mRegisterListener);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(mLoginListener);
        sinaLogin = (LinearLayout) findViewById(R.id.btn_sina_login);
        sinaLogin.setOnClickListener(sinaLoginOnClickListener);
        qqLogin = (LinearLayout) findViewById(R.id.btn_qq_login);
        qqLogin.setOnClickListener(tencentLoginClickListener);
        mRepository = CacheRepository.getInstance().fromContext(context);
    }


    View.OnClickListener mLoginListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String mUserAccount = mUserAccountEdit.getText().toString();
            String mUserPassword = mUserPasswordEdit.getText().toString();
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("account", mUserAccount);
            headers.put("password", mUserPassword);
            TipsUtil.showTipDialog(context, "正在登录");
            mApi.requestPostData(getRequestUrl(), Login.class, headers, new Response.Listener<Login>() {

                @Override
                public void onResponse(Login response) {
                    TipsUtil.dismissDialog();
                    if (response.getResponseCode() == 0) {
                        Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT).show();
                        saveUserInfo(response);
                        if(response.getExternalAccounts() != null) {
                            for(int i=0; i<response.getExternalAccounts().size(); i++) {
                                ExternalAccounts mAccouns = response.getExternalAccounts().get(i);
                                if(mAccouns != null) {
                                    if("SINA".equals(mAccouns.getType())) {
                                        mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINAID, mAccouns.getId());
                                        mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINANICKNAME, mAccouns.getNickname());
                                    }else if("QQ".equals(mAccouns.getType())) {
                                        mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQID, mAccouns.getId());
                                        mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQNICKNAME, mAccouns.getNickname());
                                    }
                                }
                            }
                        }
                        if(isScan) {
                            Intent mIntent = new Intent();
                            mIntent.putExtra("code", code);
                            mIntent.setAction(BroadcastActionUtil.IS_SCAN_ACTION);
                            sendBroadcast(mIntent);
                            finish();
                        }else {
                            Intent mIntent = new Intent();
                            mIntent.putExtra("userId", response.getUserId());
                            mIntent.setAction(BroadcastActionUtil.LOGIN_ACTION);
                            sendBroadcast(mIntent);
                            finish();
                        }
                    } else {
                        Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        LoginUtil.saveLoginState(context, response.getResponseCode());
                    }
                }
            }, null);
        }
    };

    /**
     * 忘记密码点击事件
     */
    View.OnClickListener mForGetPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, ForGetPasswordActivity.class);
            startActivity(mIntent);
        }
    };

    private void saveUserInfo(Login mLogin) {
        LoginUtil.saveUserId(context, mLogin.getUserId());
        LoginUtil.saveLoginState(context, mLogin.getResponseCode());
        LoginUtil.saveUserAvatar(context, mLogin.getAvatar());
        if(mLogin.getMobile() != null) {
            LoginUtil.saveMobileNumber(context, mLogin.getMobile());
        }
        if(mLogin.getExternalAccounts() != null) {
            for(int i=0; i<mLogin.getExternalAccounts().size(); i++) {
                ExternalAccounts mAccount = mLogin.getExternalAccounts().get(i);
                if("QQ".equals(mAccount.getType())) {
                    mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQID, mAccount.getUid());
                    mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQNICKNAME, mAccount.getNickname());
                    Util.saveSharePersistent(context, "ACCESS_TOKEN", mAccount.getToken());
                }else if("SINA".equals(mAccount.getType())) {
                    mRepository.putString(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN, mAccount.getToken());
                    mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINAID, mAccount.getUid());
                    mRepository.putString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINANICKNAME, mAccount.getNickname());
                }
            }
        }
    }

    View.OnClickListener mRegisterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, RegisterActivity.class);
            mIntent.putExtra("scan", isScan);
            mIntent.putExtra("code", code);
            startActivity(mIntent);
            finish();
        }
    };

    private String getRequestUrl() {
        return DOMAINS + "identity/signin.json";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(1);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
