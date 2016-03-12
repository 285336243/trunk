package com.example.xiadan.loginregister;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.example.xiadan.R;
import com.example.xiadan.bean.LoginBean;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.whole.AppManager;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * 企业用户登录
 */
public class CompanyLoginActivity extends BaseActivity{
    private static final String COMPANY_LOGIN_URL = "page/api/v1/company/login";
    private EditText companyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login_company);
        AppManager.getAppManager().addActivity(this);
        actionBar.setTitle("企业登录");
        intUi();
        listener();
    }
    private void intUi() {
        companyName=(EditText)findViewById(R.id.company_name);
    }
    private void listener() {
        //普通用户登录
        findViewById(R.id.genetal_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.class);
                finish();
            }
        });
        findViewById(R.id.signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContent();
            }
        });
    }

    private void checkContent() {
        String userName = companyName.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.show(activity, "公司名称为空");
            return;
        }
        loginPost(userName);
    }

    private void loginPost(String name) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("company", name);
        volley_post(COMPANY_LOGIN_URL, map);
    }
    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        ToastUtils.show(activity, "登录成功");
        Gson gson = new Gson();
        LoginBean data = gson.fromJson(response, LoginBean.class);
        String sid = data.getSid();
        String savedSid = SaveSettingUtil.getUserSid(activity);
        if (null != savedSid) {
            SaveSettingUtil.removeLoginSid(activity);
        }
        //保存登录验证码
        SaveSettingUtil.saveUserSid(activity, sid);
        String ddd = SaveSettingUtil.getUserSid(activity);
        finish();
//        startActivity(MainActivity.class);
    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
        if(error.networkResponse==null){
            return;
        }
        ToastUtils.show(activity, "公司名错误，请检查");
    }

}
