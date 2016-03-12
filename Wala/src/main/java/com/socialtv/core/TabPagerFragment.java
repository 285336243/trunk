package com.socialtv.core;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.socialtv.R;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-3-24.
 */
public abstract class TabPagerFragment<V extends PagerAdapter> extends PagerFragment implements TabHost.OnTabChangeListener, TabHost.TabContentFactory {

    /**
     * View pager
     */
    @InjectView(R.id.vp_pages)
    protected ViewPager pager;

    /**
     * Tab host
     */
    @InjectView(R.id.th_tabs)
    protected TabHost host;

    /**
     * Pager adapter
     */
    protected V adapter;

    /**
     * Tab currentItem position
     */
    protected int currentItem;

    @Override
    public void onPageSelected(final int position) {
        super.onPageSelected(position);
        host.setCurrentTab(position);
    }

    @Override
    public void onTabChanged(String tabId) {
        updateCurrentItem(host.getCurrentTab());
    }

    @Override
    public View createTabContent(String tag) {
        return ViewUtils.setGone(new View(getActivity()), true);
    }

    /**
     * Create pager adapter
     *
     * @return pager adapter
     */
    protected abstract V createAdapter();

    /**
     * Get title for position
     *
     * @param position
     * @return title
     */
    protected String getTitle(final int position) {
        return adapter.getPageTitle(position).toString();
    }

    /**
     * Get icon for position
     *
     * @param position
     * @return icon
     */
    protected String getIcon(final int position) {
        return null;
    }

    protected int getTabIcon(final int position) {
        return 0;
    }

    /**
     * Set current item to new position
     * <p>
     * This is guaranteed to only be called when a position changes and the
     * current item of the pager has already been updated to the given position
     * <p>
     * Sub-classes may override this method
     *
     * @param position
     */
    protected void setCurrentItem(final int position) {
        // Intentionally left blank
        currentItem = position;
    }

    protected int getCurrentItem() {
        return this.currentItem;
    }

    /**
     * Get content view to be used when {@link #onCreate(android.os.Bundle)} is called
     *
     * @return layout resource id
     */
    protected int getContentView() {
        return R.layout.pager_with_tabs;
    }

    private void updateCurrentItem(final int newPosition) {
        if (newPosition > -1 && newPosition < adapter.getCount()) {
            pager.setItem(newPosition);
            setCurrentItem(newPosition);
        }
    }

    private void createPager() {
        adapter = createAdapter();
        pager.setAdapter(adapter);
    }

    protected int getTabIndicatorView() {
        return R.layout.tab;
    }

    /**
     * Create tab using information from current adapter
     * <p>
     * This can be called when the tabs changed but must be called after an
     * initial call to {@link #configureTabPager()}
     */
    protected void createTabs() {
        if (host.getTabWidget().getTabCount() > 0) {
            host.setCurrentTab(0);
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(getTabIndicatorView(), null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
            int tabIcon = getTabIcon(i);
            if (tabIcon != 0) {
                imageView.setBackgroundResource(tabIcon);
            }else {
                ViewUtils.setGone(imageView, true);
            }
            TextView tabText = (TextView) view.findViewById(R.id.tv_tab);
            tabText.setText(getTitle(i));

            spec.setIndicator(view);
            host.addTab(spec);

            int background;
            if (i == 0)
                background = getLeftBackground();
            else if (i == count - 1)
                background = getCentreBackground();
            else
                background = getRightBackground();
            view.findViewById(R.id.rl_text)
                    .setBackgroundResource(background);
        }
    }

    protected abstract int getLeftBackground();

    protected abstract int getCentreBackground();

    protected abstract int getRightBackground();

    /**
     * Configure tabs and pager
     */
    protected void configureTabPager() {
        if (adapter == null) {
            createPager();
            createTabs();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getContentView(), null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pager.setOnPageChangeListener(this);
        host.setup();
        host.setOnTabChangedListener(this);
    }
}
