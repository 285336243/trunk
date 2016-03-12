package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.EntryForm;
import com.mzs.guaji.entity.Epg;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.ui.AQBWApplyActivity;
import com.mzs.guaji.ui.FNMSApplyActivity;
import com.mzs.guaji.ui.GSTXApplyActivity;
import com.mzs.guaji.offical.OfficialTvCircleActivity;
import com.mzs.guaji.ui.WebViewActivity;

public class TvCircleViewPagerAdapter extends PagerAdapter {

    private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
            .setPrettyPrinting().setVersion(1.0).create();
	private List<View> mImageViews;
	private Context context;
	private List<Object> mBannerObjects;
	public TvCircleViewPagerAdapter(Context context, List<View> mImageViews, List<Object> mBannerObjects) {
		this.context = context;
		this.mImageViews = mImageViews;
		this.mBannerObjects = mBannerObjects;
	}
	
	public void addView(List<View> views) {
		for(View view : views) {
			this.mImageViews.add(view);
		}
	}
	
	@Override
	public int getCount() {
		return mImageViews.size();
	}

	@Override
	public boolean isViewFromObject(View v, Object obj) {
		return v == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if(object instanceof RelativeLayout) {
			RelativeLayout mRelativeLayout = (RelativeLayout) object;
			((ViewPager)container).removeView(mRelativeLayout);
		}
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		RelativeLayout mRelativeLayout = (RelativeLayout) mImageViews.get(position);
		((ViewPager)container).addView(mRelativeLayout);
		mRelativeLayout.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Object o = mBannerObjects.get(position);
				if(o instanceof Epg) {
					Epg mEpg = (Epg) o;
					if(mEpg != null && mEpg.getGroup() != null) {
						if("OFFICIAL".equals(mEpg.getGroup().getType())) {
							Intent mIntent = new Intent(context, OfficialTvCircleActivity.class);
							mIntent.putExtra("img", mEpg.getGroup().getImg());
							mIntent.putExtra("id", mEpg.getGroup().getId());
							mIntent.putExtra("name", mEpg.getGroup().getName());
							context.startActivity(mIntent);
						}
					}
				}else if (o instanceof EntryForm) {
                    EntryForm mEntryForm = (EntryForm) o;
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
				}else if(o instanceof Activity) {
                    Activity mActivity = (Activity) o;
                    if(mActivity != null && "BROWSER".equals(mActivity.getType())) {
                        JsonElement mElement = mActivity.getParam();
                        if(mElement != null) {
                            SkipBrowser mBrowser = mGson.fromJson(mElement, SkipBrowser.class);
                            if(mBrowser != null) {
                                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mBrowser.getLink()));
                                context.startActivity(mIntent);
                            }
                        }
                    }else if(mActivity != null && "WEB_VIEW".equals(mActivity.getType())) {
                        JsonElement mElement = mActivity.getParam();
                        if(mElement != null) {
                            SkipWebView mWebView = mGson.fromJson(mElement, SkipWebView.class);
                            if(mWebView != null) {
                                Intent mIntent = new Intent(context, WebViewActivity.class);
                                mIntent.putExtra("url", mWebView.getLink());
                                context.startActivity(mIntent);
                            }
                        }
                    }
                }
			}
		});
		return mRelativeLayout;
	}
}
 