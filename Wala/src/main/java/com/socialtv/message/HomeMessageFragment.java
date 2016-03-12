package com.socialtv.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TabWidget;

import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.TabPagerFragment;
import com.socialtv.home.HomeActivity;
import com.socialtv.home.entity.Badges;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.socialtv.view.BadgeView;

import java.util.Map;

/**
 * Created by wlanjie on 14-7-2.
 * 首页中tab上的消息块
 */
public class HomeMessageFragment extends TabPagerFragment<HomeMessagePagerAdapter> {

    @Inject
    private MessageServices services;

    private BadgeView messageBadgeView;

    private BadgeView letterBadgeView;

    public int msgCount = -1;

    public int letterCount = -1;

    private final Handler timerHandler = new Handler();

    private LoginBroadcastReceiver receiver;

    private OnVisibleRefreshListener refreshMessageListener;

    private OnVisibleRefreshListener refreshPrivateLetterListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureTabPager();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        TabWidget tabWidget = (TabWidget) view.findViewById(android.R.id.tabs);
        messageBadgeView = new BadgeView(getActivity(), tabWidget, 0);
        messageBadgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
        messageBadgeView.setBadgeMargin(metrics.widthPixels / 2 / 2 + 16, 16);
        messageBadgeView.setImageResource(R.drawable.icon_notice_tomato);

        letterBadgeView = new BadgeView(getActivity(), tabWidget, 1);
        letterBadgeView.setBadgePosition(BadgeView.POSITION_TOP_LEFT);
        letterBadgeView.setBadgeMargin(metrics.widthPixels / 2 / 2 + 16, 16);
        letterBadgeView.setImageResource(R.drawable.icon_notice_tomato);
        timerHandler.postDelayed(timerRunnable , 0);
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timerHandler.postDelayed(this, 1000 * 15);
            getBadge();
        }
    };

    /**
     * 从服务器获取数据,判断是否在tab上显示红点
     */
    private void getBadge() {
        if (!LoginUtil.isLogin(getActivity())) {
            return;
        }
        new AbstractRoboAsyncTask<Badges>(getActivity()) {
            @Override
            protected Badges run(Object data) throws Exception {
                return (Badges) HttpUtils.doRequest(services.createBadgesRequest()).result;
            }

            @Override
            protected void onSuccess(Badges badges) throws Exception {
                super.onSuccess(badges);
                if (badges != null) {
                    Map<String, Integer> badgesCounts = badges.getBages();
                    if (badgesCounts != null) {
                        msgCount = badgesCounts.get("msg");
                        letterCount = badgesCounts.get("ppl");
                        if (msgCount > 0) {
                            showMessageBadge(msgCount + "");
                            showHomeBadge();
                        } else {
                            hideMessageBadge();
                        }

                        if (letterCount > 0) {
                            showHomeBadge();
                            showLetterBadge(letterCount + "");
                        } else {
                            hideLetterBadge();
                        }
                    }
                }
            }
        }.execute();
    }

    /**
     * 显示首页tab上的红点
     */
    public void showHomeBadge() {
        if (getActivity() != null && getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).showMessageBadge();
        }
    }

    /**
     * 显示消息中tab上的红点
     * @param number
     */
    public void showMessageBadge(String number) {
        if (!messageBadgeView.isShown())
            messageBadgeView.show();
    }

    /**
     * 显示私信中tab上的红点
     * @param number
     */
    public void showLetterBadge(String number) {
        if (!letterBadgeView.isShown())
            letterBadgeView.show();
    }

    /**
     * 隐藏消息上的红点
     */
    public void hideMessageBadge() {
        if(messageBadgeView != null && messageBadgeView.isShown()) {
            messageBadgeView.hide();
        }
    }

    /**
     * 隐藏私信上的红点
     */
    public void hideLetterBadge() {
        if (letterBadgeView != null && letterBadgeView.isShown()) {
            letterBadgeView.hide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timerHandler.removeCallbacks(timerRunnable);
    }

    /**
     * Create pager adapter
     *
     * @return pager adapter
     */
    @Override
    protected HomeMessagePagerAdapter createAdapter() {
        return new HomeMessagePagerAdapter(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.top_pager_with_tabs;
    }

    @Override
    protected int getTabIndicatorView() {
        return R.layout.message_tab;
    }

    @Override
    protected int getLeftBackground() {
        return R.drawable.top_tab_selector;
    }

    @Override
    protected int getCentreBackground() {
        return R.drawable.top_tab_selector;
    }

    @Override
    protected int getRightBackground() {
        return R.drawable.top_tab_selector;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        receiver = new LoginBroadcastReceiver();
        getActivity().registerReceiver(receiver, new IntentFilter(IConstant.USER_LOGIN));
    }

    private class LoginBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            hideLetterBadge();
            hideMessageBadge();
            ((HomeActivity) getActivity()).hideMessageBadge();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (refreshMessageListener != null) {
                refreshMessageListener.onVisibleRefresh();
            }
            if (refreshPrivateLetterListener != null) {
                refreshPrivateLetterListener.onVisibleRefresh();
            }
        }
    }

    public void setOnVisibleRefreshPrivateLetterListener(OnVisibleRefreshListener l) {
        refreshPrivateLetterListener = l;
    }

    public void setOnVisibleRefreshMessageListener(OnVisibleRefreshListener l) {
        refreshMessageListener = l;
    }

    public interface OnVisibleRefreshListener {
        public void onVisibleRefresh();
    }
}
