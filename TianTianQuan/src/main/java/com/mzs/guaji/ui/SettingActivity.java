package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.SystemSetting;
import com.mzs.guaji.entity.UpdateVerson;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.share.OAuthThirdParty;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.util.UpgradeVerson;
import com.tencent.weibo.sdk.android.api.util.Util;

import java.io.File;

/**
 * 设置页面
 * @author lenovo
 *
 */
public class SettingActivity extends DefaultThirdPartyShareActivity {

	protected Context context = SettingActivity.this;
	private LinearLayout mBackLayout;
	private RelativeLayout mModificationBaseInfoLayout;
	private RelativeLayout mModificationPasswordLayout;
	private RelativeLayout mBlckListLayout;
	private RelativeLayout mMobileBindingLayout;
	private RelativeLayout mSinaWeiboBindingLayout;
	private RelativeLayout mTencentWeiboBindingLayout;
	private RelativeLayout mClearCacheLayout;
	private RelativeLayout mCheckUpdateLayout;
	private RelativeLayout mAboutLayout;
	private RelativeLayout mOpinionFeedbackLayout;
	private RelativeLayout mLogoutLayout;
	private LinearLayout mRootLayout;
    private RelativeLayout mContactAddressLayout;
	private long userId;
	private String mobile = "";
	private TextView mMobileNumberText;
	private TextView mMobileBindingText;
    private TextView mSinaNicknameText;
    private TextView mSinaBindingText;
    private TextView mTencentNicknameText;
    private TextView mTencentBindingText;
    private String bindMobileMode;
	
	@Override
	protected void onCreate(Bundle bundle) {
		// TODO Auto-generated method stub
		super.onCreate(bundle);
		setContentView(R.layout.setting_activity);
		userId = getIntent().getLongExtra("userId", -1);
		mobile = getIntent().getStringExtra("mobile");
		mRootLayout = (LinearLayout) findViewById(R.id.setting_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.setting_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mModificationBaseInfoLayout = (RelativeLayout) findViewById(R.id.base_info_modification);
		mModificationBaseInfoLayout.setOnClickListener(mModificationBaseInfoClickListener);
		mModificationPasswordLayout = (RelativeLayout) findViewById(R.id.modification_password);
		mModificationPasswordLayout.setOnClickListener(mModificationPasswordClickListener);
        mContactAddressLayout = (RelativeLayout) findViewById(R.id.setting_contact_address);
        mContactAddressLayout.setOnClickListener(mContactAddressClickListener);
		mBlckListLayout = (RelativeLayout) findViewById(R.id.blcklist);
		mBlckListLayout.setOnClickListener(mBlckListClickListener);
		mMobileBindingLayout = (RelativeLayout) findViewById(R.id.mobile_number_binding);
		mMobileBindingLayout.setOnClickListener(mMobileBindingClickListener);
		mSinaWeiboBindingLayout = (RelativeLayout) findViewById(R.id.sina_weibo_binding);
		mSinaWeiboBindingLayout.setOnClickListener(mSinaWeiboBindingClickListener);
		mTencentWeiboBindingLayout = (RelativeLayout) findViewById(R.id.tencent_weibo_binding);
		mTencentWeiboBindingLayout.setOnClickListener(mTencentWeiboBindingClickListener);
		mClearCacheLayout = (RelativeLayout) findViewById(R.id.clear_cache);
		mClearCacheLayout.setOnClickListener(mClearCacheClickListener);
		mCheckUpdateLayout = (RelativeLayout) findViewById(R.id.check_update);
		mCheckUpdateLayout.setOnClickListener(mCheckUpdateClickListener);
		mAboutLayout = (RelativeLayout) findViewById(R.id.about);
		mAboutLayout.setOnClickListener(mAboutClickListener);
		mOpinionFeedbackLayout = (RelativeLayout) findViewById(R.id.opinion_feedback);
		mOpinionFeedbackLayout.setOnClickListener(mOpinionFeedbackClickListener);
		mLogoutLayout = (RelativeLayout) findViewById(R.id.logout);
		mLogoutLayout.setOnClickListener(mLogoutClickListener);
		mMobileNumberText = (TextView) findViewById(R.id.setting_mobile_number);
		mMobileBindingText = (TextView) findViewById(R.id.setting_mobile_number_binding);
        mSinaNicknameText = (TextView) findViewById(R.id.setting_sina_nickname);
        mSinaBindingText = (TextView) findViewById(R.id.setting_sina_binding_text);
        mTencentNicknameText = (TextView) findViewById(R.id.setting_tencent_nickname);
        mTencentBindingText = (TextView) findViewById(R.id.setting_tencent_binding_text);
        String sinaNickname = mRepository.getString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINANICKNAME);
        if(!TextUtils.isEmpty(sinaNickname)) {
            mSinaNicknameText.setText("(" + sinaNickname + ")");
            mSinaBindingText.setText("取消绑定");
            mSinaBindingText.setTextColor(getResources().getColor(R.color.search_tab_color));
        }
        String qqNickname = mRepository.getString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQNICKNAME);
        if(!TextUtils.isEmpty(qqNickname)) {
            mTencentNicknameText.setText("(" + qqNickname + ")");
            mTencentBindingText.setText("取消绑定");
            mTencentBindingText.setTextColor(getResources().getColor(R.color.search_tab_color));
        }
        mApi.requestGetData(DOMAIN+"system/setting.json", SystemSetting.class, new Response.Listener<SystemSetting>() {
			@Override
			public void onResponse(SystemSetting response) {
				if(response!=null && response.getResponseCode() == 0){
					if(response.getBindMobileMode() != null){
						bindMobileMode = response.getBindMobileMode();
					}
				}
			}
		}, null);
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!"".equals(LoginUtil.getMobileNumber(context))) {
            if(mobile == null) {
                mMobileNumberText.setText("(" + LoginUtil.getMobileNumber(context) + ")");
                mMobileBindingText.setText("取消绑定");
                mMobileBindingText.setTextColor(getResources().getColor(R.color.search_tab_color));
            }else {
                mMobileNumberText.setText("(" + mobile + ")");
                mMobileBindingText.setText("取消绑定");
                mMobileBindingText.setTextColor(getResources().getColor(R.color.search_tab_color));
            }
		}else {
            LoginUtil.clearMobileNumber(context);
        }
 	}

	/**
	 * 返回
	 */
	View.OnClickListener mBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	/**
	 * 基础信息修改
	 */
	View.OnClickListener mModificationBaseInfoClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(context, ModificationBaseInfoActivity.class);
			mIntent.putExtra("userId", userId);
			startActivity(mIntent);
		}
	};
	
	/**
	 * 修改密码
	 */
	View.OnClickListener mModificationPasswordClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(context, ModificationPasswordActivity.class);
			startActivity(mIntent);
		}
	};
	
	/**
	 * 黑名单
	 */
	View.OnClickListener mBlckListClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			
		}
	};

    View.OnClickListener mContactAddressClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, ContactAddressActivity.class);
            startActivity(mIntent);
        }
    };
	
	/**
	 * 手机号码绑定, 如果已绑定,就取消绑定
	 */
	View.OnClickListener mMobileBindingClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            if("".equals(LoginUtil.getMobileNumber(context))) {
            	if("NONE".equals(bindMobileMode)){
            		Intent mIntent = new Intent(context, BindingMobileNumberNoneSecrityCodeActivity.class);
            		startActivity(mIntent);
            	}else{
            		Intent mIntent = new Intent(context, BindingMobileNumberActivity.class);
            		startActivity(mIntent);
            	}
            }else {
                showUnBindDialog("mobile");
            }

		}
	};

	/**
	 * 新浪微博绑定 
	 */
	View.OnClickListener mSinaWeiboBindingClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            long id = mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINAID);
            if(id == -1) {
                doSinaAuthorize(null);
            }else {
                showUnBindDialog("sina");
            }

		}
	};
	
	/**
	 * 腾讯微博绑定
	 */
	View.OnClickListener mTencentWeiboBindingClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            long id = mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQID);
            if(id == -1) {
                doTencentAuthorize(null);
            }else {
                showUnBindDialog("qq");
            }
		}
	};

    /**
     * 请求解除第三方绑定
     * @param externalAccountId
     * @param nicknameKey
     * @param idKey
     */
    private void setUnBindingAccount(long externalAccountId, final String nicknameKey, final String idKey, final TextView mWeiboNicknameText, final TextView mBindingText, final Dialog mDialog) {
        mApi.requestDeleteData(getUnBindingAccountRequest(externalAccountId), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        ToastUtil.showToast(context, "解除绑定成功");
//                        mRepository.clear(OAuthThirdParty.TENCENT);
                        Util.clearSharePersistent(context);
                        mRepository.removeString(LoginUtil.LOGIN_STATE_NAME, nicknameKey);
                        mRepository.removeString(LoginUtil.LOGIN_STATE_NAME, idKey);
                        setBindingText(mWeiboNicknameText, mBindingText, "", false);
                        if(mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, this);
    }

    /**
     * 设置绑定的用户名, 更改绑定文字和颜色
     * @param mWeiboNicknameText
     * @param mBindingText
     * @param nickname
     * @param isBinding
     */
    private void setBindingText(TextView mWeiboNicknameText, TextView mBindingText, String nickname, boolean isBinding) {
        if(isBinding) {
            mWeiboNicknameText.setVisibility(View.VISIBLE);
            mWeiboNicknameText.setText("(" + nickname + ")");
            mBindingText.setText("取消绑定");
            mBindingText.setTextColor(getResources().getColor(R.color.search_tab_color));
        }else {
            mWeiboNicknameText.setVisibility(View.GONE);
            mBindingText.setText("绑定");
            mBindingText.setTextColor(getResources().getColor(R.color.pink));
        }
    }

    /**
     * 绑定成功的回调
     * @param type
     */
    @Override
    protected void bindAccountComplete(String type) {
        super.bindAccountComplete(type);
        if("SINA".equals(type)) {
            setBindingText(mSinaNicknameText, mSinaBindingText, mRepository.getString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINANICKNAME), true);
        }else if("QQ".equals(type)) {
            setBindingText(mTencentNicknameText, mTencentBindingText, mRepository.getString(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQNICKNAME), true);
        }
    }

    private void showUnBindDialog(final String type) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setContentView(R.layout.dialog_view);
        mDialog.setCanceledOnTouchOutside(false);
        TextView mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_title);
        mDialogTitle.setText("提示");
        TextView mInfoText = (TextView) mDialog.findViewById(R.id.update_info);
        mInfoText.setText("是否取消绑定");
        Button mConfirmButton = (Button) mDialog.findViewById(R.id.update_dialog_ok);
        mConfirmButton.setText("暂不取消");
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
                }
            }
        });
        Button mCancelButton = (Button) mDialog.findViewById(R.id.update_dialog_cancel);
        mCancelButton.setText("取消绑定");
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("mobile".equals(type)) {
                    setUnMobile(mDialog);
                }else if("qq".equals(type)) {
                    setUnBindingAccount(mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.QQID),LoginUtil.QQNICKNAME, LoginUtil.QQID, mTencentNicknameText, mTencentBindingText, mDialog);
                }else if("sina".equals(type)) {
                    setUnBindingAccount(mRepository.getLong(LoginUtil.LOGIN_STATE_NAME, LoginUtil.SINAID), LoginUtil.SINANICKNAME, LoginUtil.SINAID, mSinaNicknameText, mSinaBindingText, mDialog);
                }
            }
        });
        if(!mDialog.isShowing()) {
           mDialog.show();
        }
    }

    private void setUnMobile(final Dialog mDialog) {
        mApi.requestDeleteData(getUnBindingMobileRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        mMobileNumberText.setVisibility(View.GONE);
                        mMobileBindingText.setText("绑定");
                        mMobileBindingText.setTextColor(getResources().getColor(R.color.pink));
                        LoginUtil.clearMobileNumber(context);
                        if(mDialog.isShowing()) {
                            mDialog.dismiss();
                        }
                    }else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }
        }, SettingActivity.this);
    }

    /**
	 * 清除缓存 
	 */
	View.OnClickListener mClearCacheClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
            clearFile(new File(Environment.getExternalStorageDirectory(), "GuaJi"));
			mImageLoader.clearDiskCache();
            ToastUtil.showToast(context, R.string.clear_cache_succeed);
		}
	};

    private void clearFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    clearFile(f);
                }
            }
            file.delete();
        }
    }
	
	/**
	 * 检查更新
	 */
	View.OnClickListener mCheckUpdateClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mCheckUpdateLayout.setEnabled(false);
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.update_tips);
			mApi.requestGetData(getNewVersionRequest(), UpdateVerson.class, new Response.Listener<UpdateVerson>() {
				@Override
				public void onResponse(UpdateVerson response) {
					mCheckUpdateLayout.setEnabled(true);
					TipsUtil.dismissPopupWindow();
					if(response != null) {
                        if(response.getResponseCode() == 0) {
                            int localVersion = UpgradeVerson.getLocalVersionName(context);
                            int serverVersion = response.getVersionCode();
                            if (serverVersion > localVersion) {
                                UpgradeVerson.createUpdateDialog(context, response.getUpgradeUrl(), response.getVersionNo(), response.getUpgradeMsg());
                            } else {
                                UpgradeVerson.needNotUpdate(context);
                            }
//                            if(!localVersion.equals(response.getVersionNo())) {
//                                UpgradeVerson.createUpdateDialog(context, response.getUpgradeUrl(), response.getVersionNo(), response.getUpgradeMsg());
//                            }else {
//                                UpgradeVerson.needNotUpdate(context);
//                            }
                        }
					}
				}
			}, SettingActivity.this);
		}
	};
	
	/**
	 * 关于我们
	 */
	View.OnClickListener mAboutClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, WebViewActivity.class);
            intent.putExtra("url", DOMAIN+"system/about.html");
            intent.putExtra("title","关于我们");
            context.startActivity(intent);
		}
	};
	
	/**
	 * 意见反馈
	 */
	View.OnClickListener mOpinionFeedbackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(context, OpinionFeedbackActivity.class);
			startActivity(mIntent);
		}
	};
	
	/**
	 * 退出登录
	 */
	View.OnClickListener mLogoutClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mApi.requestGetData(getLogoutRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
				@Override
				public void onResponse(DefaultReponse response) {
					if(response != null) {
						if(response.getResponseCode() == 0) {
							clearUserInfo();
							sendLogoutBroadcast();
							finish();
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, SettingActivity.this);
		}
	};

    private void clearUserInfo() {
        LoginUtil.clearLoginState(context);
        LoginUtil.clearMobileNumber(context);
        LoginUtil.clearUserAvatar(context);
        mRepository.clear(LoginUtil.LOGIN_STATE_NAME);
        mRepository.clear(OAuthThirdParty.SINA);
//        mRepository.clear(OAuthThirdParty.TENCENT);
        mApi.clearCookie();
    }

	
	private void sendLogoutBroadcast() {
		Intent mIntent = new Intent();
		mIntent.setAction(BroadcastActionUtil.LOGOUT_ACTION);

		sendBroadcast(mIntent);
	}
	
	/**
	 * 退出登录
	 * @return
	 */
	private String getLogoutRequest() {
		return DOMAIN + "identity/signout.json";
	}
	
	/**
	 * 检查新版本
	 * @return
	 */
	private String getNewVersionRequest() {
		return DOMAIN + "system/version.json" + "?p=android";
	}
	
	/**
	 * 解除手机号绑定
	 * @return
	 */
	private String getUnBindingMobileRequest() {
		return DOMAIN + "user/mobile.json";
	}

    /**
     * 解除第三方账号绑定
     */
    private String getUnBindingAccountRequest(long externalAccountId) {
        return DOMAIN + "identity/external_account.json" + "?externalAccountId=" + externalAccountId;
    }
}
