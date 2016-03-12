package com.mzs.guaji.adapter;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.mzs.guaji.core.FragmentStatePagerAdapter;

/**
 * Created by wlanjie on 14-4-9.
 */
public class TopicHomePagerAdapter extends FragmentStatePagerAdapter {

    /**
     * @param activity
     */
    public TopicHomePagerAdapter(SherlockFragmentActivity activity) {
        super(activity);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 0;
    }
}
