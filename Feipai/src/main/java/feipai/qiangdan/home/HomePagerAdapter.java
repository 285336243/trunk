package feipai.qiangdan.home;

import android.support.v4.app.Fragment;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import feipai.qiangdan.core.FragmentStatePagerAdapter;
import feipai.qiangdan.map.MapFragment;
import feipai.qiangdan.my.MyCoreFragment;
import feipai.qiangdan.order.QiangOrderFragment;

import feipai.qiangdan.other.OtherFragement;


/**
 *首页adapter
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
            return new QiangOrderFragment();
        } else if (position == 1) {
            return new MapFragment();
        } else if (position == 2) {
            return new MyCoreFragment();
        }else if (position == 3) {
            return new OtherFragement();
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
/*        if (position == 0) {
            return "抢单";
        } else if (position == 1) {
            return "地图";
        } else if (position == 2) {
            return "我的";
        }else if (position == 3) {
            return "提醒";
        }*/
        return "";
    }
}
