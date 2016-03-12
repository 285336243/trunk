package com.socialtv.home;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.socialtv.core.FragmentStatePagerAdapter;
import com.socialtv.feed.FeedFragment;
import com.socialtv.message.HomeMessageFragment;
import com.socialtv.personcenter.PersionCenterFragment;

/**
 * Created by wlanjie on 14-6-20.
 * HomeActivty中的ViewPager的Adapter
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
            return new HomeFragment();
        } else if (position == 2) {
            return new HomeMessageFragment();
        } else if (position == 3) {
            return new PersionCenterFragment();
        }
        return null;
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "动态";
        } else if (position == 1) {
            return "发现";
        } else if (position == 2) {
            return "消息";
        } else if (position == 3) {
            return "个人中心";
        }
        return super.getPageTitle(position);
    }
}
