package com.jiujie8.choice.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.jiujie8.choice.home.entity.ChoiceMode;
import com.jiujie8.choice.util.IConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wlanjie on 14/12/4.
 * 主页里的ViewPager中的Adapter
 */
public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private List<ChoiceMode> items = new ArrayList<ChoiceMode>();

    private boolean hasMore = false;

    private final HomeActivity mActivity;

    private final SparseArray<HomeFragment> mFragments = new SparseArray<HomeFragment>();

    public HomePagerAdapter(HomeActivity activity) {
        super(activity.getSupportFragmentManager());
        mActivity = activity;
    }

    public void setItems(final List<ChoiceMode> items, boolean hasMore) {
        this.items = items;
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public void clear() {
        this.items.clear();
    }

    public SparseArray<HomeFragment> getFragments() {
        return mFragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        mFragments.remove(position);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == getCount() - 1 && !hasMore) {
            return new LoadingFragment();
        } else  {
            HomeFragment mFragment = mFragments.get(position);
            if (mFragment == null) {
                mFragment = new HomeFragment();
                mFragments.put(position, mFragment);
                final ChoiceMode mMode = items.get(position);
                if (mMode != null) {
                    Bundle mArguments = new Bundle();
                    mArguments.putSerializable(IConstant.MODE_ITEM, mMode);
                    mFragment.setArguments(mArguments);
                }
            }
            return mFragment;
        }
    }

    @Override
    public int getCount() {
        if (items.isEmpty()) {
            return 0;
        }
        return hasMore ? items.size() : items.size() + 1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
