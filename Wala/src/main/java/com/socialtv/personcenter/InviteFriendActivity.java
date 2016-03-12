package com.socialtv.personcenter;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.socialtv.R;
import com.socialtv.core.TabPagerActivity;

/**
 * Created by wlanjie on 14-8-14.
 * 邀请好友的页面
 */
public class InviteFriendActivity extends TabPagerActivity<InviteFriendPagerAdapter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureTabPager();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("邀请好友");
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected InviteFriendPagerAdapter createAdapter() {
        return new InviteFriendPagerAdapter(this);
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
}
