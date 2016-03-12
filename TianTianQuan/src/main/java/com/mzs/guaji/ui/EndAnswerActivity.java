package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.EndAnswer;
import com.mzs.guaji.entity.ShareTemplete;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by wlanjie on 13-12-24. 答题结束UI
 */
public class EndAnswerActivity extends DefaultThirdPartyShareActivity {

	private Context context = EndAnswerActivity.this;
	private LinearLayout mBackLayout;
	private TextView mTitleText;
	private TextView mCorrectCountText;
	private TextView mScoreText;
	private TextView mUseTimeSecondText;
	private Button mShareButton;
	private Button mOneMoreChanceButton;
    private Dialog mDialog;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.end_answer_layout);
		String sessionId = getIntent().getStringExtra("sessionId");
		mBackLayout = (LinearLayout) findViewById(R.id.end_answer_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mTitleText = (TextView) findViewById(R.id.end_answer_title);
		mCorrectCountText = (TextView) findViewById(R.id.end_answer_correct_count_text);
		mScoreText = (TextView) findViewById(R.id.end_answer_scor_text);
		mUseTimeSecondText = (TextView) findViewById(R.id.end_answer_use_time_second_text);
		mShareButton = (Button) findViewById(R.id.end_answer_share);
		mOneMoreChanceButton = (Button) findViewById(R.id.end_answer_one_more_chance);
		mOneMoreChanceButton.setOnClickListener(mOneMoreChanceClickListener);
		execEndAnswer(sessionId);
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
	 * 分享点击事件
	 */
	View.OnClickListener mShareClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
            mDialog.setContentView(R.layout.share_pop);
            Button mCancelLayout = (Button) mDialog.findViewById(R.id.share_pop_cancel);
            LinearLayout mSinaLayout = (LinearLayout) mDialog.findViewById(R.id.share_sina_layout);
            mSinaLayout.setTag(v.getTag());
            mSinaLayout.setOnClickListener(mSinaShareClickListener);
            LinearLayout mTencentLayout = (LinearLayout) mDialog.findViewById(R.id.share_tencent_layout);
            mTencentLayout.setTag(v.getTag());
            mTencentLayout.setOnClickListener(mTencentShareClickListener);
            LinearLayout mWeiXinLayout = (LinearLayout) mDialog.findViewById(R.id.share_weixin_layout);
            setWeiXinClickListener(mWeiXinLayout, (ShareTemplete) v.getTag());
            if(!mDialog.isShowing()) {
                mDialog.show();
            }
            mCancelLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
		}
	};

    public void setWeiXinClickListener(LinearLayout mWeiXinLayout, final ShareTemplete mTemplete) {
        View.OnClickListener mWeiXinClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                final String shareText = mTemplete.getShareText();
                shareWeiXinText(shareText);
//                if(mTopic.getImg() == null || "".equals(mTopic.getImg())) {
//                    shareWeiXinText(StringUtil.getShareText(shareText, mTopic.getDesc()));
//                }else {
//                    shareWeiXinPic(mHeaderContentImage, StringUtil.getShareText(shareText, mTopic.getDesc()));
//                }
            }
        };
        mWeiXinLayout.setOnClickListener(mWeiXinClickListener);
    }

    /**
     * 新浪微博分享
     */
    View.OnClickListener mSinaShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ShareTemplete mTemplete = (ShareTemplete) v.getTag();
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            final String shareText = mTemplete.getShareText();
            if(mTemplete.getShareImg() == null || "".equals(mTemplete.getShareImg())) {
                sinaShareText(shareText);
            }else {
                mImageLoader.displayImage(mTemplete.getShareImg(), new ImageView(context), options, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        sinaSharePic(loadedImage, shareText);
                    }
                });
            }
        }
    };

    /**
     * 腾讯微博分享
     */
    View.OnClickListener mTencentShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ShareTemplete mTemplete = (ShareTemplete) v.getTag();
            if(mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            if(mTemplete.getShareImg() == null || "".equals(mTemplete.getShareImg())) {
                tencentShareText(mTemplete.getShareText());
            }else {
                mImageLoader.displayImage(mTemplete.getShareImg(), new ImageView(context), options, new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        tencentSharePic(loadedImage, mTemplete.getShareText());
                    }
                });
            }
        }
    };

	/**
	 * 再来一次点击事件
	 */
	View.OnClickListener mOneMoreChanceClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			EndAnswerActivity.this.finish();
		}
	};

	protected void onDestroy() {
		super.onDestroy();

	};

	private void execEndAnswer(String sessionId) {
		mApi.requestGetData(getEndAnswerRequest(sessionId), EndAnswer.class, new Response.Listener<EndAnswer>() {
			@Override
			public void onResponse(EndAnswer response) {
				if (response != null) {
					if (response.getResponseCode() == 0) {
						mTitleText.setText(response.getMessage());
						mCorrectCountText.setText(String.format(getResources().getString(R.string.answer_correct_count), response.getTotalRightQuestionAmount()));
						mScoreText.setText(response.getTotalCoinsAmout() + "");
						mUseTimeSecondText.setText(String.format(getResources().getString(R.string.use_time_second), response.getTotalAnswerTime()));
                        if(response.getShareTemplete() != null) {
                            mShareButton.setTag(response.getShareTemplete());
                            mShareButton.setOnClickListener(mShareClickListener);
                        }
					} else {
						ToastUtil.showToast(context, response.getResponseMessage());
					}
				}
			}
		}, null);
	}
	/**
	 * 结束答题URL
	 * 
	 * @param sessionId
	 * @return
	 */
	private String getEndAnswerRequest(String sessionId) {
		return DOMAIN + "question/end.json" + "?sessionId=" + sessionId;
	}

}
