package com.socialtv.personcenter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.socialtv.R;
import com.socialtv.WalaApplication;
import com.socialtv.core.CacheRepository;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.ExternalAccounts;
import com.socialtv.personcenter.entity.Login;
import com.socialtv.share.OAuthThirdParty;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.tencent.weibo.sdk.android.api.util.Util;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * user login
 */
public class LoginActivity extends ThirdPartyShareActivity {

    private static final String LOGIN_URL = "identity/signin.json";

    @InjectView(R.id.register_layout)
    private View regiateAccount;

    @InjectView(R.id.user_name)
    private EditText userName;

    @InjectView(R.id.user_password)
    private EditText userPassWord;

    @InjectView(R.id.login_button)
    private View loginButton;

    @InjectView(R.id.sina_weibo)
    private View sinaWeiBo;

    @InjectView(R.id.tencent_weibo)
    private View tencentWeibo;

    @InjectView(R.id.login_forget_password)
    private View forgetPasswordView;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.login_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("登录");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        regiateAccount.setOnClickListener(clickListeners);
        loginButton.setOnClickListener(clickListeners);
        sinaWeiBo.setOnClickListener(clickListeners);
        tencentWeibo.setOnClickListener(clickListeners);

        forgetPasswordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intents(LoginActivity.this, BindingMobileActivity.class).add(IConstant.BINDING_MOBILE_TITLE, "重置密码").add(IConstant.IS_BACK, false).toIntent());
            }
        });
    }


    private View.OnClickListener clickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_layout:
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    finish();
                    break;
                case R.id.login_button:
                    loginAccount();
                    break;
                case R.id.sina_weibo:
                    doSinaAuthorize(new DoShare() {
                        @Override
                        public void share() {
                        }
                    });
                    break;
                case R.id.tencent_weibo:
                    doTencentAuthorize(new DoShare() {
                        @Override
                        public void share() {
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loginAccount() {
        String account = userName.getText().toString().trim();
        String password = userPassWord.getText().toString().trim();
        if (TextUtils.isEmpty(account)) {
            ToastUtils.show(this, "用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(this, "密码不能为空");
            return;
        }
        setSupportProgressBarIndeterminateVisibility(true);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("account", account);
        params.put("password", password);
        new ProgressDialogTask<Login>(this) {
            @Override
            protected Login run(Object data) throws Exception {
                GsonRequest<Login> request = new GsonRequest<Login>(Request.Method.POST, LOGIN_URL);
                request.setHeaders(params);
                request.setClazz(Login.class);
                return (Login) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Login login) throws Exception {
                super.onSuccess(login);
                setSupportProgressBarIndeterminateVisibility(false);
                if (login != null) {
                    if (login.getResponseCode() == 0) {
                        //登陆成功上传设备唯一ID
                        WalaApplication application = (WalaApplication) getApplication();
                        application.postPushId();

                        inputMethodManager.hideSoftInputFromWindow(userPassWord.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        setResult(RESULT_OK);
                        if (login != null) {
                            saveUserInfo(login);
                        }
                        sendLoginBroadcast();
                        if ("BIND_MOBILE".equals(login.getRender())) {
                            startActivity(new Intents(LoginActivity.this, BindingMobileActivity.class).add(IConstant.IS_BACK, true).toIntent());
                            finish();
                        } else {
                            finish();
                        }
                    } else {
                        ToastUtils.show(LoginActivity.this, login.getResponseMessage());
                    }
                }
            }
        }.start("正在登陆");
    }

    private void sendLoginBroadcast() {
        Intent eIntent = new Intent();
        eIntent.setAction(IConstant.USER_LOGIN);
        sendBroadcast(eIntent);
    }

    private void saveUserInfo(Login login) {
        LoginUtil.saveUserId(this, login.getUser().getUserId());
        LoginUtil.saveLoginState(this, login.getResponseCode());
        LoginUtil.saveUserNickName(this, login.getUser().getNickname());
        LoginUtil.saveUserAvatar(this, login.getUser().getAvatar());
        if (login.getExternalAccount() != null && !login.getExternalAccount().isEmpty()) {
            for (ExternalAccounts accounts : login.getExternalAccount()) {
                if ("QQ".equals(accounts.getType())) {
                    Util.saveSharePersistent(this, "ACCESS_TOKEN", accounts.getToken());
                    Util.saveSharePersistent(this, "EXPIRES_IN", String.valueOf(System.currentTimeMillis() / 1000 * 30 * 60 * 60 * 24));
                    Util.saveSharePersistent(this, "REFRESH_TOKEN", "");
                    Util.saveSharePersistent(this, "OPEN_ID", accounts.getUid());
                    Util.saveSharePersistent(this, "NAME", accounts.getNickname());
                    Util.saveSharePersistent(this, "NICK", accounts.getNickname());
                    Util.saveSharePersistent(this, "AUTHORIZETIME",
                            String.valueOf(System.currentTimeMillis() / 1000l));
                    Util.saveSharePersistent(this, "CLIENT_ID",String.valueOf(appId));
                } else {
                    CacheRepository.getInstance().fromContext(this).putString(OAuthThirdParty.SINA, OAuthThirdParty.TOKEN, accounts.getToken()).putLong(OAuthThirdParty.SINA, OAuthThirdParty.EXPIRED_TIME, System.currentTimeMillis() / 1000 * 30 * 60 * 60 * 24).putString(OAuthThirdParty.SINA, OAuthThirdParty.UID, accounts.getUid());
                }
            }
        }
    }

}
