package com.socialtv.feed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.inject.Inject;

/**
 * Created by wlanjie on 14-10-9.
 * 动态ViewPager的Adapter
 */
public class FeedPagerAdapter extends FragmentStatePagerAdapter {

    @Inject
    public FeedPagerAdapter(final SherlockFragment fragment) {
        super(fragment.getChildFragmentManager());
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
//            return new FeedLastestFragment();
            return new FeedLastFragmentAdvert();
        } else if (position == 1) {
            return new FeedHotFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
