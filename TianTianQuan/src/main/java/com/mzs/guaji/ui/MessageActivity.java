package com.mzs.guaji.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.android.volley.SynchronizationHttpRequest;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.MessagePagerAdapter;
import com.mzs.guaji.core.AbstractRoboAsyncTask;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.core.TabPagerActivity;
import com.mzs.guaji.entity.Badges;
import com.mzs.guaji.view.BadgeView;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * Created by wlanjie on 14-1-20.
 */
public class MessageActivity extends TabPagerActivity {

    private BadgeView messageBadgeView;

    private BadgeView letterBadgeView;

    private TabWidget tabWidget;

    private MessagePagerAdapter pagerAdapter;

    public int msgCount = -1;

    public int letterCount = -1;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        configureTabPager();
        tabWidget = finder.find(android.R.id.tabs);
        messageBadgeView = new BadgeView(this, tabWidget, 0);
        letterBadgeView = new BadgeView(this, tabWidget, 1);
        TextView backText = finder.find(R.id.tv_message_back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        new AbstractRoboAsyncTask<Badges>(this) {
            @Override
            protected Badges run(Object data) throws Exception {
                SynchronizationHttpRequest<Badges> request = RequestUtils.getInstance().createGet(MessageActivity.this, getBadgesCount(), null);
                request.setClazz(Badges.class);
                return request.getResponse();
            }

            @Override
            protected void onSuccess(Badges badges) throws Exception {
                super.onSuccess(badges);
                if (badges != null) {
                    Map<String, Integer> badgesCounts = badges.getBages();
                    if (badgesCounts != null) {
                        msgCount = badgesCounts.get("msg");
                        letterCount = badgesCounts.get("ppl");
                        if (msgCount > 99) {
                            showMessageBadge("99+");
                        }else {
                            if (msgCount > 0) {
                                showMessageBadge(msgCount + "");
                            }
                        }
                        if (letterCount > 99) {
                            showLetterBadge("99+");
                        } else {
                            if (letterCount > 0) {
                                showLetterBadge(letterCount + "");
                            }
                        }
                    }
                }
            }
        }.execute();
    }

    public void showMessageBadge(String number) {
        messageBadgeView.setText(" " + number + " ");
        messageBadgeView.setTextSize(10);
        messageBadgeView.setBadgeMargin(12, 0);
        messageBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        messageBadgeView.setBackgroundResource(R.drawable.badge_background);
        messageBadgeView.show();
    }

    public void showLetterBadge(String number) {
        letterBadgeView.setText(" "+ number +" ");
        letterBadgeView.setTextSize(10);
        letterBadgeView.setBadgeMargin(12, 0);
        letterBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        letterBadgeView.setBackgroundResource(R.drawable.badge_background);
        letterBadgeView.show();
    }

    public void hideMessageBadge() {
        if(messageBadgeView != null && messageBadgeView.isShown()) {
            messageBadgeView.hide();
        }
    }

    public void hideLetterBadge() {
        if(letterBadgeView != null && letterBadgeView.isShown()) {
            letterBadgeView.hide();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.message_layout;
    }

    @Override
    protected PagerAdapter createAdapter() {
        pagerAdapter = new MessagePagerAdapter(this);
        return pagerAdapter;
    }

    @Override
    protected int getLeftBackground() {
        return R.drawable.message_selector;
    }

    @Override
    protected int getCentreBackground() {
        return R.drawable.private_letter_selector;
    }

    @Override
    protected int getRightBackground() {
        return R.drawable.private_letter_selector;
    }

    @Override
    protected void createTabs() {
        if (host.getTabWidget().getTabCount() > 0) {
            host.setCurrentTab(0);
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(R.layout.private_letter_tab, null);
            ((TextView) view.findViewById(R.id.private_letter_tab_text)).setText(getTitle(i));

            spec.setIndicator(view);
            host.addTab(spec);

            int background;
            if (i == 0)
                background = getLeftBackground();
            else if (i == count - 1)
                background = getCentreBackground();
            else
                background = getRightBackground();
            ((ImageView) view.findViewById(R.id.private_letter_tab_image))
                    .setImageResource(background);
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        super.onTabChanged(tabId);
        if ("tab0".equals(tabId)) {
            MobclickAgent.onEvent(this, "user_message_notice");
        } else if ("tab1".equals(tabId)) {
            MobclickAgent.onEvent(this, "user_message_private");
        }
        if (pagerAdapter != null && pagerAdapter.messageFragment != null && pagerAdapter.messageFragment.deleteMessageParent != null) {
            if (pagerAdapter.messageFragment.deleteMessageParent.getVisibility() == View.VISIBLE) {
                pagerAdapter.messageFragment.deleteMessageParent.setVisibility(View.GONE);
                pagerAdapter.messageFragment.clearAllDelete();
            }
        }

        if (pagerAdapter != null && pagerAdapter.letterFragment != null && pagerAdapter.letterFragment.deleteMessageParent != null) {
            if (pagerAdapter.letterFragment.deleteMessageParent.getVisibility() == View.VISIBLE) {
                pagerAdapter.letterFragment.deleteMessageParent.setVisibility(View.GONE);
                pagerAdapter.letterFragment.clearAllDelete();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (pagerAdapter != null && pagerAdapter.messageFragment != null && pagerAdapter.messageFragment.deleteMessageParent != null) {
                if (pagerAdapter.messageFragment.deleteMessageParent.getVisibility() == View.VISIBLE) {
                    pagerAdapter.messageFragment.deleteMessageParent.setVisibility(View.GONE);
                    pagerAdapter.messageFragment.clearAllDelete();
                    return false;
                }
            }

            if (pagerAdapter != null && pagerAdapter.letterFragment != null && pagerAdapter.letterFragment.deleteMessageParent != null) {
                if (pagerAdapter.letterFragment.deleteMessageParent.getVisibility() == View.VISIBLE) {
                    pagerAdapter.letterFragment.deleteMessageParent.setVisibility(View.GONE);
                    pagerAdapter.letterFragment.clearAllDelete();
                    return false;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private String getBadgesCount() {
        return "system/badges.json";
    }
}
