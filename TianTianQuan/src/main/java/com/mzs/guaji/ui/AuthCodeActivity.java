package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

/**
 * 接收手机验证码UI
 * @author lenovo
 *
 */
public class AuthCodeActivity extends GuaJiActivity {

	protected Context context = AuthCodeActivity.this;
	private LinearLayout mBackLayout;
	private EditText mAuthCodeText;
	private Button mAfreshSendButton;
	private Button mNextButton;
	private LinearLayout mRootLayout;
	private String mobile;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.auth_code_layout);
		mobile = getIntent().getStringExtra("mobile");
		mRootLayout = (LinearLayout) findViewById(R.id.auth_code_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.auth_code_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mAuthCodeText = (EditText) findViewById(R.id.auth_code);
		mAfreshSendButton = (Button) findViewById(R.id.auth_code_afresh_send);
		mAfreshSendButton.setOnClickListener(mAfreshSendClickListener);
		mNextButton = (Button) findViewById(R.id.auth_code_next);
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
	 * 重新发送按钮点击事件
	 */
	View.OnClickListener mAfreshSendClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mAfreshSendButton.setEnabled(false);
			if("".equals(mobile)) {
				ToastUtil.showToast(context, R.string.mobile_number_cannot_empty);
				return;
			}
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			mApi.requestGetData(getBindingMobileNumberRequest(mobile), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					if(response != null) {
						if(response.getResponseCode() == 0) {
							mHandler = new Handler();
							mHandler.postDelayed(new SecurityCodeRunnable(mAfreshSendButton), 1000);
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, null);
		}
	};
	
	/**
	 * 下一步按钮点击事件
	 */
	View.OnClickListener mNextClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mNextButton.setEnabled(false);
			String code = mAuthCodeText.getText().toString();
			if("".equals(code)) {
				ToastUtil.showToast(context, R.string.auth_code_cannot_empty);
				return;
			}
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			mApi.requestGetData(getSendAuthCodeRequest(code), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					mNextButton.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
							ToastUtil.showToast(context, R.string.auth_mobile_succeed);
							LoginUtil.saveMobileNumber(context, mobile);
                            if(response.getGivenScore() != null) {
                                showScoreDialog(response.getGivenScore().getMessage());
                            }else {
                                finish();
                            }
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, null);
		}
	};

    private void showScoreDialog(String message) {
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
                    finish();
                }
            }
        });
        mTextView.setText(message);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }

	private class SecurityCodeRunnable implements Runnable {
		private int mSendTime = 60;
		private Button mAfreshSendButton;
		public SecurityCodeRunnable(Button mAfreshSendButton) {
			this.mAfreshSendButton = mAfreshSendButton;
		}
		
		@Override
		public void run() {
			mSendTime--;
			mAfreshSendButton.setText("重新发送   ("+mSendTime + ")");
			if(mSendTime == 0) {
				mAfreshSendButton.setText("重新发送 ");
				mAfreshSendButton.setEnabled(true);
			}else {
				mHandler.postDelayed(this, 1000);
			}
		}
	}
	
	/**
	 * 发送验证码URL
	 * @return
	 */
	private String getSendAuthCodeRequest(String code) {
		return DOMAIN + "user/mobile_auth.json" + "?code=" + code;
	}
	
	/**
	 * 绑定手机号码URL
	 * @param mobileNumber 手机号码
	 * @return
	 */
	private String getBindingMobileNumberRequest(String mobileNumber) {
		return DOMAIN + "user/mobile_code.json" + "?mobile=" + mobileNumber;
	}
}
