package com.example.xiadan.loginregister;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.xiadan.R;
import com.example.xiadan.bean.Response;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.VertifyPhoneNumber;
import com.example.xiadan.whole.BaseActivity;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class RegisterActivity extends BaseActivity {

    private static final String ACQUIRE_CODE_URL = "page/api/v1/code/register";
    private static final int CODE = 0XB5;
    private static final String REGIDTER_URL = "page/api/v1/register";
    private static final int REGISTER_CODE = 0XD3;
    private String codeResult;
    private EditText phoneInput;
    private String phonenumber;
    private Activity context;
    private String registerResult;
    /**
     * 是否同意用户协议
     */
    private boolean isAgreeProtacal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        context = RegisterActivity.this;
        phoneInput = (EditText) findViewById(R.id.phone_user_input);
        actionBar.setTitle("注册");
        acquireCodeInit();
        registerInit();
    }

    private void registerInit() {
        Button registerButton = (Button) findViewById(R.id.register_button);
        CheckBox checkUser = (CheckBox) findViewById(R.id.check_user_protocol);
        TextView protocol = (TextView) findViewById(R.id.protocol_content);
        protocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UserProtocal.class);
            }
        });
        checkUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     isAgreeProtacal=isChecked;
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCheck();
            }
        });
    }

    /**
     * 注册请求
     */
    private void registerCheck() {
        phonenumber = phoneInput.getText().toString().trim();
        if (TextUtils.isEmpty(phonenumber)) {
            ToastUtils.show(context, "手机号不能为空");
            return;
        }

        EditText verifyCodeEdit = (EditText) findViewById(R.id.verify_code_edit);
        String verifyCode = verifyCodeEdit.getText().toString();
        if (TextUtils.isEmpty(verifyCode)) {
            ToastUtils.show(context, "请输入验证码");
            return;
        }

        EditText passwordEdit = (EditText) findViewById(R.id.password_edit);
        String password = passwordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            ToastUtils.show(context, "请输入密码");
            return;
        }

        EditText confirmPasswordEdit = (EditText) findViewById(R.id.confirm_password_edit);
        String confirm = confirmPasswordEdit.getText().toString().trim();
        if (TextUtils.isEmpty(confirm)) {
            ToastUtils.show(context, "请输入确认密码");
            return;
        }
        if (!password.equals(confirm)) {
            ToastUtils.show(context, "两次输入密码不一致");
            return;
        }
        if(!isAgreeProtacal){
            ToastUtils.show(activity,"请确认用户协议");
            return;
        }
        requestRegis(phonenumber, verifyCode, password);
    }

    private void requestRegis(String phonenumber, String verifyCode, String password) {
        final HashMap<String, String> map = new HashMap<String, String>();
        map.put("phone", phonenumber);
        map.put("password", password);
        map.put("code", verifyCode);
        volley_post(REGIDTER_URL, map);
/*        HttpClientUtil.getInstance().doPostAsyn(REGIDTER_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                registerResult = result;
                Message msg = Message.obtain();
                msg.what = REGISTER_CODE;
                mHandler.sendMessage(msg);

            }
        });*/
    }

    @Override
    public void correcttResponse(String response) {
        super.correcttResponse(response);
        //无需解析
        ToastUtils.show(context, "注册成功，请登录");
        finish();
        startActivity(LoginActivity.class);
    }

    @Override
    public void errorResponse(VolleyError error) {
        super.errorResponse(error);
        ToastUtils.show(context, "注册失败，请检查输入信息");
    }

    /**
     * 获取验证码
     */
    private void acquireCodeInit() {

        TextView acquireVerify = (TextView) findViewById(R.id.acquire_verify);
        acquireVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phonenumber = phoneInput.getText().toString().trim();
                if (TextUtils.isEmpty(phonenumber)) {
                    ToastUtils.show(context, "手机号不能为空");
                    return;
                }
                if (!VertifyPhoneNumber.isMobileNO(phonenumber)) {
                    ToastUtils.show(context, "请输入正确的手机号");
                    return;
                }
                acquireVerifyCode(phonenumber);
            }
        });
    }

    private void acquireVerifyCode(String content) {
        ProgressDlgUtil.showProgressDlg(context, "正在发送，请稍候");
        final Map<String, String> map = new HashMap<String, String>();
        map.put("phone", content);
        HttpClientUtil.getInstance().doGetAsyn(ACQUIRE_CODE_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                codeResult = result;
                Message msg = Message.obtain();
                msg.what = CODE;
                mHandler.sendMessage(msg);

            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //验证码结果
                case CODE:
                    ProgressDlgUtil.stopProgressDlg();
                    if (codeResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, codeResult);
                        ToastUtils.show(context, "该手机号码已经注册过了");
                    } else {
                        //无需解析
//                        Gson gson = new Gson();
//                        Response response = gson.fromJson(codeResult, Response.class);
                        ToastUtils.show(context, "发送成功");
                    }
                    break;
/*                //注册结果
                case REGISTER_CODE:
                    if (registerResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, codeResult);
                        ToastUtils.show(context, "注册失败，请检查输入信息");
                    } else {
                        //无需解析
//                        Gson gson = new Gson();
//                        Response response = gson.fromJson(codeResult, Response.class);
                        ToastUtils.show(context, "注册成功，请登录");
                        finish();
                        startActivity(LoginActivity.class);
                    }
                    break;*/
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
