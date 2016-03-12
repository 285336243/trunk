package feipai.qiangdan.other;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.github.kevinsawicki.wishlist.Keyboard;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import feipai.qiangdan.R;
import feipai.qiangdan.core.DialogFragmentActivity;
import feipai.qiangdan.core.ToastUtils;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.util.HttpClientUtil;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.SaveSettingUtil;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/10/28.
 */
@ContentView(R.layout.modify_password)
public class ModifyPasswordActivity extends DialogFragmentActivity {

    private static final String MODIFY_PASSWORD_URL = "api/v1/account/changepsw";
    private static final int PASSWPRD_BACK_RESULT = 0xDE;
    @InjectView(R.id.modify_password_now)
    private EditText nowPasswordEdit;

    @InjectView(R.id.modify_password_setting_new)
    private EditText settingNewPasswordEdit;

    @InjectView(R.id.modify_password_repeat)
    private EditText repeatPasswordEdit;

    @InjectView(R.id.modify_password_submit)
    private View submitView;

    @InjectView(R.id.return_back)
    private View returnBack;

    @Inject
    private Activity activity;


    private Dialog dialog;
    private String returnPassResulr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        returnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelDialog();
            }
        });

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
                ProgressDlgUtil.showProgressDlg(activity, "请稍后......");
                final Map<String, String> map = new HashMap<String, String>();
                map.put("sid", SaveSettingUtil.getUserSid(activity));
                map.put("password", nowPassword);// [必填] 原密码
                map.put("newPsw", settingNewPassword);// [必填] 新密码
                HttpClientUtil.getInstance().doPutAsyn(MODIFY_PASSWORD_URL, map, new HttpClientUtil.CallBack() {
                    @Override
                    public void onRequestComplete(String result) {
                        returnPassResulr = result;
                        Message msg = Message.obtain();
                        msg.what = PASSWPRD_BACK_RESULT;
                        mHandler.sendMessage(msg);
                    }
                });

            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PASSWPRD_BACK_RESULT:
                    ProgressDlgUtil.stopProgressDlg();
                    if (returnPassResulr.contains(IConstant.REQUST_FAIL)) {
                        ToastUtils.show(activity, returnPassResulr);
                    } else {
//                        Gson gson = new Gson();
//                        MyInfoBean data = gson.fromJson(returnString, MyInfoBean.class);
                        // status: 1 ， 操作结果(1:成功)
                        ToastUtils.show(activity, "密码修改成功,请重新登录");
                        startActivity(new Intent(activity, LoginActivity.class));
                        finish();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BREAK) {
            showCancelDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
}
