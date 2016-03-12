package com.shengzhish.xyj.activity.specialnews;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.DialogFragment;
import com.shengzhish.xyj.core.ViewPager;

import java.lang.reflect.Field;

import roboguice.inject.InjectView;

/**
 * Created by 51wanh on 14-7-24.
 */
public class SpecialFragment extends DialogFragment implements TabHost.OnTabChangeListener, TabHost.TabContentFactory, android.support.v4.view.ViewPager.OnPageChangeListener{

    private MyListener myListener;

    private int index;

    @InjectView(R.id.vp_pages)
    private ViewPager pager;

    @InjectView (R.id.th_tabs)
    private TabHost host;

    private SpecialPagerAdapter pagerAdapter;

    public static SpecialFragment newInstance(int index) {
        SpecialFragment fragment = new SpecialFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        host.setCurrentTab(position);
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onTabChanged(String tabId) {
        myListener.showPosition(host.getCurrentTab());
        //添加tab监听
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
    /** Acitivity要实现这个接口，这样Fragment和Activity就可以共享事件触发的资源了 */
    public interface MyListener
    {
        public void showPosition(int index);
    }

    /** Fragment第一次附属于Activity时调用,在onCreate之前调用 */
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        myListener = (MyListener) activity;
    }
    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(android.os.Bundle)} and {@link #onActivityCreated(android.os.Bundle)}.
     * <p/>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.special_news_tabs, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null){
            index = args.getInt("index");
        }

        pager.setOnPageChangeListener(this);
        host.setup();
        host.setOnTabChangedListener(this);
        configureTabPager();

    }

    private void configureTabPager() {
        if (pagerAdapter == null) {
            createPager();
            createTabs();
        }

    }

    private void createTabs() {
        if (host.getTabWidget().getTabCount() > 0) {
            host.clearAllTabs();
        }

        LayoutInflater inflater = getLayoutInflater(null);
        int count = pagerAdapter.getCount();
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec spec = host.newTabSpec("tab" + i);
            spec.setContent(this);
            View view = inflater.inflate(R.layout.special_tab, null);
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
                background = R.drawable.hot_recommend_selector;
            } else if (i == count - 1) {
                background =  R.drawable.hot_recommend_selector;
            } else {
                background = R.drawable.hot_recommend_selector;
            }
            ((ImageView) view.findViewById(R.id.iv_tab)).setImageResource(background);
        }
        host.setCurrentTab(index);
    }



    private void createPager() {
        pagerAdapter = new SpecialPagerAdapter(getSherlockActivity());
        pager.setAdapter(pagerAdapter);
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

}
