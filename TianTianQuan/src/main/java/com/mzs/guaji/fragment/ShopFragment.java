package com.mzs.guaji.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.EmptyAdapter;
import com.mzs.guaji.adapter.ShopsAdapter;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.DetailShop;
import com.mzs.guaji.entity.ExchangeShop;
import com.mzs.guaji.entity.Shops;
import com.mzs.guaji.entity.SimpleShop;
import com.mzs.guaji.entity.ＤefiniteShopDetail;
import com.mzs.guaji.ui.LoginActivity;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.Rotate3dAnimation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

/**
 * 商城Fragment
 * @author wlanjie
 *
 */
public class ShopFragment extends GuaJiFragment {

	private Context context;
	private ShopsAdapter mAdapter;
	private LinearLayout mRootLayout;
	private ImageView mImageView;
	private TextView mPriceText;
	private RelativeLayout mEcchageLayout;
	private TextView mShopNameText;
	private TextView mDescText;
	private DisplayImageOptions options;
	private EditText mMobileNumberText;
	private Button mNextButton;
	private Handler mHandler;
	private EditText mAuthCodeText;
	private Button mAfreshSendButton;
	private Button mAuthCodeNextButton;
	private String mobile;
	private long shopId;
	private Dialog mDialog;
	private RelativeLayout mShopDetailLayout;
	private RelativeLayout mShopBindingMobileLayout;
	private LinearLayout mDialogRootLayout;
    private ImageButton mCloseButton;
    private ImageButton mBindingMobileCloseButton;
    private TextView mBindingMobileTipsText;
    private ViewFlipper mFlipper;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		options = new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.EXACTLY_STRETCHED).showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_image)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		context = getActivity();
		mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
		mDialog.setCanceledOnTouchOutside(false);
		mDialog.setCancelable(false);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.shop_layout, null);
        mRootView = v;
		mRefreshListView = (PullToRefreshListView) v.findViewById(R.id.shop_list);
        super.onCreateView(inflater, container, savedInstanceState);
        mRootLayout = (LinearLayout) v.findViewById(R.id.shop_root_layout);
		return v;
	}

    @Override
    protected void onInitialization() {
        super.onInitialization();
        mApi.requestGetData(getShopRequest(page, count), Shops.class, new Response.Listener<Shops>() {
            @Override
            public void onResponse(final Shops response) {
                setOnLaodingGone();
                if(response != null && response.getShops() != null && response.getShops().size() > 0) {
                    mAdapter = new ShopsAdapter(context, response.getShops());
                    SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
                    mAnimationAdapter.setAbsListView(mRefreshListView.getRefreshableView());
                    mRefreshListView.setAdapter(mAnimationAdapter);
                    mRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            MobclickAgent.onEvent(context, "pointcenter_product_read");
                            MobclickAgent.onEvent(context, String.valueOf(1));
                            SimpleShop mShop = response.getShops().get((int)id);
                            showDialog(mShop.getId());
                        }
                    });
                }else {
                    mRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                    mRefreshListView.setAdapter(new EmptyAdapter(context, R.string.shops_empty));
                }
            }
        }, this);
    }

    @Override
    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        super.onRefresh(refreshView);
        page = 1;
        mApi.requestGetData(getShopRequest(page, count), Shops.class, new Response.Listener<Shops>() {

            @Override
            public void onResponse(Shops response) {
                mRefreshListView.onRefreshComplete();
                if(response != null && response.getShops() != null && mAdapter != null) {
                    mAdapter.clear();
                    mAdapter.addSimpleShop(response.getShops());
                }
            }
        }, ShopFragment.this);
    }

    @Override
    public void onLastItemVisible() {
        super.onLastItemVisible();
        if(isLastItemVisible) {
            if (!isFootShow) {
                View v = View.inflate(getActivity(), R.layout.list_foot_layout, null);
                mRefreshListView.getRefreshableView().addFooterView(v);
                isFootShow = true;
            }
            return;
        }
        page = page + 1;
        mApi.requestGetData(getShopRequest(page, count), Shops.class, new Response.Listener<Shops>() {

            @Override
            public void onResponse(Shops response) {
                if(response != null && response.getShops() != null && mAdapter != null) {
                    mAdapter.addSimpleShop(response.getShops());
                    if (ListViewLastItemVisibleUtil.isLastItemVisible(page, count, response.getTotal())) {
                        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        isLastItemVisible = true;
                    }
                }
            }
        }, ShopFragment.this);
    }

	/**
	 * 获取商城列表ＵＲＬ
	 * @return
	 */
	private String getShopRequest(long page ,long count) {
		return DOMAIN + "shop/list.json" + "?p=" + page + "&cnt=" + count;
	}
	
	private String getShopDetailRequest(long id) {
		return DOMAIN + "shop/detail.json" + "?id=" + id;
	}

    /**
     * 显示商品详情dialog
     * @param id
     */
	private void showDialog(long id) {
		mDialog.setContentView(R.layout.shop_detail_layout_base);
		mDialogRootLayout = (LinearLayout) mDialog.findViewById(R.id.shop_detail_root_layout);
		mShopDetailLayout = (RelativeLayout) mDialog.findViewById(R.id.shop_detail_item_layout);
		mShopBindingMobileLayout = (RelativeLayout) mDialog.findViewById(R.id.shop_binding_mobile_item_layout);
        mFlipper = (ViewFlipper) mDialog.findViewById(R.id.shop_binding_flipper);
		mImageView = (ImageView) mDialog.findViewById(R.id.shop_detail_image);
		mPriceText = (TextView) mDialog.findViewById(R.id.shop_detail_price);
		mEcchageLayout = (RelativeLayout) mDialog.findViewById(R.id.shop_detail_ecchange_layout);
		mShopNameText = (TextView) mDialog.findViewById(R.id.shop_detail_name);
		mDescText = (TextView) mDialog.findViewById(R.id.shop_detail_desc);
	    mCloseButton = (ImageButton) mDialog.findViewById(R.id.shop_detail_close);

		mCloseButton.setOnClickListener(mCloseClickListener);
	
		mMobileNumberText = (EditText) mDialog.findViewById(R.id.shop_binding_mobile);
		mNextButton = (Button) mDialog.findViewById(R.id.shop_binding_mobile_number_next);
        mBindingMobileTipsText = (TextView) mDialog.findViewById(R.id.shop_binding_mobile_tips_text);
		mNextButton.setOnClickListener(mNextClickListener);
		mBindingMobileCloseButton = (ImageButton) mDialog.findViewById(R.id.shop_binding_mobile_close);
		mBindingMobileCloseButton.setOnClickListener(mCloseClickListener);

		mAuthCodeText = (EditText) mDialog.findViewById(R.id.shop_auth_code_edit);
		mAfreshSendButton = (Button) mDialog.findViewById(R.id.shop_uth_code_afresh_send);
		mAfreshSendButton.setOnClickListener(mAfreshSendClickListener);
		mAuthCodeNextButton = (Button) mDialog.findViewById(R.id.shop_auth_code_next);
		mAuthCodeNextButton.setOnClickListener(mAuthCodeNextClickListener);
		mApi.requestGetData(getShopDetailRequest(id), ＤefiniteShopDetail.class, new Response.Listener<ＤefiniteShopDetail>() {
			@Override
			public void onResponse(ＤefiniteShopDetail response) {
				if(response != null) {
					DetailShop mDetailShop = response.getShop();
					if(mDetailShop != null) {
						shopId = mDetailShop.getId();
						mPriceText.setText(mDetailShop.getPrice());
						mShopNameText.setText(mDetailShop.getName());
						mDescText.setText(mDetailShop.getAbout());
						mImageLoader.displayImage(mDetailShop.getImg(), mImageView, options);
						mEcchageLayout.setTag(response);
						mEcchageLayout.setOnClickListener(mEcchageClickListener);
					}
				}
			}
		}, ShopFragment.this);
		if(!mDialog.isShowing()) {
			mDialog.show();
		}
	
	}
	
	/**
     * Setup a new 3D rotation on the container view.
     *
     * @param start the start angle at which the rotation must begin
     * @param end the end angle of the rotation
     */
    private void applyRotation(float centerX, float centerY, float start, float end) {
        // Find the center of the container
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
//        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView());

        mDialogRootLayout.startAnimation(rotation);
    }
    
    private final class DisplayNextView implements Animation.AnimationListener {

        public void onAnimationStart(Animation animation) {
            mCloseButton.setVisibility(View.GONE);

        }

        public void onAnimationEnd(Animation animation) {
            mShopDetailLayout.post(new SwapViews());
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }
    
    
    private final class SwapViews implements Runnable {
        public void run() {
            final float centerX = mShopDetailLayout.getWidth() / 2.0f;
            final float centerY = mShopDetailLayout.getHeight() / 2.0f;
            mShopDetailLayout.setVisibility(View.GONE);
            mShopBindingMobileLayout.setVisibility(View.VISIBLE);
            mBindingMobileCloseButton.setVisibility(View.VISIBLE);
            Rotate3dAnimation rotation = new Rotate3dAnimation(90, 180, centerX, centerY, 310.0f, false);
            rotation.setDuration(500);
//            rotation.setFillAfter(true);
            rotation.setInterpolator(new DecelerateInterpolator());
            mMobileNumberText.setFocusable(true);
            mDialogRootLayout.startAnimation(rotation);
            rotation.setAnimationListener(new Animation.AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					mMobileNumberText.setVisibility(View.GONE);
                    mBindingMobileTipsText.setVisibility(View.GONE);
                    mNextButton.setVisibility(View.GONE);
                    mBindingMobileCloseButton.setVisibility(View.GONE);

				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
                    mMobileNumberText.setVisibility(View.VISIBLE);
                    mBindingMobileTipsText.setVisibility(View.VISIBLE);
                    mNextButton.setVisibility(View.VISIBLE);
                    mBindingMobileCloseButton.setVisibility(View.VISIBLE);
				}
			});
        }
    }
	
	View.OnClickListener mCloseClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismissDialog();
		}
	};
	
	/**
	 * 兑换点击事件， 如果 exchangemode 为0, 直接兑换
	 * 为1,没有登录
	 * 为2, 积分不足
	 * 为3, 先绑定手机
	 */
	View.OnClickListener mEcchageClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ＤefiniteShopDetail mShopDetail = (ＤefiniteShopDetail) v.getTag();
			switch (mShopDetail.getExchangeMode()) {
			case 0:
				execExchangeShop(mShopDetail.getShop().getId());
				break;

			case 1:
				startLoginUI();
				break;
				
			case 2:
				tipScoreDeficiency();
				break;
			case 3:
//				showBindingMobileWindow();
				applyRotation(mShopDetailLayout.getWidth() / 2.0f, mShopDetailLayout.getHeight() / 2.0f, 0, 90);
				break;
			}
		}
	};
	
	/**
	 * 提示积分不足
	 */
	private void tipScoreDeficiency() {
		ToastUtil.showToast(context, R.string.tip_score_deficiency);
	}
	
	/**
	 * 激活登录界面
	 */
	private void startLoginUI() {
		Intent mIntent = new Intent(context, LoginActivity.class);
		startActivity(mIntent);
        dismissDialog();
	}
	
	/**
	 * 执行兑换商品
	 * @param id
	 */
	private void execExchangeShop(long id) {
		mApi.requestGetData(getExchangeReuqest(id), ExchangeShop.class, new Response.Listener<ExchangeShop>() {
			@Override
			public void onResponse(ExchangeShop response) {
				if(response != null) {
					if(response.getResponseCode() == 0) {
						ToastUtil.showToast(context, response.getOkMsg());
					}else {
						ToastUtil.showToast(context, response.getResponseMessage());
					}
				}
			}
		}, ShopFragment.this);
	}
	
	/**
	 * 兑换商品URL
	 * @param id
	 * @return
	 */
	private String getExchangeReuqest(long id) {
		return DOMAIN + "shop/exchange.json" + "?id=" + id;
	}
	
	/**
	 * dismiss dialog
	 */
	public void dismissDialog() {
		if(mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}
	/**
	 * 验证手机号码,下一步按钮占击事件,请求成功验证验证码
	 */
	View.OnClickListener mNextClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mNextButton.setEnabled(false);
			final String mobileNumber = mMobileNumberText.getText().toString();
			if("".equals(mobileNumber)) {
				ToastUtil.showToast(context, R.string.mobile_number_cannot_empty);
				return;
			}
			mobile = mobileNumber;
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			mApi.requestGetData(getBindingMobileNumberRequest(mobileNumber), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					TipsUtil.dismissPopupWindow();
					mNextButton.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
                            showAuthCodeView();
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, ShopFragment.this);
		}
	};

    private void showAuthCodeView() {
        mFlipper.setInAnimation(context, R.anim.push_up_in);
        mFlipper.setOutAnimation(context, R.anim.push_up_out);
        mFlipper.showNext();
    }
	
	/**
	 * 绑定手机号码URL
	 * @param mobileNumber 手机号码
	 * @return
	 */
	private String getBindingMobileNumberRequest(String mobileNumber) {
		return DOMAIN + "user/mobile_code.json" + "?mobile=" + mobileNumber;
	}
	
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
			}, ShopFragment.this);
		}
	};
	
	/**
	 * 验证码下一步按钮点击事件, 验证成功, 关闭popupwindow,调用兑换商品url
	 */
	View.OnClickListener mAuthCodeNextClickListener = new View.OnClickListener() {
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
							dismissDialog();
							execExchangeShop(shopId);
							ToastUtil.showToast(context, R.string.auth_mobile_succeed);
							LoginUtil.saveMobileNumber(context, mobile);
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, ShopFragment.this);
		}
	};
	
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
}
