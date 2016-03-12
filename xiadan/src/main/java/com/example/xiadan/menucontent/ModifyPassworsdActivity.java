package com.example.xiadan.menucontent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.xiadan.R;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.HttpClientUtil;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.SaveSettingUtil;
import com.example.xiadan.util.VertifyPhoneNumber;
import com.example.xiadan.whole.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 51wanh on 2015/3/12.
 */
public class ModifyPassworsdActivity extends BaseActivity{
    /**
     * 修改密码url
     */
    private static final String MODIFY_URL = "page/api/v1/changepsw";
    private static final int SUBMIT_CODE = 0X82;
    /**
     * 电话号码
     */
    private EditText mPhone;
    /**
     * 验证码结果
     */
    private String codeResult;
    /**
     * 旧密码
     */
    private EditText oldPassWordEdit;
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
        setContentView(R.layout.layout_modify_password);
        actionBar.setTitle("密码修改");
        initUi();
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
        String oldPassWord = getInputStr(oldPassWordEdit);
        String newPassWord = getInputStr(newPsdEdit);
        String conP = getInputStr(confirPsdEdit);
        if (checkIsEmpty(phone, "请输入手机号")) {
            return;
        }
        if (!VertifyPhoneNumber.isMobileNO(phone)) {
            ToastUtils.show(activity, "请输入正确的手机号");
            return;
        }
        if (checkIsEmpty(oldPassWord, "请输入现有密码")) {
            return;
        }
        if (checkIsEmpty(newPassWord, "请输入新密码")) {
            return;
        }
        if (checkIsEmpty(conP, "请输入确认密码")) {
            return;
        }
        if (!TextUtils.equals(newPassWord, conP)) {
            ToastUtils.show(activity, "两次输入新密码不一致");
            return;
        }
/*        if(!getInputStr(newPsdEdit).equals(getInputStr(confirPsdEdit))){
            ToastUtils.show(activity, "两次输入密码不一致");
            return;
        }*/

        requestSub(phone, oldPassWord, newPassWord);
    }

    private void requestSub(String phone, String oldPassWord, String newPassWord) {
        final Map<String, String> map = new HashMap<String, String>();
        // [必填] 手机号码
        map.put("phone", phone);
        // [必填] 旧密码
        map.put("oldpsw", oldPassWord);
        // [必填] 新密码
        map.put("newpsw", newPassWord);
        HttpClientUtil.getInstance().doPutAsyn(MODIFY_URL, map, new HttpClientUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                secretResult = result;
                Message msg = Message.obtain();
                msg.what = SUBMIT_CODE;
                mHandler.sendMessage(msg);

            }
        });
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //提交结果
                case SUBMIT_CODE:
                    if (secretResult.contains(IConstant.REQUST_FAIL)) {
//                        ToastUtils.show(context, secretResult);
                        ToastUtils.show(activity, "用户名或密码错误");
                    } else {
                        //无需解析
//                        Gson gson = new Gson();
//                        Response response = gson.fromJson(codeResult, Response.class);
                        ToastUtils.show(activity, "密码修改成功，请登录");
                        String savedSid = SaveSettingUtil.getUserSid(activity);
                        if (null != savedSid) {
                            SaveSettingUtil.removeLoginSid(activity);
                        }
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
        oldPassWordEdit = (EditText) findViewById(R.id.oldpasswprd_edit);
        newPsdEdit = (EditText) findViewById(R.id.newpassword_edit);
        confirPsdEdit = (EditText) findViewById(R.id.confirm_password_edit);
        subButton = (Button) findViewById(R.id.submit_button);
    }
}
