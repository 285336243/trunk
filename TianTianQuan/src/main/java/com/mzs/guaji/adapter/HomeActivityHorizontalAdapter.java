package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Activity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14-1-15.
 */
public class HomeActivityHorizontalAdapter extends BaseAdapter {

    private Context context;
    private int width;
    private List<Activity> activities;

    public HomeActivityHorizontalAdapter(Context context, List<Activity> activities, int width) {
        this.context = context;
        this.width = width;
        this.activities = activities;
    }

    @Override
    public int getCount() {
        return activities.size();
    }

    @Override
    public Object getItem(int position) {
        return activities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        HomeHorizontalViewHolder mHolder;
        if (v == null) {
            mHolder = new HomeHorizontalViewHolder();
            v = View.inflate(context, R.layout.home_horizontal_star_item, null);
            mHolder.mHorizontalLayout = (FrameLayout) v.findViewById(R.id.home_horizontal_star_layout);
            mHolder.mHorizontalLayout.setLayoutParams(new RelativeLayout.LayoutParams((width - 36) / 2, width / 2 * 3 / 4));
            mHolder.mImageView = (ImageView) v.findViewById(R.id.home_horizontal_star_image);
            mHolder.mNameText = (TextView) v.findViewById(R.id.home_horizontal_star_name);
            v.setTag(mHolder);
        }else {
            mHolder = (HomeHorizontalViewHolder) v.getTag();
        }
        Activity activity = activities.get(position);
        if(activity != null) {
            if(activity.getCoverImg() != null && !"".equals(activity.getCoverImg())) {
                ImageLoader.getInstance().displayImage(activity.getCoverImg(), mHolder.mImageView, ImageUtils.imageLoader(context, 0));
            }
            mHolder.mNameText.setText(activity.getName());
        }
        return v;
    }

    private class HomeHorizontalViewHolder {
        public ImageView mImageView;
        public TextView mNameText;
        public FrameLayout mHorizontalLayout;
    }
}
