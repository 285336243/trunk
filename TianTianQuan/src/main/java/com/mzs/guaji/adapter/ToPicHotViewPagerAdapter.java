package com.mzs.guaji.adapter;

import java.util.List;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ToPicHotViewPagerAdapter extends PagerAdapter {

	private List<View> mViews;
    private Context context;
	public ToPicHotViewPagerAdapter(Context context ,List<View> mViews) {
		this.mViews = mViews;
        this.context = context;
	}
	
	@Override
	public int getCount() {
		return mViews.size();
//		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View v, Object o) {
		return v == o;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
//		((ViewPager) container).removeView((View) object);
	}

	@Override
	public Object instantiateItem(View container, int position) {
		View v = mViews.get(position % mViews.size());
		v.findViewById(R.id.topic_hot_viewpager_item_image_one).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                click((EntryForm)v.getTag());
			}
		});
		
		v.findViewById(R.id.topic_hot_viewpager_item_image_two).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                click((EntryForm)v.getTag());
			}
		});;
		((ViewPager) container).removeView(mViews.get(position%mViews.size()));
		((ViewPager) container).addView(mViews.get(position%mViews.size()));
//		((ViewPager) container).addView(v);
		return v;
	}

    private void click(EntryForm mEntryForm){
        if(mEntryForm != null && "GSTX_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent applyIntent = new Intent(context, GSTXApplyActivity.class);
            applyIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(applyIntent);
        }else if(mEntryForm != null && "AQBWZ_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, AQBWApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            mIntent.putExtra("clause", mEntryForm.getClause());
            context.startActivity(mIntent);
        }else if(mEntryForm != null && "FNMS_ENTRY".equals(mEntryForm.getTemplateName())) {
            Intent mIntent = new Intent(context, FNMSApplyActivity.class);
            mIntent.putExtra("id", mEntryForm.getId());
            context.startActivity(mIntent);
        }
    }
}
