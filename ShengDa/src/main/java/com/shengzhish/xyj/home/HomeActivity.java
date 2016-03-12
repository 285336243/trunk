package com.shengzhish.xyj.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.shengzhish.xyj.DistributeAPI;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.TabPagerActivity;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;

public class HomeActivity extends TabPagerActivity<HomePagerAdapter> {

    private int currentItem = 0;
    private final static int CANCEL_REQUEST_CODE = 1988;
    private LogoutBroadcastReceiver mLogoutReceiver;
    private RegisterBroadcastReceiver mRegisterReceiver;
    private boolean isRegister = false;

    public HomeActivity() {
    }

    @Override
    protected int getContentView() {
        return R.layout.home;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Object object = applicationInfo.metaData.get("PACKAGE_CHANNEL");
            if (object != null) {
                String marketHouseId = object.toString();
                if (marketHouseId.length() == 1) {
                    marketHouseId = "0" + marketHouseId;
                }
                DistributeAPI.activateDevice(this, marketHouseId, getPackageName(), versionName);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        configureTabPager();
        mLogoutReceiver = new LogoutBroadcastReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(IConstant.ACTIVITY_LOCATION);
        registerReceiver(mLogoutReceiver, mFilter);

        mRegisterReceiver = new RegisterBroadcastReceiver();
        IntentFilter rFilter = new IntentFilter();
        rFilter.addAction(IConstant.REGISTER_BROADCAST);
        rFilter.addAction(IConstant.PERSON_LOGIN);
        registerReceiver(mRegisterReceiver, rFilter);
    }


    /**
     * Create pager adapter
     *
     * @return pager adapter
     */
    @Override
    protected HomePagerAdapter createAdapter() {
        return new HomePagerAdapter(this);
    }

    @Override
    public void onTabChanged(String tabId) {
        if (isRegister) {
            updateCurrentItem(2);
            host.setCurrentTab(2);
            isRegister = false;
            return;
        }
        if ("tab2".equals(tabId)) {
            if (!LoginUtilSh.isLogin(this)) {
                startActivityForResult(new Intents(this, LoginActivity.class).toIntent(), CANCEL_REQUEST_CODE);
            } else {

                updateCurrentItem(host.getCurrentTab());
            }
        } else {

            updateCurrentItem(host.getCurrentTab());
            currentItem = host.getCurrentTab();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            host.setCurrentTab(currentItem);//
        } else if (resultCode == RESULT_OK) {
            host.setCurrentTab(2);
            pager.setCurrentItem(2);
        }
    }

    @Override
    protected int getLeftBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getCentreBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getRightBackground() {
        return R.drawable.tab_selector;
    }

    @Override
    protected int getTabIcon(int position) {
        if (position == 0) {
            return R.drawable.home_selector;
        } else if (position == 1) {
            return R.drawable.activity_selector;
        } else if (position == 2) {
            return R.drawable.persionalcore_selector;
        }
        return super.getTabIcon(position);
    }


    private class LogoutBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ((IConstant.ACTIVITY_LOCATION).equals(intent.getAction())) {
                    host.setCurrentTab(0);
                    pager.setCurrentItem(0);
                }
            }
        }
    }

    private class RegisterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if(IConstant.PERSON_LOGIN.equals(intent.getAction())){
                    isRegister = true;
                }

                if (IConstant.REGISTER_BROADCAST.equals(intent.getAction()) ) {
//                    updateCurrentItem(2);
                    isRegister = true;
                    host.setCurrentTab(2);
                    pager.setCurrentItem(2);
                }

            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLogoutReceiver != null) {
            this.unregisterReceiver(mLogoutReceiver);
        }
        if (mRegisterReceiver != null) {
            this.unregisterReceiver(mRegisterReceiver);
        }
    }
}
