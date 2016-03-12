package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.GroupVideo;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VideoListingAdapter extends BaseAdapter {

	private Context context;
	private List<GroupVideo> mVideoLists;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	public VideoListingAdapter(Context context, List<GroupVideo> mVideoLists) {
		this.context = context;
		this.mVideoLists  = mVideoLists;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 0);
	}
	
	public void addGroupVideoItem(List<GroupVideo> mGroupVideos) {
		for(GroupVideo mGroupVideo : mGroupVideos) {
			this.mVideoLists.add(mGroupVideo);
		}
		notifyDataSetChanged();
	}
	
	public void clear() {
		mVideoLists.clear();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mVideoLists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mVideoLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder mHolder;
		if(v == null) {
			mHolder = new ViewHolder();
			v = View.inflate(context, R.layout.video_listing_item, null);
			mHolder.mImageView = (ImageView) v.findViewById(R.id.video_listing_item_image);
			mHolder.mTitleText = (TextView) v.findViewById(R.id.video_listing_item_title);
			mHolder.mCreateTimeText = (TextView) v.findViewById(R.id.video_listing_item_create_time);
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		GroupVideo mGroupVideo = mVideoLists.get(position);
		mHolder.mTitleText.setText(mGroupVideo.getTitle());
		mHolder.mCreateTimeText.setText(mGroupVideo.getCreateTime());
		mImageLoader.displayImage(mGroupVideo.getImg(), mHolder.mImageView, options);
		return v;
	}
	
	private static class ViewHolder {
		public ImageView mImageView;
		public TextView mTitleText;
		public TextView mCreateTimeText;
	}

}
