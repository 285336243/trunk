package com.socialtv.personcenter;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.wishlist.Keyboard;
import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.topic.CancelDialog;

import java.util.HashMap;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/10/28.
 */
@ContentView(R.layout.modify_password)
public class ModifyPasswordActivity extends DialogFragmentActivity {

    @InjectView(R.id.modify_password_now)
    private EditText nowPasswordEdit;

    @InjectView(R.id.modify_password_setting_new)
    private EditText settingNewPasswordEdit;

    @InjectView(R.id.modify_password_repeat)
    private EditText repeatPasswordEdit;

    @InjectView(R.id.modify_password_submit)
    private View submitView;

    @Inject
    private Activity activity;

    @Inject
    private UserService service;

    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("修改密码");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

        dialog = new CancelDialog(this, new CancelDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismissListener() {
                dialog.dismiss();
                finish();
            }
        });

        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nowPassword = nowPasswordEdit.getText().toString().trim();
                final String settingNewPassword = settingNewPasswordEdit.getText().toString().trim();
                final String repeatPassword = repeatPasswordEdit.getText().toString().trim();
                if (TextUtils.isEmpty(nowPassword)) {
                    ToastUtils.show(activity, "现有密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(settingNewPassword)) {
                    ToastUtils.show(activity, "新密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(repeatPassword)) {
                    ToastUtils.show(activity, "确认密码不能为空");
                    return;
                }
                if (!TextUtils.equals(settingNewPassword, repeatPassword)) {
                    ToastUtils.show(activity, "两次密码不一致");
                    return;
                }
                new ProgressDialogTask<Response>(activity){
                    @Override
                    protected Response run(Object data) throws Exception {
                        final Map<String, String> bodys = new HashMap<String, String>();
                        bodys.put("oldpw", nowPassword);
                        bodys.put("newpw", settingNewPassword);
                        return (Response) HttpUtils.doRequest(service.createModifyPassword(bodys)).result;
                    }

                    @Override
                    protected void onSuccess(Response response) throws Exception {
                        super.onSuccess(response);
                        if (response != null) {
                            if (response.getResponseCode() == 0) {
                                finish();
                            } else {
                                ToastUtils.show(activity, response.getResponseMessage());
                            }
                        }
                    }
                }.start("请稍候");
            }
        });
    }

    private void showCancelDialog() {
        Keyboard.hideSoftInput(nowPasswordEdit);
        final String nowPassword = nowPasswordEdit.getText().toString();
        final String settingNewPassword = settingNewPasswordEdit.getText().toString();
        final String repeatPassword = repeatPasswordEdit.getText().toString();
        if (!TextUtils.isEmpty(nowPassword) || !TextUtils.isEmpty(settingNewPassword) || !TextUtils.isEmpty(repeatPassword)) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showCancelDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BREAK) {
            showCancelDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}
