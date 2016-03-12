package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.SynchronizationHttpRequest;
import com.mzs.guaji.R;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.core.ThirdPartyShareActivity;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.Badges;
import com.mzs.guaji.entity.UpdateVerson;
import com.mzs.guaji.fragment.HomeFragment;
import com.mzs.guaji.fragment.PersonalCenterFragment;
import com.mzs.guaji.fragment.ShopFragment;
import com.mzs.guaji.topic.TopicFragment;
import com.mzs.guaji.util.AppManager;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.CacheRepository;
import com.mzs.guaji.util.GpsInfo;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.UpgradeVerson;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 主页面
 * 
 * @author lenovo
 * 
 */
public class MainActivity extends ThirdPartyShareActivity {
    private Context context = MainActivity.this;
	private long firstTime = 0;
	private Intent intent;
	private RadioGroup mRadioGroup;
	private FragmentManager mFragmentManager;
    private HomeFragment mHomeFragment;
	private TopicFragment mToPicFragment;
	private ShopFragment mShopFragment;
	private PersonalCenterFragment mMyInfoFragment;
	private int id;
	public long userId;
	private LogoutBroadcastReceiver mLogoutReceiver;
    private LoginBroadcastReceiver mLoginReceiver;
    private Fragment mLastShowFragment;
    private FragmentTransaction mTransaction;
    private Dialog mDialog;
    private ImageView badgeImage;
    private GuaJiAPI mApi;
    private CacheRepository mRepository;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        mApi = GuaJiAPI.newInstance(this);
        mRepository = CacheRepository.getInstance().fromContext(context);
        GpsInfo gpsInfo = GpsInfo.getInstance(this);
        gpsInfo.getLocation();
//        MobclickAgent.setDebugMode( true );
//      SDK在统计Fragment时，需要关闭Activity自带的页面统计，
//		然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
//		MobclickAgent.setAutoLocation(true);
//		MobclickAgent.setSessionContinueMillis(1000);

        MobclickAgent.updateOnlineConfig(this);
//        JPushInterface.setAliasAndTags(getApplicationContext(), "wlanjie", null, new TagAliasCallback() {
//            @Override
//            public void gotResult(int i, String s, Set<String> strings) {
////                System.out.println(s);
//            }
//        });
		mLogoutReceiver = new LogoutBroadcastReceiver();
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(BroadcastActionUtil.LOGOUT_ACTION);
		registerReceiver(mLogoutReceiver, mFilter);

        mLoginReceiver = new LoginBroadcastReceiver();
        IntentFilter mLoginFilter = new IntentFilter();
        mLoginFilter.addAction(BroadcastActionUtil.LOGIN_ACTION);
        registerReceiver(mLoginReceiver, mLoginFilter);

        badgeImage = (ImageView) findViewById(R.id.main_badge);
		mRadioGroup = (RadioGroup) findViewById(R.id.main_tab_group);
		id = mRadioGroup.getCheckedRadioButtonId();
		mRadioGroup.setOnCheckedChangeListener(checkedChangeListener);
		mFragmentManager = getSupportFragmentManager();
        mTransaction = mFragmentManager.beginTransaction();
        if(mHomeFragment == null) {
            mHomeFragment = new HomeFragment();
            mLastShowFragment = new ShopFragment();
            mTransaction.add(R.id.main_tabcontent, mHomeFragment);
            mTransaction.commitAllowingStateLoss();
        }
//		intent = new Intent(this, NetWorkChangeService.class);
//		startService(intent);
        mApi.requestGetData(getNewVersionRequest(), UpdateVerson.class, new Response.Listener<UpdateVerson>() {
            @Override
            public void onResponse(UpdateVerson response) {
                if(response != null) {
                    if(response.getResponseCode() == 0) {
                        int localVersion = UpgradeVerson.getLocalVersionName(context);
                        int serverVersion = response.getVersionCode();
                        if (serverVersion > localVersion) {
                            UpgradeVerson.createUpdateDialog(context, response.getUpgradeUrl(), response.getVersionNo(), response.getUpgradeMsg());
                        }
//                        if(!localVersion.equals(response.getVersionNo())) {
//                            UpgradeVerson.createUpdateDialog(context, response.getUpgradeUrl(), response.getVersionNo(), response.getUpgradeMsg());
//                        }
                    }
                }
            }
        }, null);
//        LoginUtil.saveFirstLogin(context, 1);
        getBadgeCount();
	}

    RadioGroup.OnCheckedChangeListener checkedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
            switch (checkId) {
                case R.id.main_tab_tv_circle:
                    id = R.id.main_tab_tv_circle;
                    mTransaction = mFragmentManager.beginTransaction();
                    if(mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                        mTransaction.add(R.id.main_tabcontent, mHomeFragment);
                    }
                    mHomeFragment.setEnabledTrue();
                    addFragmentToStack(mHomeFragment, mTransaction);
                    MobclickAgent.onEvent(context, "home");
                break;

                case R.id.main_tab_topic:
                    id = R.id.main_tab_topic;
                    mTransaction = mFragmentManager.beginTransaction();
                    if(mToPicFragment == null) {
                        mToPicFragment = new TopicFragment();
                        mTransaction.add(R.id.main_tabcontent, mToPicFragment);
                    }
                    mHomeFragment.setEnabledFalse();
                    addFragmentToStack(mToPicFragment, mTransaction);
                    MobclickAgent.onEvent(context, "topic");
                    break;
                case R.id.main_tab_shop:
                    id = R.id.main_tab_shop;
                    mTransaction = mFragmentManager.beginTransaction();
                    if(mShopFragment == null) {
                        mShopFragment = new ShopFragment();
                        mTransaction.add(R.id.main_tabcontent, mShopFragment);
                    }
                    mHomeFragment.setEnabledFalse();
                    addFragmentToStack(mShopFragment, mTransaction);
                    MobclickAgent.onEvent(context, "pointcenter");
                    break;
                case R.id.main_tab_my:
                    if(LoginUtil.isLogin(context)) {
                        mTransaction = mFragmentManager.beginTransaction();
                        if(mMyInfoFragment == null) {
                            mMyInfoFragment = new PersonalCenterFragment();
                            mTransaction.add(R.id.main_tabcontent, mMyInfoFragment);
                        }
                        handler.sendEmptyMessageDelayed(0, 1000*100);
                        badgeImage.setVisibility(View.GONE);
                        mHomeFragment.setEnabledFalse();
                        addFragmentToStack(mMyInfoFragment, mTransaction);
                    }else {
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        startActivityForResult(loginIntent, 0);
                    }
                    MobclickAgent.onEvent(context, "user");
                    break;
            }
        }
    };

    private void addFragmentToStack(Fragment mFragment, FragmentTransaction mTransaction) {
        if(mLastShowFragment != null) {
            mTransaction.hide(mLastShowFragment);
            mTransaction.show(mFragment);
            mLastShowFragment = mFragment;
            mTransaction.commitAllowingStateLoss();
        }
    }


	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent data) {
		if(resultCode == 1) {
			((RadioButton) findViewById(id)).setChecked(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
				firstTime = secondTime;
				return true;
			} else {
				if(intent != null) {
					stopService(intent);
				}
                AppManager.getAppManager().AppExit(context);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
	protected void onDestroy() {
		super.onDestroy();
        if(mLogoutReceiver != null) {
            unregisterReceiver(mLogoutReceiver);
        }
        if(mLoginReceiver != null) {
            unregisterReceiver(mLoginReceiver);
        }
	}

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            getBadgeCount();
            handler.sendEmptyMessageDelayed(0, 1000*100);
//            ((RadioButton) findViewById(R.id.main_tab_my)).setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.icon_shop_tj) , null, null);
        }
    };

    private void getBadgeCount() {
        new AsyncTask<Void, Void, Badges>(){
            @Override
            protected Badges doInBackground(Void... params) {
                SynchronizationHttpRequest<Badges> request = RequestUtils.getInstance().createGet(context, getBadgesCount(), null);
                request.setClazz(Badges.class);
                return request.getResponse();
            }

            @Override
            protected void onPostExecute(Badges badges) {
                super.onPostExecute(badges);
                if (badges != null) {
                    Map<String, Integer> badgesCounts = badges.getBages();
                    if (badgesCounts != null) {
                        long msgCount = badgesCounts.get("msg");
                        long letterCount = badgesCounts.get("ppl");
                        if (msgCount>0 || letterCount > 0) {
                            badgeImage.setVisibility(View.VISIBLE);
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, "msgCount", msgCount);
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, "letterCount", letterCount);
                        } else {
                            badgeImage.setVisibility(View.GONE);
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, "msgCount", 0L);
                            mRepository.putLong(LoginUtil.LOGIN_STATE_NAME, "letterCount", 0L);
                        }
                    }
                }
            }
        }.execute();
    }
	
	/**
	 * 退出登录的广播,接收到退出到广播时, 选中主面tab
	 * @author lenovo
	 *
	 */
	private class LogoutBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null) {
				if("com.mzs.guaji.logout".equals(intent.getAction())) {
					mRadioGroup.check(R.id.main_tab_tv_circle);
				}
			}
		}
	}

    /**
     * 登录成功时的广播
     */
    private class LoginBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && BroadcastActionUtil.LOGIN_ACTION.equals(intent.getAction())) {
                ((RadioButton) findViewById(R.id.main_tab_my)).setChecked(true);
                mTransaction = mFragmentManager.beginTransaction();
                mMyInfoFragment = new PersonalCenterFragment();
                mTransaction.add(R.id.main_tabcontent, mMyInfoFragment);
                addFragmentToStack(mMyInfoFragment, mTransaction);
            }
        }
    }

    /**
     * 检查新版本
     * @return
     */
    private String getNewVersionRequest() {
        return DOMAIN + "system/version.json" + "?p=android";
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        mHomeFragment.showPopupWindow();
    }

    private String getBadgesCount() {
        return "system/badges.json";
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            if(mRepository.getLong(LoginUtil.CONFIG, LoginUtil.WINDOW_FIRST_OPEN) == -1) {
                mHomeFragment.showPopupWindow();
                mRepository.putLong(LoginUtil.CONFIG, LoginUtil.WINDOW_FIRST_OPEN, 0L);
//                LoginUtil.saveFirstLogin(context, 1);
            }
        }
    }
}
