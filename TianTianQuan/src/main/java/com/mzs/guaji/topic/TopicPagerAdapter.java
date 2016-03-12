package com.mzs.guaji.topic;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.inject.Inject;

/**
 * Created by wlanjie on 14-5-20.
 */
public class TopicPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * @param activity
     */
    @Inject
    public TopicPagerAdapter(SherlockFragmentActivity activity) {
        super(activity.getSupportFragmentManager());
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FindFragment();
        } else {
            return new DynamicFragment();
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "发现";
        } else {
            return "动态";
        }
    }
}
