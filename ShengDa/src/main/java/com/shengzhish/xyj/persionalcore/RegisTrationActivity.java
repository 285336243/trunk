package com.shengzhish.xyj.persionalcore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.ProgressDialogTask;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.http.MultipartRequest;
import com.shengzhish.xyj.persionalcore.entity.Register;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;

import roboguice.inject.InjectView;

/**
 * 个人注册页面
 */
public class RegisTrationActivity extends SetUserHeadPhoto {

    private final static String REGIST_URI = "identity/signup.json";

    @InjectView(R.id.back_imageview)
    private View backImageView;

    @InjectView(R.id.user_header_photo)
    private ImageView userHeaderView;

    @InjectView(R.id.user_nickname)
    private EditText userNickName;

    @InjectView(R.id.user_email)
    private EditText userEmail;

    @InjectView(R.id.user_secret)
    private EditText userSecret;

    @InjectView(R.id.register_commit)
    private Button regidterComit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uaer_registe_layout);
        backImageView.setOnClickListener(onClickListeners);
        regidterComit.setOnClickListener(onClickListeners);
        userHeaderView.setOnClickListener(userPhotoClickListeners);
        mAvatarImage = userHeaderView;
    }

    private void collectionData() {
        final String userNamed = userNickName.getText().toString().trim();
        final String userEmaild = userEmail.getText().toString().trim();
        final String userSecretd = userSecret.getText().toString().trim();
        if (TextUtils.isEmpty(userNamed)) {
            ToastUtils.show(this, "昵称不能为空");
            return;
        } else if (TextUtils.isEmpty(userEmaild)) {
            ToastUtils.show(this, "邮箱不能为空");
            return;
        } else if (TextUtils.isEmpty(userSecretd)) {
            ToastUtils.show(this, "密码不能为空");
            return;
        }

        new ProgressDialogTask<Register>(this) {

            @Override
            protected Register run(Object data) {
                MultipartRequest<Register> request = new MultipartRequest<Register>(REGIST_URI);
                request.setClazz(Register.class);
                request.addMultipartStringEntity("account", userEmaild);
                request.addMultipartStringEntity("password", userSecretd);
                request.addMultipartStringEntity("nickname", userNamed);
                if (saveFile != null) {
                    request.addMultipartFileEntity("pic", saveFile);
                }
                return (Register) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(Register register) throws Exception {
                super.onSuccess(register);
                if (register != null) {
                    if (register.getResponseCode() == 0) {
                        ToastUtils.show(RegisTrationActivity.this, "注册成功");
                        LoginUtilSh.saveUserId(getApplicationContext(), String.valueOf(register.getUserId()));
                        LoginUtilSh.saveLoginState(getApplicationContext(), register.getResponseCode());
                        sendRegisterBroadcast();
                        finish();
                    } else {
                        ToastUtils.show(RegisTrationActivity.this, register.getResponseMessage());
                        LoginUtilSh.saveLoginState(getApplicationContext(), register.getResponseCode());
                    }
                }
            }
        }.start("正在注册");
    }

    private void sendRegisterBroadcast() {
        Intent rIntent = new Intent();
        rIntent.setAction(IConstant.REGISTER_BROADCAST);
        sendBroadcast(rIntent);
    }

    private View.OnClickListener onClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back_imageview:
                    finish();
                    break;
                case R.id.register_commit:
                    collectionData();
                    //提交。。。。。。
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }


}
