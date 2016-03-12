package com.socialtv.personcenter;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.socialtv.core.FragmentStatePagerAdapter;

/**
 * Created by wlanjie on 14-8-14.
 */
public class InviteFriendPagerAdapter extends FragmentStatePagerAdapter {

    private InviteMobileFragment mobileFragment;

    private InviteWeiXinFragment weiXinFragment;
    /**
     * @param activity
     */
    public InviteFriendPagerAdapter(SherlockFragmentActivity activity) {
        super(activity);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            if (mobileFragment == null) {
                mobileFragment = new InviteMobileFragment();
            }
            return mobileFragment;

        } else if (position == 1) {
            if (weiXinFragment == null) {
                weiXinFragment = new InviteWeiXinFragment();
            }
            return weiXinFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "手机";
        } else if (position == 1) {
            return "微信";
        }
        return super.getPageTitle(position);
    }
}
