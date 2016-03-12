package com.example.xiadan.loginregister;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.xiadan.MainActivity;
import com.example.xiadan.R;
import com.example.xiadan.bean.LoginBean;
import com.example.xiadan.bean.Response;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.AppManager;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseActivity {


    private static final String LOGIN_URL = "page/api/v1/common/login";
    private static final int LOGIN_CODE = 0XDA1;
    private TextView regisLink;
    private EditText mName;
    private EditText mPassword;
    private Button mButton;
    private Activity context;
    private String loginResult;
    /**
     * 忘记密码
     */
    private TextView forgetPasss;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_login);
        context = LoginActivity.this;
        AppManager.getAppManager().addActivity(this);
        actionBar.setTitle("登录");
        initView();

    }

    private void initView() {
        mName = (EditText) findViewById(R.id.username_edit);
        mPassword = (EditText) findViewById(R.id.password_edit);
        regisLink = (TextView) findViewById(R.id.register_link);
        mButton = (Button) findViewById(R.id.signin_button);
        forgetPasss = (TextView) findViewById(R.id.forget_password);
        forgetPasss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ForgetPasswordActivity.class);
            }
        });
        regisLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RegisterActivity.class);
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInfo();
            }
        });
        findViewById(R.id.company_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CompanyLoginActivity.class);
               finish();
            }
        });
    }


    private void checkInfo() {
        String userName = mName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.show(context, "用户名不能为空");
            return;
        }
        if (userName.length() != 11) {
            ToastUtils.show(context, "请输入正确的用户名");
            return;
        }
        String password = mPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(context, "密码不能为空");
            return;
        }
//        loginReq(userName, password);
        loginPost(userName, password);
    }

    private void loginPost(String name, String secret) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", name);
        map.put("password", secret);
        volley_post(LOGIN_URL, map);
    }


    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        ToastUtils.show(context, "登录成功");
        Gson gson = new Gson();
        LoginBean data = gson.fromJson(response, LoginBean.class);
        String sid = data.getSid();
        String savedSid = SaveSettingUtil.getUserSid(context);
        if (null != savedSid) {
            SaveSettingUtil.removeLoginSid(context);
        }
        //保存登录验证码
        SaveSettingUtil.saveUserSid(context, sid);
        String ddd = SaveSettingUtil.getUserSid(context);
        finish();
//        startActivity(MainActivity.class);
    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
        if(error.networkResponse==null){
            return;
        }
        ToastUtils.show(context, "用户名或密码错误");
    }

/*    private void loginReq(String name, String secret) {
        ProgressDlgUtil.showProgressDlg(activity,"请稍后...");
        final Map<String, String> map = new HashMap<String, String>();
        map.put("userName", name);
        map.put("password", secret);
        HttpClientUtil.getInstance().doPostAsyn(LOGIN_URL,map,new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                loginResult = result;
                Message msg = Message.obtain();
                msg.what = LOGIN_CODE;
                mHandler.sendMessage(msg);
            }
        });

    }
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //登录结果
                case LOGIN_CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (loginResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, codeResult);
                        ToastUtils.show(context,"用户名或密码错误");
                    } else {
                        ToastUtils.show(context, "登录成功");
                        Gson gson = new Gson();
                        LoginBean data = gson.fromJson(loginResult, LoginBean.class);
                        String sid = data.getSid();
                        String savedSid = SaveSettingUtil.getUserSid(context);
                        if (null != savedSid) {
                            SaveSettingUtil.removeLoginSid(context);
                        }
                        //保存登录验证码
                        SaveSettingUtil.saveUserSid(context, sid);
                       String ddd= SaveSettingUtil.getUserSid(context);
                        finish();
                        startActivity(MainActivity.class);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };*/
}