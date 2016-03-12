package com.mzs.guaji.ui;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.PersonCenterInfo;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 意见反馈UI
 * @author lenovo
 *
 */
public class OpinionFeedbackActivity extends GuaJiActivity {

	private LinearLayout mRootLayout;
	private LinearLayout mBackLayout;
	private LinearLayout mSaveLayout;
	private ImageView mAvatarImage;
	private EditText mContextText;
	protected Context context = OpinionFeedbackActivity.this;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.opinion_feedback_activity);
		mRootLayout = (LinearLayout) findViewById(R.id.opinion_feedback_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.opinion_feedback_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mSaveLayout = (LinearLayout) findViewById(R.id.opinion_feedback_save);
		mSaveLayout.setOnClickListener(mSaveClickListener);
		mAvatarImage = (ImageView) findViewById(R.id.opinion_feedback_avatar);
		mContextText = (EditText) findViewById(R.id.opinion_feedback_content);
		String avatar = LoginUtil.getUserAvatar(context);
		if(!"".equals(avatar)) {
			mImageLoader.displayImage(avatar, mAvatarImage, ImageUtils.imageLoader(context, 4));
		}else {
			mApi.requestGetData(getRequestUrl(LoginUtil.getUserId(context)), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {
				@Override
				public void onResponse(PersonCenterInfo response) {
					if(response != null && response.getAvatar() != null && !"".equals(response.getAvatar())) {
						mImageLoader.displayImage(response.getAvatar(), mAvatarImage, ImageUtils.imageLoader(context, 4));
					}
 				}
				
			}, this);
		}
	}
	
	/**
	 * 返回按钮点击事件
	 */
	View.OnClickListener mBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(OpinionFeedbackActivity.this, mContextText.getText().toString());
		}
	};
	
	/**
	 * 保存按钮点击事件
	 */
	View.OnClickListener mSaveClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String mContent = mContextText.getText().toString();
			if("".equals(mContent)) {
				ToastUtil.showToast(context, R.string.opinion_feedback_cannot_empty);
				return;
			}
			mSaveLayout.setEnabled(false);
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("message", mContent);
			mApi.requestPostData(getFeebackRequest(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					mSaveLayout.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
							ToastUtil.showToast(context, R.string.opinion_feedback_succeed);
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
	 * 意见反馈URL
	 * @return
	 */
	private String getFeebackRequest() {
		return DOMAIN + "system/feedback.json";
	}
	
	/**
	 * 请求个人资料url
	 * @param id 用户id
	 * @return
	 */
	private String getRequestUrl(long id) {
		return DOMAIN + "user/self.json" + "?id=" + id;
	}

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(OpinionFeedbackActivity.this, mContextText.getText().toString());
    }
}
