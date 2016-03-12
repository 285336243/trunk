package com.socialtv.message;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * Created by wlanjie on 14-7-2.
 * 主页上tab的消息的PagerAdapter
 */
public class HomeMessagePagerAdapter extends FragmentStatePagerAdapter {

    /**
     * @param activity
     */
    public HomeMessagePagerAdapter(SherlockFragment fragment) {
        super(fragment.getChildFragmentManager());
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MessageFragment();
        } else if (position == 1) {
            return new PrivateLetterFragment();
        }
        return null;
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
            return "消息";
        } else if (position == 1) {
            return "私信";
        }
        return super.getPageTitle(position);
    }
}
