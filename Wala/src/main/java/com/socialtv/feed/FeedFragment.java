package com.socialtv.feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.google.inject.Inject;
import com.socialtv.R;
import com.socialtv.core.DialogFragment;
import com.socialtv.core.ViewPager;
import com.socialtv.home.HomeActivity;
import com.socialtv.util.LoginUtil;

import java.util.List;

/**
 * Created by wlanjie on 14-7-3.
 *动态中的最新和热门的父类
 */
public class FeedFragment extends DialogFragment implements HomeActivity.OnPageSelected {

    private View refreshView;

    private RadioGroup group;

    private ViewPager pager;

    @Inject
    private FeedPagerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_pager, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (LoginUtil.isLogin(getActivity())) {
            onPageSelected(0);
        }
        HomeActivity activity = (HomeActivity) getActivity();
        activity.setOnPageSelected(this);
        activity.setSupportProgressBarIndeterminateVisibility(false);
        pager = (ViewPager) view.findViewById(R.id.view_pager);
        pager.setAdapter(new FeedPagerAdapter(this));
        pager.setCurrentItem(0);
        pager.setOnPageChangeListener(new android.support.v4.view.ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (group != null) {
                    if (position == 0) {
                        group.check(R.id.feed_newest);
                    } else if (position == 1) {
                        group.check(R.id.feed_hot);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置tab上的动态的actionbar
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        getSherlockActivity().getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.feed_title);
        refreshView = getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.feed_refresh);
        group = (RadioGroup) getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.feed_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.feed_newest) {
                    pager.setCurrentItem(0);
                } else if (id == R.id.feed_hot) {
                    pager.setCurrentItem(1);
                }
            }
        });
        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Fragment> items = getChildFragmentManager().getFragments();
                if (group.getCheckedRadioButtonId() == R.id.feed_newest) {
                    ((FeedItemFragment) items.get(0)).refreshResult();
                } else {
                    ((FeedItemFragment) items.get(1)).refreshResult();
                }
            }
        });
        if (pager != null) {
            pager.setCurrentItem(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            List<Fragment> items = getChildFragmentManager().getFragments();
            for (Fragment fragment : items) {
                FeedItemFragment feedItemFragment = (FeedItemFragment) fragment;
                feedItemFragment.refreshResult();
            }
        }
    }
}
