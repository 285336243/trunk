package com.mzs.guaji.ui;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

/**
 * 绑定手机号码 UI
 * 
 * @author lenovo
 * 
 */
public class BindingMobileNumberNoneSecrityCodeActivity extends GuaJiActivity {

	private LinearLayout mBackLayout;
	private EditText mMobileNumberText;
	private Button mMobileSubmit;
	private LinearLayout mRootLayout;
	protected Context context = BindingMobileNumberNoneSecrityCodeActivity.this;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.binding_mobile_number_none_secritycode_layout);
		mRootLayout = (LinearLayout) findViewById(R.id.binging_mobile_number_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.binding_mobile_number_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mMobileNumberText = (EditText) findViewById(R.id.binding_mobile);
		mMobileSubmit = (Button) findViewById(R.id.binding_mobile_number_submit);
		mMobileSubmit.setOnClickListener(mSubmitClickListener);
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
	 * 提交按钮占击事件
	 */
	View.OnClickListener mSubmitClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mMobileSubmit.setEnabled(false);
			final String mobileNumber = mMobileNumberText.getText().toString();
			if ("".equals(mobileNumber)) {
				ToastUtil.showToast(context, R.string.mobile_number_cannot_empty);
				mMobileSubmit.setEnabled(true);
				return;
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("mobile", mobileNumber);
			mApi.requestPostData(DOMAIN + "user/mobile.json", DefaultReponse.class, map, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
                    mMobileSubmit.setEnabled(true);
					if (response != null) {
						if (response.getResponseCode() == 0) {
							ToastUtil.showToast(context, R.string.auth_mobile_succeed);
							LoginUtil.saveMobileNumber(context, mobileNumber);
							finish();
						} else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, null);
		}
	};

}
