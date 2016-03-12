package com.shengzhish.xyj.home;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.shengzhish.xyj.activity.ActivityFragment;
import com.shengzhish.xyj.core.FragmentStatePagerAdapter;
import com.shengzhish.xyj.persionalcore.PersionalCoreFragment;

/**
 * 首页adapter
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {

    /**
     * @param activity
     */
    public HomePagerAdapter(SherlockFragmentActivity activity) {
        super(activity);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FeedFragment();
        } else if (position == 1) {
            return new ActivityFragment();
        } else if (position == 2) {
            return new PersionalCoreFragment();
        }
        return null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "动态";
        } else if (position == 1) {
            return "活动";
        } else if (position == 2) {
            return "个人中心";
        }
        return "";
    }
}
