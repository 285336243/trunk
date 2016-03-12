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
import com.mzs.guaji.entity.User;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14-1-15.
 */
public class HomeStarHorizontalAdapter extends BaseAdapter {

    private Context context;
    private int width;
    private List<User> mGroups;

    public HomeStarHorizontalAdapter(Context context, List<User> mGroups, int width) {
        this.context = context;
        this.width = width;
        this.mGroups = mGroups;
    }

    @Override
    public int getCount() {
        return mGroups.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroups.get(position);
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
            mHolder.mHorizontalLayout.setLayoutParams(new RelativeLayout.LayoutParams((width - 36) / 3, (width - 36) / 3));
            mHolder.mImageView = (ImageView) v.findViewById(R.id.home_horizontal_star_image);
            mHolder.mNameText = (TextView) v.findViewById(R.id.home_horizontal_star_name);
            v.setTag(mHolder);
        }else {
            mHolder = (HomeHorizontalViewHolder) v.getTag();
        }
        User star = mGroups.get(position);
        if(star != null) {
            if(star.getCoverImg() != null && !"".equals(star.getCoverImg())) {
                ImageLoader.getInstance().displayImage(star.getCoverImg(), mHolder.mImageView, ImageUtils.imageLoader(context, 0));
            }
            mHolder.mNameText.setText(star.getNickname());
        }
        return v;
    }

    private class HomeHorizontalViewHolder {
        public ImageView mImageView;
        public TextView mNameText;
        public FrameLayout mHorizontalLayout;
    }
}
