package com.mzs.guaji.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.ToastUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14-1-14.
 * 忘记密码UI
 */
public class ForGetPasswordActivity extends GuaJiActivity {

    private Context context = ForGetPasswordActivity.this;
    private TextView mBackText;
    private EditText mAccountText;
    private EditText mMobileNumberText;
    private Button mSubmitButton;
    private LinearLayout mFillContentLayout;
    private TextView mSucessText;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.for_get_password_layout);
        mBackText = (TextView) findViewById(R.id.forget_password_back);
        mBackText.setOnClickListener(mBackClickListener);
        mAccountText = (EditText) findViewById(R.id.forget_password_account);
        mMobileNumberText = (EditText) findViewById(R.id.forget_password_mobile_number);
        mSubmitButton = (Button) findViewById(R.id.forget_password_submit);
        mSubmitButton.setOnClickListener(mForGetSubmitClickListener);
        mFillContentLayout = (LinearLayout) findViewById(R.id.forget_password_fill_content);
        mSucessText = (TextView) findViewById(R.id.forget_password_success_text);
    }

    View.OnClickListener mForGetSubmitClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            execSubmitForGetPassword();
        }
    };

    private void execSubmitForGetPassword() {
        String account = mAccountText.getText().toString();
        String mobile = mMobileNumberText.getText().toString();
        if("".equals(account)) {
            ToastUtil.showToast(context, "帐号不能为空");
            return;
        }
        if("".equals(mobile)) {
            ToastUtil.showToast(context, "手机号码不能为空");
            return;
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("account", account);
        headers.put("mobile", mobile);
        mApi.requestPostData(getForGetPasswordRequest(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        mFillContentLayout.setVisibility(View.GONE);
                        mSucessText.setVisibility(View.VISIBLE);
                    }
                }
            }
        }, this);
    }

    private String getForGetPasswordRequest() {
        return DOMAIN + "identity/reset_password.json";
    }
}
