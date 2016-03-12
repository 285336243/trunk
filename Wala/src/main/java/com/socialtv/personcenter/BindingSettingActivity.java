package com.socialtv.personcenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.Intents;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.ExternalAccounts;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;

import roboguice.inject.ContentView;
import roboguice.inject.InjectResource;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-8-15.
 */

@ContentView(R.layout.binding_setting)
public class BindingSettingActivity extends ThirdPartyShareActivity {

    @InjectView(R.id.binding_setting_mobile)
    private View mobileView;

    @InjectView(R.id.binding_setting_mobile_number)
    private TextView mobileNumberText;

    @InjectView(R.id.binding_setting_mobile_binding_text)
    private TextView mobileBindingText;

    @InjectView(R.id.binding_setting_sina)
    private View sinaView;

    @InjectView(R.id.binding_setting_sina_text)
    private TextView sinaBindingText;

    @InjectView(R.id.binding_setting_tencent)
    private View tencentView;

    @InjectView(R.id.binding_setting_tencent_text)
    private TextView tencentBindingText;

    @InjectView(R.id.binding_setting_sina_nickname)
    private TextView sinaNicknameText;

    @InjectView(R.id.binding_setting_tencent_nickname)
    private TextView tencentNicknameText;

    @InjectResource(R.color.white_grey)
    private int whiteGreyColor;

    @InjectResource(R.color.tab_background)
    private int redColor;

    @Inject
    private UserService service;

    @Inject
    private Activity activity;

    private String mobileNumber;

    private ExternalAccounts sinaAccount;

    private ExternalAccounts tencentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("绑定设置");
        setSupportProgressBarIndeterminateVisibility(false);

        sinaAccount = (ExternalAccounts) getSerializableExtra(IConstant.SINA);
        tencentAccount = (ExternalAccounts) getSerializableExtra(IConstant.QQ);

        if (sinaAccount != null) {
            if (!TextUtils.isEmpty(sinaAccount.getNickname())) {
                sinaNicknameText.setText(sinaAccount.getNickname());
            }
            sinaBindingText.setText("已绑定");
            sinaBindingText.setTextColor(whiteGreyColor);
        }

        if (tencentAccount != null) {
            if (!TextUtils.isEmpty(tencentAccount.getNickname())) {
                tencentNicknameText.setText(tencentAccount.getNickname());
            }
            tencentBindingText.setText("已绑定");
            tencentBindingText.setTextColor(whiteGreyColor);
        }

        mobileNumber = LoginUtil.getUserMobile(activity);
        if (!TextUtils.isEmpty(mobileNumber)) {
            mobileNumberText.setText(mobileNumber);
            mobileBindingText.setTextColor(whiteGreyColor);
            mobileBindingText.setText("已绑定");
        } else {
            mobileBindingText.setTextColor(redColor);
            mobileBindingText.setText("绑定");
        }

        mobileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mobileNumber = LoginUtil.getUserMobile(activity);
                if (!TextUtils.isEmpty(mobileNumber)) {
                    showUnBindDialog(new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            mobileUnBind();
                        }
                    });
                } else {
                    startActivityForResult(new Intents(activity, BindingMobileActivity.class).toIntent(), 0);
                }
            }
        });

        sinaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sinaAccount != null) {
                    showUnBindDialog(new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            sinaUnBind();
                        }
                    });
                } else {
                    sinaAuthorize(new DoAuthorize() {
                        @Override
                        public void authorize(ExternalAccounts accounts) {
                            sinaAccount = accounts;
                            if (!TextUtils.isEmpty(accounts.getNickname())) {
                                sinaNicknameText.setText(accounts.getNickname());
                            }
                            sinaBindingText.setText("已绑定");
                            sinaBindingText.setTextColor(whiteGreyColor);
                        }
                    });
                }
            }
        });

        tencentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tencentAccount != null) {
                    showUnBindDialog(new OnConfirmListener() {
                        @Override
                        public void onConfirm() {
                            tencentUnBind();
                        }
                    });
                } else {
                     tencentAuthorize(new DoAuthorize() {
                         @Override
                         public void authorize(ExternalAccounts accounts) {
                             tencentAccount = accounts;
                             if (!TextUtils.isEmpty(accounts.getNickname())) {
                                 tencentNicknameText.setText(accounts.getNickname());
                             }
                             tencentBindingText.setText("已绑定");
                             tencentBindingText.setTextColor(whiteGreyColor);
                         }
                     });
                }
            }
        });
    }

    private void mobileUnBind() {
        new ProgressDialogTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(service.createDeleteBindingMobile()).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        LoginUtil.clearMobile(activity);
                        mobileNumberText.setText("");
                        mobileBindingText.setTextColor(redColor);
                        mobileBindingText.setText("绑定");
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                System.out.println(e.getMessage());
            }
        }.start("请稍候");
    }

    private void sinaUnBind() {
        new ProgressDialogTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(service.createDeleteExternalAccount(sinaAccount.getExternalAccountId())).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        sinaBindingText.setText("绑定");
                        sinaBindingText.setTextColor(redColor);
                        sinaAccount = null;
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }
        }.start("请稍候");
    }

    private void tencentUnBind() {
        new ProgressDialogTask<Response>(activity){
            /**
             * Execute task with an authenticated account
             *
             * @param data
             * @return result
             * @throws Exception
             */
            @Override
            protected Response run(Object data) throws Exception {
                return (Response) HttpUtils.doRequest(service.createDeleteExternalAccount(tencentAccount.getExternalAccountId())).result;
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        tencentBindingText.setText("绑定");
                        tencentBindingText.setTextColor(redColor);
                        tencentAccount = null;
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }
        }.start("请稍候");
    }

    private void showUnBindDialog(final OnConfirmListener listener) {
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.un_bind_tip_dialog);
        View cancelView = dialog.findViewById(R.id.un_bind_tip_dialog_cancel);
        View confirmView = dialog.findViewById(R.id.un_bind_tip_dialog_ok);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onConfirm();
                    dialog.dismiss();
                }
            }
        });
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private interface OnConfirmListener {
        public void onConfirm();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode != RESULT_CANCELED) {
            final String mobile = LoginUtil.getUserMobile(activity);
            if (!TextUtils.isEmpty(mobile)) {
                mobileNumberText.setText(mobile);
                mobileBindingText.setTextColor(whiteGreyColor);
                mobileBindingText.setText("已绑定");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
