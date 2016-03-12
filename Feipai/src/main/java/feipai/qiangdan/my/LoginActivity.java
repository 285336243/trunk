package feipai.qiangdan.my;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.crashcatch.AppManager;
import feipai.qiangdan.home.HomeActivity;
import feipai.qiangdan.javabean.LoginBean;
import feipai.qiangdan.order.LocationActivity;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.NetWorkConnect;
import feipai.qiangdan.util.SaveSettingUtil;
import feipai.qiangdan.view.ClearEditText;

/**
 *
 */
public class LoginActivity extends DialogFragmentActivity {

    private static final String LOGIN_URL = "api/v1/login";
    private static final int ACCEPT_CODE = 0x12;
    private Toast mToast;
    private String response;
    private ProgressDialog progressdialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        final ClearEditText username = (ClearEditText) findViewById(R.id.username);
        final ClearEditText password = (ClearEditText) findViewById(R.id.password);

        ((Button) findViewById(R.id.login)).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = username.getText().toString().trim();
                String secret = password.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    //设置晃动
                    username.setShakeAnimation();
                    //设置提示
                    showToast("用户名不能为空");
                    return;
                }

                if (TextUtils.isEmpty(secret)) {
                    password.setShakeAnimation();
                    showToast("密码不能为空");
                    return;
                }
                loginReq(name, secret);

            }
        });


    }


    private void loginReq(String name, String secret) {
        if (!NetWorkConnect.isConnect(this)) {
            ToastUtils.show(this, "您的网络不可用，请检查您的网络状况");
            return;
        }
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", name);
        map.put("password", secret);
/*        progressdialog = new ProgressDialog(this);
        progressdialog.setMessage("正在登录,请稍候...");
        progressdialog.setCancelable(true); //点击返回键是否消失 true为消失
        progressdialog.show();*/
        volley_post(LOGIN_URL, map);
/*        new Thread() {
            @Override
            public void run() {
                Message msg2 = Message.obtain();
                response = HttpClientUtil.getInstance().getPostDta(LOGIN_URL, map);
                msg2.what = ACCEPT_CODE;
                mHandler.sendMessage(msg2);
            }
        }.start();*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        showToast("登录成功");
        Gson gson = new Gson();
        LoginBean data = gson.fromJson(response, LoginBean.class);
        String sid = data.getSid();
        String savedSid = SaveSettingUtil.getUserSid(context);
        if (null != savedSid) {
            SaveSettingUtil.removeLoginSid(context);
        }
        //保存登录验证码
        SaveSettingUtil.saveUserSid(context, sid);
//                       String ddd= SaveSettingUtil.getUserSid(context);
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

/*    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACCEPT_CODE:
                    if (response.contains(IConstant.REQUST_FAIL)) {
//                        showToast(response);
                        showToast("登录失败" +
                                "");
                    } else {
                        showToast("登录成功");
                        Gson gson = new Gson();
                        LoginBean data = gson.fromJson(response, LoginBean.class);
                        String sid = data.getSid();
                        String savedSid = SaveSettingUtil.getUserSid(context);
                        if (null != savedSid) {
                            SaveSettingUtil.removeLoginSid(context);
                        }
                        //保存登录验证码
                        SaveSettingUtil.saveUserSid(context, sid);
//                       String ddd= SaveSettingUtil.getUserSid(context);
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if (null != progressdialog || progressdialog.isShowing())
                        progressdialog.dismiss();
                    break;
            }
            super.handleMessage(msg);
        }
    };*/

    /**
     * 显示Toast消息
     *
     * @param msg 提示消息
     */
    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AppManager.getAppManager().AppExit(this);
        }
        return super.onKeyDown(keyCode, event);
    }
}
