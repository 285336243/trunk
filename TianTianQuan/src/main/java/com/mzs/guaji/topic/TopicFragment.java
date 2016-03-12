package com.mzs.guaji.topic;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.mzs.guaji.R;
import com.mzs.guaji.core.DialogFragment;
import com.mzs.guaji.core.ViewPager;

import roboguice.inject.InjectView;

public class TopicFragment extends DialogFragment implements TabHost.OnTabChangeListener, TabHost.TabContentFactory, android.support.v4.view.ViewPager.OnPageChangeListener {

    private TopicPagerAdapter pagerAdapter;

    @InjectView(R.id.vp_pages)
    private ViewPager pager;

    @InjectView (R.id.th_tabs)
    private TabHost host;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(getContentView(), null);
	}

    protected int getContentView() {
        return R.layout.topic_with_tabs;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setOnPageChangeListener(this);
        host.setup();
        host.setOnTabChangedListener(this);
        configureTabPager();
    }

    @Override
    public void onTabChanged(String tabId) {
        updateCurrentItem(host.getCurrentTab());
    }

    private void updateCurrentItem(final int newPosition) {
        if (newPosition > -1 && newPosition < pagerAdapter.getCount()) {
            pager.setItem(newPosition);
        }
    }

    /**
     * Callback to make the tab contents
     *
     * @param tag Which tab was selected.
     * @return The view to display the contents of the selected tab.
     */
    @Override
    public View createTabContent(String tag) {
        return ViewUtils.setGone(new View(getActivity()), true);
    }

    protected String getTitle(final int position) {
        return pagerAdapter.getPageTitle(position).toString();
    }

    protected String getIcon(final int position) {
        return null;
    }

    protected int getTabIcon(final int position) {
        return 0;
    }

    protected void configureTabPager() {
        if (pagerAdapter == null) {
            createPager();
            createTabs();
        }
    }

    protected void createPager() {
        pagerAdapter = new TopicPagerAdapter(getSherlockActivity());
        pager.setAdapter(pagerAdapter);
    }

    protected void createTabs() {
        if (host.getTabWidget().getTabCount() > 0) {
            host.setCurrentTab(0);
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(R.layout.topic_tab, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
            TextView textIcon = (TextView) view.findViewById(R.id.tv_icon);
            String iconText = getIcon(i);
            if (!TextUtils.isEmpty(iconText)) {
                textIcon.setText(iconText);
            } else {
                ViewUtils.setGone(textIcon, true);
            }
            int tabIcon = getTabIcon(i);
            if (tabIcon != 0)
                imageView.setBackgroundResource(tabIcon);
            ((TextView) view.findViewById(R.id.tv_tab)).setText(getTitle(i));
            spec.setIndicator(view);
            host.addTab(spec);
            int background;
            if (i == 0) {
                background = getLeftBackground();
            } else if (i == count - 1) {
                background = getCenterBackground();
            } else {
                background = getRightBackground();
            }
            ((ImageView) view.findViewById(R.id.iv_tab)).setImageResource(background);
        }
    }

    protected int getLeftBackground() {
        return R.drawable.topic_find_selector;
    }

    protected int getCenterBackground() {
        return R.drawable.topic_dynamic_selector;
    }

    protected int getRightBackground() {
        return R.drawable.topic_dynamic_selector;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        host.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
