package com.socialtv.personcenter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.AuthCode;
import com.socialtv.personcenter.entity.ForgetPassword;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-8-15.
 * 绑定手机号的页面
 */
@ContentView(R.layout.binding_mobild)
public class BindingMobileActivity extends DialogFragmentActivity {

    private final static String FORGET_PASSWORD_TITLE = "重置密码";

    @InjectView(R.id.register_flipper)
    private ViewFlipper viewFlipper;

    @InjectView(R.id.register_mobile_number_edit)
    private EditText mobileNumberEdit;

    @InjectView(R.id.register_mobile_number_hint)
    private TextView mobileNumberHint;

    @InjectView(R.id.register_mobile_number_next)
    private View mobileNumberNext;

    @InjectView(R.id.auth_code_edit)
    private EditText authCodeEdit;

    @InjectView(R.id.auth_code_hint)
    private TextView authCodeHint;

    @InjectView(R.id.auth_code_afresh_send)
    private Button authCodeAfreshSend;

    @InjectView(R.id.auth_code_next)
    private Button authCodeNext;

    @Inject
    private Activity activity;

    @Inject
    private UserService service;

    @InjectResource(R.color.white_grey)
    private int whiteGreyColor;

    @InjectResource(R.color.light_grey)
    private int lightGreyColor;

    private int mSendTime = 60;

    private final Handler mHandler = new Handler();

    private final SecurityCodeRunnable runnable = new SecurityCodeRunnable();

    private long toVerify = 1;

    private String mobile;

    private boolean isBack = false;

    private String title;

    private int ignoreCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        title = getStringExtra(IConstant.BINDING_MOBILE_TITLE);
        if (!TextUtils.equals(FORGET_PASSWORD_TITLE, title)) {
            actionBar.setTitle("绑定手机号");
        } else {
            actionBar.setTitle(title);
        }

        if (TextUtils.equals(FORGET_PASSWORD_TITLE, title)) {
            ignoreCheck = 0;
        } else {
            ignoreCheck = 1;
        }
        setSupportProgressBarIndeterminateVisibility(false);

        isBack = getIntent().getBooleanExtra(IConstant.IS_BACK, false);

        final SharedPreferences preferences = getSharedPreferences(IConstant.MESSAGE, MODE_PRIVATE);
        mobileNumberHint.setText(preferences.getString(IConstant.REGISTER_MOBILE, ""));
        authCodeHint.setText(preferences.getString(IConstant.VERIFY_CODE_MESSAGE, ""));

        mobileNumberNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authCodeEdit.setText("");
                final String mobileNumber = mobileNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mobileNumber)) {
                    ToastUtils.show(activity, "手机号码不能为空");
                    return;
                }
                if (mobileNumber.length() < 11) {
                    ToastUtils.show(activity, "格式不正确");
                    return;
                }
                if (!mobileNumber.equals(mobile)) {
                    //绑定手机号
                    new ProgressDialogTask<AuthCode>(activity) {
                        /**
                         * Execute task with an authenticated account
                         *
                         * @param data
                         * @return result
                         * @throws Exception
                         */
                        @Override
                        protected AuthCode run(Object data) throws Exception {
                            return (AuthCode) HttpUtils.doRequest(service.createGetAuthCode(mobileNumber, ignoreCheck)).result;
                        }

                        @Override
                        protected void onSuccess(AuthCode authCode) throws Exception {
                            super.onSuccess(authCode);
                            if (authCode != null) {
                                if (authCode.getResponseCode() == 0) {
                                    toVerify = authCode.getToVerify();
                                    if (authCode.getToVerify() == 1) {
                                        mHandler.removeCallbacks(runnable);
                                        mHandler.postDelayed(runnable, 1000);
                                        viewFlipper.showNext();
                                        mobile = mobileNumber;
                                    } else {
                                        finish();
                                    }
                                } else {
                                    ToastUtils.show(activity, authCode.getResponseMessage());
                                }
                            }
                        }
                    }.start("请稍候");
                } else {
                    if (toVerify == 1) {
                        viewFlipper.showNext();
                        if (mSendTime != 0) {
                            mHandler.postDelayed(runnable, 1000);
                        }
                    } else {
                        finish();
                    }
                }
            }
        });

        authCodeNext.setText(getString(R.string.submit));
        //提交的点击事件
        authCodeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobile = mobileNumberEdit.getText().toString().trim();
                final String authCode = authCodeEdit.getText().toString().trim();
                if (TextUtils.isEmpty(authCode)) {
                    ToastUtils.show(activity, "验证码不能为空");
                    return;
                }
                new ProgressDialogTask<Response>(activity) {
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Response run(Object data) throws Exception {
                        final Map<String, String> bodys = new HashMap<String, String>();
                        bodys.put("mobile", mobile);
                        bodys.put("code", authCode);
                        return (Response) HttpUtils.doRequest(service.createVerifyAuthCode(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                if (TextUtils.equals(FORGET_PASSWORD_TITLE, title)) {
                                    resetPasswordRequest();
                                } else {
                                    submitBindingMobile();
                                }
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }

                        }
                    }
                }.start("正在提交");
            }
        });

        //重新发送验证码的点击事件
        authCodeAfreshSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mobileNumber = mobileNumberEdit.getText().toString().trim();
                if (TextUtils.isEmpty(mobileNumber)) {
                    ToastUtils.show(activity, "手机号码不能为空");
                    return;
                }
                if (mobileNumber.length() < 11) {
                    ToastUtils.show(activity, "格式不正确");
                    return;
                }
                mSendTime = 60;
                mHandler.postDelayed(runnable, 1000);
                new ProgressDialogTask<AuthCode>(activity) {
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected AuthCode run(Object data) throws Exception {
                        return (AuthCode) HttpUtils.doRequest(service.createGetAuthCode(mobileNumber, ignoreCheck)).result;
                    }

                    @Override
                    protected void onSuccess(AuthCode authCode) throws Exception {
                        super.onSuccess(authCode);
                        if (authCode != null) {
                            if (authCode.getResponseCode() == 0) {

                            } else {
                                ToastUtils.show(activity, authCode.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        });
    }

    /**
     * 重新设置密码的请求
     */
    private void resetPasswordRequest() {
        final String mobileNumber = mobileNumberEdit.getText().toString().trim();
        //忘记密码
        new AbstractRoboAsyncTask<ForgetPassword>(activity) {
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected ForgetPassword run(Object data) throws Exception {
                final Map<String, String> bodys = new HashMap<String, String>();
                bodys.put("mobile", mobileNumber);
                return (ForgetPassword) HttpUtils.doRequest(service.createForgetPassword(bodys)).result;
            }

            @Override
            protected void onSuccess(ForgetPassword forgetPassword) throws Exception {
                super.onSuccess(forgetPassword);
                if (forgetPassword != null) {
                    if (forgetPassword.getResponseCode() == 0) {
                        startActivity(new Intents(activity, ForgetPasswordResultActivity.class).add(IConstant.MESSAGE, forgetPassword.getMessage()).toIntent());
                        finish();
                    } else {
                        ToastUtils.show(activity, forgetPassword.getResponseMessage());
                    }
                }
            }
        }.execute();
    }

    /**
     * 提交绑定手机号的请求
     */
    private void submitBindingMobile() {
        final String mobile = mobileNumberEdit.getText().toString().trim();
        final Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("mobile", mobile);
        new AbstractRoboAsyncTask<Response>(this){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(service.createBindingMobile(bodys)).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        authCodeEdit.setText("");
                        LoginUtil.saveUserMobile(activity, mobile);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }
        }.execute();
    }

    /**
     * 计时的Runnable
     */
    private class SecurityCodeRunnable implements Runnable {
        @Override
        public void run() {
            mSendTime--;
            authCodeAfreshSend.setClickable(false);
            authCodeAfreshSend.setTextColor(lightGreyColor);
            authCodeAfreshSend.setText("重新发送(" + mSendTime + ")");

            if(mSendTime == 0) {
                authCodeAfreshSend.setTextColor(whiteGreyColor);
                authCodeAfreshSend.setText("重新发送");
                authCodeAfreshSend.setClickable(true);
            }else {
                mHandler.postDelayed(this, 1000);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                int displayedChild = viewFlipper.getDisplayedChild();
                if (displayedChild != 0) {
                    if (displayedChild == 1) {
                        mHandler.removeCallbacks(runnable);
                    }
                    viewFlipper.showPrevious();
                } else {
                    if (!isBack)
                        finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isBack) {
                return false;
            }
            int displayedChild = viewFlipper.getDisplayedChild();
            if (displayedChild != 0) {
                if (displayedChild == 1) {
                    mHandler.removeCallbacks(runnable);
                }
                viewFlipper.showPrevious();
                return false;
            } else {
                if (!isBack)
                    finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
