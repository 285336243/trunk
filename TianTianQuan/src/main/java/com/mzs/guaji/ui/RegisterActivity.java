package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Register;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.LoginUtil;

import java.io.File;

public class RegisterActivity extends SetAvatarActivity {

	private RelativeLayout mSettingPhotoLayout;
	private EditText mEmailAndPhoneEdit;
	private EditText mNickNameEdit;
	private EditText mPasswordEdit;
	private Button mRegisterCommitButton;
    private boolean isScan;
    private String code;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.register_layout);
        isScan = getIntent().getBooleanExtra("scan", false);
        code = getIntent().getStringExtra("code");
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.register_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mSettingPhotoLayout = (RelativeLayout) findViewById(R.id.register_setting_photo);
		mSettingPhotoLayout.setOnClickListener(mSettingPhotoListener);
		mEmailAndPhoneEdit = (EditText) findViewById(R.id.register_mobile);
		mNickNameEdit = (EditText) findViewById(R.id.register_nick_name);
		mPasswordEdit = (EditText) findViewById(R.id.register_pwd);
		mRegisterCommitButton = (Button) findViewById(R.id.register_commit);
		mRegisterCommitButton.setOnClickListener(mRegisterCommitListener);
		mAvatarImage = (ImageView) findViewById(R.id.register_avatar);
		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return;
			}
		}
	}

	/**
	 * 提交按钮点击事件
	 */
	View.OnClickListener mRegisterCommitListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String mAccountText = mEmailAndPhoneEdit.getText().toString();
			String mNickNameText = mNickNameEdit.getText().toString();
			String mPassword = mPasswordEdit.getText().toString();
			if ("".equals(mAccountText)) {
				Toast.makeText(context, "帐号不能为空", Toast.LENGTH_SHORT).show();
				return;
			} else if ("".equals(mNickNameText)) {
				Toast.makeText(context, "昵称不能为空", Toast.LENGTH_SHORT).show();
				return;
			} else if ("".equals(mPassword)) {
				Toast.makeText(context, "密码不能为空", Toast.LENGTH_SHORT).show();
				return;
			}

			MultipartRequest<Register> request = mApi.requestMultipartPostData(getRequestUrl(), Register.class, new Response.Listener<Register>() {

				@Override
				public void onResponse(Register response) {
					if (response!=null && response.getResponseCode() == 0) {
						Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();
						LoginUtil.saveLoginState(context, response.getResponseCode());
						LoginUtil.saveUserId(context, response.getUserId());
                        if(response.getGivenScore() != null) {
                            showScoreDialog(response.getGivenScore().getMessage(), response);
                        }else {
                            if(isScan) {
                                Intent mIntent = new Intent();
                                mIntent.putExtra("code", code);
                                mIntent.setAction(BroadcastActionUtil.IS_SCAN_ACTION);
                                sendBroadcast(mIntent);
                                finish();
                            }else {
                                Intent mIntent = new Intent();
                                mIntent.putExtra("userId", response.getUserId());
                                mIntent.setAction(BroadcastActionUtil.LOGIN_ACTION);
                                sendBroadcast(mIntent);
                                finish();
                            }
                        }
					} else {
						Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
						LoginUtil.saveLoginState(context, response.getResponseCode());
					}
				}
			}, null);
			request.addMultipartStringEntity("account", mAccountText).addMultipartStringEntity("password", mPassword)
					.addMultipartStringEntity("nickname", mNickNameText);
            if(saveFile != null) {
                request.addMultipartFileEntity("pic", saveFile);
            }
			mApi.addRequest(request);
		}
	};

    private void showScoreDialog(String message, final Register response) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.first_login_dialog);
        TextView mTextView = (TextView) mDialog.findViewById(R.id.first_login_text);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.first_login_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
//                    finish();
                    if(isScan) {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("code", code);
                        mIntent.setAction(BroadcastActionUtil.IS_SCAN_ACTION);
                        sendBroadcast(mIntent);
                        finish();
                    }else {
                        Intent mIntent = new Intent();
                        mIntent.putExtra("userId", response.getUserId());
                        mIntent.setAction(BroadcastActionUtil.LOGIN_ACTION);
                        sendBroadcast(mIntent);
                        finish();
                    }
                }
            }
        });
        mTextView.setText(message);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

	private String getRequestUrl() {
		return DOMAINS + "identity/signup.json";
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }
}
