package com.example.xiadan.loginregister;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xiadan.R;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.VertifyPhoneNumber;
import com.example.xiadan.whole.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 51wanh on 2015/3/11.
 */
public class ForgetPasswordActivity extends BaseActivity {
    /**
     * 获取验证码
     */
    private static final String ACQUIRE_CODE_URL = "page/api/v1/code/changepsw";
    private static final int CODE = 0x21;
    /**
     * 忘记密码
     */
    private static final String SUB_URL = "page/api/v1/changepsw/forget";
    private static final int SUBMIT_CODE = 0X86;
    /**
     * 电话号码
     */
    private EditText mPhone;
    /**
     * 获得验证码
     */
    private View acquView;
    /**
     * 验证码结果
     */
    private String codeResult;
    /**
     * 验证码
     */
    private EditText verifyCodeEdit;
    /**
     * 新密码
     */
    private EditText newPsdEdit;
    /**
     * 确认新密码
     */
    private EditText confirPsdEdit;
    /**
     * 提交button
     */
    private Button subButton;
    /**
     * 提交结果
     */
    private String secretResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_forget_password);
        actionBar.setTitle("找回密码");
        initUi();
        acquireVerrifycode();
        submitInit();
    }

    private void submitInit() {
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkContent();
            }
        });
    }

    /**
     * 检查输入内容
     */
    private void checkContent() {
        String phone = super.getInputStr(mPhone);
        String code = getInputStr(verifyCodeEdit);
        String newP = getInputStr(newPsdEdit);
        String conP = getInputStr(confirPsdEdit);
        if (checkIsEmpty(phone, "请输入手机号")) {
            return;
        }
        if (!VertifyPhoneNumber.isMobileNO(phone)) {
            ToastUtils.show(activity, "请输入正确的手机号");
            return;
        }
        if (checkIsEmpty(code, "请输入验证码")) {
            return;
        }
        if (checkIsEmpty(newP, "请输入新密码")) {
            return;
        }
        if (checkIsEmpty(conP, "请输入确认密码")) {
            return;
        }
        if (!TextUtils.equals(newP, conP)) {
            ToastUtils.show(activity, "两次输入密码不一致");
            return;
        }
/*        if(!getInputStr(newPsdEdit).equals(getInputStr(confirPsdEdit))){
            ToastUtils.show(activity, "两次输入密码不一致");
            return;
        }*/

        requestSub(phone, code, newP);
    }

    private void requestSub(String phone, String code, String newpsw) {
        final Map<String, String> map = new HashMap<String, String>();
        // [必填] 手机号码
        map.put("phone", phone);
        // [必填] 新密码
        map.put("newpsw", newpsw);
        // [必填] 手机验证码
        map.put("code", code);
        HttpClientUtil.getInstance().doPutAsyn(SUB_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                secretResult = result;
                Message msg = Message.obtain();
                msg.what = SUBMIT_CODE;
                mHandler.sendMessage(msg);

            }
        });
    }

    private void acquireVerrifycode() {
        acquView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mPhone.getText().toString().trim();
                checkIsEmpty(phoneNumber, "请填入手机号");
                if (!VertifyPhoneNumber.isMobileNO(phoneNumber)) {
                    ToastUtils.show(activity, "请输入正确的手机号");
                    return;
                }
                acquireVerifyCode(phoneNumber);
            }
        });

    }

    /**
     * 获取验证码
     *
     * @param content 验证手机号
     */
    private void acquireVerifyCode(String content) {
        ProgressDlgUtil.showProgressDlg(activity, "正在发送，请稍候");
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
                        ToastUtils.show(activity, "提交失败");
                    } else {
                        //无需解析
//                        Gson gson = new Gson();
//                        Response response = gson.fromJson(codeResult, Response.class);
                        ToastUtils.show(activity, "提交成功");
                    }
                    break;
                //提交结果
                case SUBMIT_CODE:
                    if (secretResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, secretResult);
                        ToastUtils.show(activity, "验证码错误,手机验证码过期,该手机号码未注册");
                    } else {
                        //无需解析
//                        Gson gson = new Gson();
//                        Response response = gson.fromJson(codeResult, Response.class);
                        ToastUtils.show(activity, "密码重置成功，请登录");
                        finish();
                        startActivity(LoginActivity.class);
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void initUi() {
        mPhone = (EditText) findViewById(R.id.phone_user_input);
        acquView = findViewById(R.id.acquire_verify);
        verifyCodeEdit = (EditText) findViewById(R.id.verify_code_edit);
        newPsdEdit = (EditText) findViewById(R.id.newpassword_edit);
        confirPsdEdit = (EditText) findViewById(R.id.confirm_password_edit);
        subButton = (Button) findViewById(R.id.submit_button);
    }
}
