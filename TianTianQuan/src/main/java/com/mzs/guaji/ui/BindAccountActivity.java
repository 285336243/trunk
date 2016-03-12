package com.mzs.guaji.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.BindAccount;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;

/**
 * 绑定手机号
 */
public class BindAccountActivity extends SetAvatarActivity {

	TextView welcomeTextView;
	EditText mobileText;
	EditText nicknameText;
	EditText passwdText;
	Button commitBtn;
	Long externalAccountId;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.bind_account_layout);
        LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.bind_account_title_back);
        mBackLayout.setOnClickListener(mBackClickListener);
		mAvatarImage = (ImageView) findViewById(R.id.bind_account_avatar);
		welcomeTextView = (TextView) findViewById(R.id.bind_account_welcome);
		mobileText = (EditText) findViewById(R.id.bind_account_mobile);
		nicknameText = (EditText) findViewById(R.id.bind_account_nick_name);
		passwdText = (EditText) findViewById(R.id.bind_account_pwd);
		commitBtn = (Button) findViewById(R.id.bind_account_commit);

        mAvatarLayout = (RelativeLayout) findViewById(R.id.bind_account_setting_photo);
		String avatar = getIntent().getStringExtra("avatar");
		if (avatar != null) {
			mImageLoader.displayImage(avatar, mAvatarImage, ImageUtils.imageLoader(context, 2));
		}
        mAvatarImage.setOnClickListener(mSettingPhotoListener);
		String welcomeText = getIntent().getStringExtra("welcomeText");
		welcomeTextView.setText(welcomeText);
		String nickname = getIntent().getStringExtra("nickname");
		nicknameText.setText(nickname);
		externalAccountId = getIntent().getLongExtra("externalAccountId", -1);


		commitBtn.setOnClickListener(mBindaccountListener);
	}

	View.OnClickListener mBindaccountListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String mAccountText = mobileText.getText().toString();
			String mNickNameText = nicknameText.getText().toString();
			String mPassword = passwdText.getText().toString();
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
            MultipartRequest<BindAccount> mRequest = mApi.requestMultipartPostData(getBindAccountRequest(), BindAccount.class, new Response.Listener<BindAccount>() {
                @Override
                public void onResponse(BindAccount response) {
                    if (response.getResponseCode() == 0) {
                        LoginUtil.saveLoginState(context, response.getResponseCode());
                        LoginUtil.saveUserId(context, response.getUserId());
                        Intent intent = new Intent();
                        intent.putExtra("userId", response.getUserId());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(context, response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        LoginUtil.saveLoginState(context, response.getResponseCode());
                    }
                }
            }, BindAccountActivity.this);
            mRequest.addMultipartStringEntity("account", mAccountText)
                    .addMultipartStringEntity("password", mPassword)
                    .addMultipartStringEntity("externalAccountId", String.valueOf(externalAccountId))
                    .addMultipartStringEntity("nickname", mNickNameText);
            if(saveFile != null) {
                mRequest.addMultipartFileEntity("avatar", saveFile);
            }
            mApi.addRequest(mRequest);
		}
	};

    private String getBindAccountRequest() {
        return DOMAIN + "identity/bindaccount.json";
    }

}
