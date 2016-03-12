package com.socialtv.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TabWidget;
import android.widget.Toast;

import com.socialtv.R;
import com.socialtv.WalaApplication;
import com.socialtv.core.TabPagerActivity;
import com.socialtv.util.AppManager;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.MD5Util;
import com.socialtv.view.BadgeView;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * Created by wlanjie on 14-6-20.
 *
 * 主页面
 */
public class HomeActivity extends TabPagerActivity<HomePagerAdapter> {

    private TabWidget widget;
    private LogoutBroadcastReceiver loginOutReceiver;

    private long firstTime = 0;

    public boolean isShowUpdateDialog = false;

    private BadgeView badgeView;

    private OnPageSelected listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //信鸽配置
        //开启debug
        XGPushConfig.enableDebug(this, IConstant.DEBUG);
        //注册信鸽
        WalaApplication application = (WalaApplication) getApplication();
        XGPushManager.registerPush(getApplicationContext(), application.getUniqueId());

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        if (adapter == null) {
            configureTabPager();
        }
        if (LoginUtil.isLogin(this)) {
            pager.setCurrentItem(0);
        } else {
            pager.setCurrentItem(1);
        }
        pager.setOffscreenPageLimit(4);
        widget = (TabWidget) findViewById(android.R.id.tabs);

        badgeView = new BadgeView(this, widget, 2);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeView.setImageResource(R.drawable.icon_pointernotice_tomato);

        if (!LoginUtil.isLogin(this)) {
            hide(widget);
        }
        loginOutReceiver = new LogoutBroadcastReceiver();
        registerReceiver(loginOutReceiver, new IntentFilter(IConstant.SETTING_EXIT));
    }

    @Override
    public void onPageSelected(int position) {
        super.onPageSelected(position);
        if (position == 0) {
            if (listener != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                listener.onPageSelected(position);
            }
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setTitle(getTitle(position));
        }

        if (position == 2) {
            hideMessageBadge();
        }
    }

    /**
     * 获取Viewpager当前的Item
     * @return
     */
    public int getViewPagerCurrentItem() {
        return pager.getCurrentItem();
    }

    /**
     * 设置Viewpager的监听事件
     * @param l
     */
    public void setOnPageSelected(OnPageSelected l) {
        listener = l;
    }

    public interface OnPageSelected {
        public void onPageSelected(int position);
    }

    /**
     * 显示tabhost上的红点
     */
    public void showMessageBadge() {
        if (badgeView != null && !badgeView.isShown()) {
            badgeView.show();
        }
    }

    /**
     * 隐藏tabhost上的红点
     */
    public void hideMessageBadge() {
        if (badgeView != null && badgeView.isShown())
            badgeView.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loginOutReceiver != null)
            unregisterReceiver(loginOutReceiver);
//        XGPushManager.unregisterPush(getApplicationContext());
    }

    /**
     * 显示tabhost
     */
    public void showHost() {
        show(widget);
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

    /**
     * 设置tab的Icon
     * @param position
     * @return
     */
    @Override
    protected int getTabIcon(int position) {
        if (position == 0) {
            return R.drawable.icon_feed_tomato;
        } else if (position == 1) {
            return R.drawable.icon_discovery_tomato;
        } else if (position == 2) {
            return R.drawable.icon_msg_tomato;
        } else if (position == 3) {
            return R.drawable.icon_profile_tomato;
        }
        return super.getTabIcon(position);
    }

    /**
     * 监听back键,设置按两次退出程序
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                AppManager.getAppManager().AppExit(this);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 退出登录的广播
     */
    private class LogoutBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ((IConstant.SETTING_EXIT).equals(intent.getAction())) {
                    host.setCurrentTab(1);
                    pager.setCurrentItem(1);
                    hide(widget);
                }
            }
        }
    }
}
