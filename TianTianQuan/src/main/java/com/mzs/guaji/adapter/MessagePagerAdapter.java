package com.mzs.guaji.adapter;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.mzs.guaji.core.FragmentStatePagerAdapter;
import com.mzs.guaji.fragment.MessageFragment;
import com.mzs.guaji.fragment.PrivateLetterFragment;

/**
 * Created by wlanjie on 14-2-28.
 */
public class MessagePagerAdapter extends FragmentStatePagerAdapter {

    public MessageFragment messageFragment;

    public PrivateLetterFragment letterFragment;

    public MessagePagerAdapter(SherlockFragmentActivity activity) {
        super(activity);
        messageFragment = new MessageFragment();
        letterFragment = new PrivateLetterFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return messageFragment;
            case 1:
                return letterFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "消息";
            case 1:
                return "私信";
            default:
                return "";
        }
    }
}
