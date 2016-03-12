package com.mzs.guaji.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

/**
 * 修改密码UI
 * @author lenovo
 *
 */
public class ModificationPasswordActivity extends GuaJiActivity {

	private LinearLayout mBackLayout;
	private EditText mOldPasswordText;
	private EditText mNewPasswordText;
	private EditText mAffirmPasswordText;
	private Button mSubmit;
	private Context context = ModificationPasswordActivity.this;
	private LinearLayout mRootLayout;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.modification_password_layout);
		mRootLayout = (LinearLayout) findViewById(R.id.modification_password_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.modification_password_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mOldPasswordText = (EditText) findViewById(R.id.modification_old_pwd);
		mNewPasswordText = (EditText) findViewById(R.id.modification_new_pwd);
		mAffirmPasswordText = (EditText) findViewById(R.id.modification_affirm_pwd);
		mSubmit = (Button) findViewById(R.id.modification_submit);
		mSubmit.setOnClickListener(mSubmitClickListener);
	}
	
	/**
	 * 返回按钮点击事件
	 */
	View.OnClickListener mBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	/**
	 * 提交按钮点击事件
	 */
	View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mSubmit.setEnabled(false);
			String oldPassword = mOldPasswordText.getText().toString();
			String newPassword = mNewPasswordText.getText().toString();
			String affirmPassword = mAffirmPasswordText.getText().toString();
			if("".equals(oldPassword)) {
				ToastUtil.showToast(context, R.string.password_cannot_empty);
				return;
			}
			if("".equals(newPassword)) {
				ToastUtil.showToast(context, R.string.new_password_cannot_empty);
				return;
			}
			if("".equals(affirmPassword)) {
				ToastUtil.showToast(context, R.string.affirm_password_cannot_empty);
				return;
			}
			if(!newPassword.equals(affirmPassword)) {
				ToastUtil.showToast(context, R.string.password_inconformity);
				return;
			}
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("oldpw", oldPassword);
			headers.put("newpw", newPassword);
			mApi.requestPostData(getChangePasswordRequest(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					mSubmit.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
							ToastUtil.showToast(context, R.string.modification_password_succeed);
							finish();
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, ModificationPasswordActivity.this);
		}
	};
	
	private String getChangePasswordRequest() {
		return DOMAINS + "identity/change_password.json";
	}
}
