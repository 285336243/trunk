package com.shengzhish.xyj.activity.specialnews;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.inject.Inject;

/**
 * fragment Adapter
 */
public class SpecialPagerAdapter extends FragmentStatePagerAdapter {

    @Inject
    public SpecialPagerAdapter(SherlockFragmentActivity activity) {
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
            return  LatestFragment.newInstance(true);
        } else {
            return LatestFragment.newInstance(false);
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "热门推荐";
        } else {
            return "最新动态";
        }
    }
}
