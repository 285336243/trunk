/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jiujie8.choice.core;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.jiujie8.choice.R;

/**
 * Activity with tabbed pages
 *
 * @param <V>
 */
public abstract class TabPagerActivity<V extends PagerAdapter>
        extends PagerActivity implements OnTabChangeListener, TabContentFactory {

    /**
     * View pager
     */
    protected ViewPager pager;

    /**
     * Tab host
     */
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
        return ViewUtils.setGone(new View(getApplication()), true);
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
     * Set tab and pager as gone or visible
     *
     * @param gone
     * @return this activity
     */
    protected TabPagerActivity<V> setGone(boolean gone) {
        ViewUtils.setGone(host, gone);
        ViewUtils.setGone(pager, gone);
        return this;
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

    protected void updateCurrentItem(final int newPosition) {
        if (newPosition > -1 && newPosition < adapter.getCount()) {
            pager.setItem(newPosition);
            setCurrentItem(newPosition);
        }
    }

    private void createPager() {
        adapter = createAdapter();
        invalidateOptionsMenu();
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
            // Crash on Gingerbread if tab isn't set to zero since adding a
            // new tab removes selection state on the old tab which will be
            // null unless the current tab index is the same as the first
            // tab index being added
            host.setCurrentTab(0);
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater();
        int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(getTabIndicatorView(), null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_icon);
            int tabIcon = getTabIcon(i);
            if (tabIcon != 0) {
                imageView.setBackgroundResource(tabIcon);
            }else {
                ViewUtils.setGone(imageView, true);
            }
            ((TextView) view.findViewById(R.id.tv_tab)).setText(getTitle(i));

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        pager = (ViewPager) findViewById(R.id.vp_pages);
        pager.setOnPageChangeListener(this);
        host = (TabHost) findViewById(R.id.th_tabs);
        host.setup();
        host.setOnTabChangedListener(this);
    }
}
