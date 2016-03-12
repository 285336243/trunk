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
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by wlanjie on 14-1-15.
 */
public class HomeHorizontalAdapter extends BaseAdapter {

    private Context context;
    private int width;
    private List<Group> mGroups;

    public HomeHorizontalAdapter(Context context, List<Group> mGroups, int width) {
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
            v = View.inflate(context, R.layout.home_horizontal_item, null);
            mHolder.mHorizontalLayout = (FrameLayout) v.findViewById(R.id.home_horizontal_layout);
            mHolder.mHorizontalLayout.setLayoutParams(new RelativeLayout.LayoutParams((width - 36) / 2, width / 2 * 3 / 4));
            mHolder.mImageView = (ImageView) v.findViewById(R.id.home_horizontal_image);
            mHolder.mNameText = (TextView) v.findViewById(R.id.home_horizontal_name);
            mHolder.mMembersNumberText = (TextView) v.findViewById(R.id.home_horizontal_members);
            mHolder.mTopicsNumberText = (TextView) v.findViewById(R.id.home_horizontal_topics);
            v.setTag(mHolder);
        }else {
            mHolder = (HomeHorizontalViewHolder) v.getTag();
        }
        Group mGroup = mGroups.get(position);
        if(mGroup != null) {
            if(mGroup.getCoverImg() != null && !"".equals(mGroup.getCoverImg())) {
                ImageLoader.getInstance().displayImage(mGroup.getCoverImg(), mHolder.mImageView, ImageUtils.imageLoader(context, 0));
            }
            mHolder.mNameText.setText(mGroup.getName());
            mHolder.mMembersNumberText.setText(mGroup.getMembersCnt()+"");
            mHolder.mTopicsNumberText.setText(mGroup.getTopicsCnt()+"");
        }
        return v;
    }

    private class HomeHorizontalViewHolder {
        public ImageView mImageView;
        public TextView mNameText;
        public TextView mMembersNumberText;
        public TextView mTopicsNumberText;
        public FrameLayout mHorizontalLayout;
    }
}
