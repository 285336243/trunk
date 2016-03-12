package com.mzs.guaji.ui;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * 绑定手机号码 UI
 * @author lenovo
 *
 */
public class BindingMobileNumberActivity extends GuaJiActivity {

	private LinearLayout mBackLayout;
	private EditText mMobileNumberText;
	private Button mNextButton;
	private LinearLayout mRootLayout;
	protected Context context = BindingMobileNumberActivity.this;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.binding_mobile_number_layout);
		mRootLayout = (LinearLayout) findViewById(R.id.binging_mobile_number_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.binding_mobile_number_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mMobileNumberText = (EditText) findViewById(R.id.binding_mobile);
		mNextButton = (Button) findViewById(R.id.binding_mobile_number_next);
		mNextButton.setOnClickListener(mNextClickListener);
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
	 * 下一步按钮占击事件
	 */
	View.OnClickListener mNextClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mNextButton.setEnabled(false);
			final String mobileNumber = mMobileNumberText.getText().toString();
			if("".equals(mobileNumber)) {
				ToastUtil.showToast(context, R.string.mobile_number_cannot_empty);
				mNextButton.setEnabled(true);
				return;
			}
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			mApi.requestGetData(getBindingMobileNumberRequest(mobileNumber), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					mNextButton.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
							Intent mIntent = new Intent(context, AuthCodeActivity.class);
							mIntent.putExtra("mobile", mobileNumber);
							startActivity(mIntent);
							finish();
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, null);
		}
	};
	
	/**
	 * 绑定手机号码URL
	 * @param mobileNumber 手机号码
	 * @return
	 */
	private String getBindingMobileNumberRequest(String mobileNumber) {
		return DOMAIN + "user/mobile_code.json" + "?mobile=" + mobileNumber;
	}
}
