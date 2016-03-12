package com.mzs.guaji.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class NewsDetailsPagerAdapter extends PagerAdapter {

	private List<View> mViews;
	public NewsDetailsPagerAdapter(List<View> mViews) {
		this.mViews = mViews;
	}
	
	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View v, Object o) {
		return v == o;
	}

	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View)object);
	}
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
		View v = mViews.get(position);
		((ViewPager) container).addView(v);
		return v;
	}
}
