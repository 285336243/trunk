package com.jiujie8.choice.myjiujie;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.jiujie8.choice.core.ThirdPartyShareActivity;
import com.jiujie8.choice.home.LoadingFragment;
import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.util.IConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14/12/4.
 */
public class MyJiujiePagerAdapter extends FragmentStatePagerAdapter {

    private List<ChoiceMode> items = new ArrayList<ChoiceMode>();

    private boolean hasMore = false;

    private final ThirdPartyShareActivity mActivity;


    private MyJiujieFragment mFragment;
    private ChoiceMode mode;

    public MyJiujiePagerAdapter(ThirdPartyShareActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
    }

    public void setMode(ChoiceMode mode) {
        this.mode = mode;
    }

    public void clear() {
        this.items.clear();
    }

    public MyJiujieFragment getFragments() {
        return mFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            MyJiujieFragment mFragment;
            mFragment = new MyJiujieFragment();
            Bundle mArguments = new Bundle();
            //新增加加
            mArguments.putSerializable(IConstant.MODE_ITEM, mode);
            mFragment.setArguments(mArguments);
           this.mFragment=mFragment;
            return mFragment;
        }
        return null;
    }


    @Override
    public int getCount() {
        return 1;
/*        if (items.isEmpty()) {
            return 0;
        }
        return hasMore ? items.size() : items.size() + 1;*/
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
