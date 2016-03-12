package com.mzs.guaji.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PrivateCircleAdapter extends BaseAdapter {

	private Context context;
	private List<Topic> mToPics;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	public PrivateCircleAdapter(Context context, List<Topic> mToPics) {
		this.context = context;
		this.mToPics = mToPics;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 4);
	}
	
	public void addToPicItem(List<Topic> mToPics) {
		for(Topic mToPic : mToPics) {
			this.mToPics.add(mToPic);
		}
		notifyDataSetChanged();
	}
	
	public void clear() {
		mToPics.clear();
	}
	
	@Override
	public int getCount() {
		return mToPics.size();
	}

	@Override
	public Object getItem(int position) {
		return mToPics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		PrivateCircleHolder mHolder = null;
		if(v == null) {
			mHolder = new PrivateCircleHolder();
			v = View.inflate(context, R.layout.private_circle_list_item, null);
			mHolder.mUserAvatarImage = (ImageView) v.findViewById(R.id.search_topic_list_item_user_avatar);
			mHolder.mUserNameText = (TextView) v.findViewById(R.id.search_topic_list_item_usre_name);
			mHolder.mGroupNameText = (TextView) v.findViewById(R.id.search_topic_list_item_group_name);
			mHolder.mCreateTimeText = (TextView) v.findViewById(R.id.search_topic_list_item_create_time);
			mHolder.mTitleText = (TextView) v.findViewById(R.id.search_topic_list_item_title);
			mHolder.mDescText = (TextView) v.findViewById(R.id.search_topic_list_item_desc);
			mHolder.mContentImage = (ImageView) v.findViewById(R.id.search_topic_list_item_content_img);
			mHolder.mPostsCountText = (TextView) v.findViewById(R.id.search_topic_list_item_post_count);
			mHolder.mSupportsCountText = (TextView) v.findViewById(R.id.search_topic_list_item_supports_count);
			mHolder.mShareLayout = (RelativeLayout) v.findViewById(R.id.search_topic_list_item_share);
			mHolder.mPostsLayout = (RelativeLayout) v.findViewById(R.id.search_topic_list_item_posts);
			mHolder.mSupportsLayout = (RelativeLayout) v.findViewById(R.id.search_topic_list_item_supports);
			mHolder.mFrameLayout = (FrameLayout) v.findViewById(R.id.search_topic_list_item_content_img_layout);
			mHolder.mLikeImage = (ImageView) v.findViewById(R.id.search_topic_list_item_like_icon);
			v.setTag(mHolder);
		}else {
			mHolder = (PrivateCircleHolder) v.getTag();
		}
		final Topic toPic = mToPics.get(position);
		if(toPic.getUserAvatar() != null && !"".equals(toPic.getUserAvatar())) {
			mImageLoader.displayImage(toPic.getUserAvatar(), mHolder.mUserAvatarImage, options);
		}
		if(toPic.getIsLiked() == 0) {
			mHolder.mLikeImage.setImageResource(R.drawable.icon_liketopic_tj);
		}else {
			mHolder.mLikeImage.setImageResource(R.drawable.icon_liketopic_active_tj);
		}
		mHolder.mUserNameText.setText(toPic.getUserNickname());
		mHolder.mGroupNameText.setText(toPic.getGroupName());
		mHolder.mCreateTimeText.setText(toPic.getCreateTime());
		mHolder.mTitleText.setText(toPic.getTitle());
		mHolder.mDescText.setText(toPic.getDesc()+"");
		if(toPic.getImg() != null && !"".equals(toPic.getImg())) {
			mHolder.mFrameLayout.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(toPic.getImg()+".160x120", mHolder.mContentImage, ImageUtils.imageLoader(context, 0));
		}else {
			mHolder.mFrameLayout.setVisibility(View.GONE);
		}
		mHolder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(context, ImageDetailsActivity.class);
				mIntent.putExtra("imageUrl", toPic.getImg());
				context.startActivity(mIntent);
			}
		});
		mHolder.mPostsCountText.setText(String.format(context.getResources().getString(R.string.comment_count), toPic.getPostsCnt()));
		mHolder.mSupportsCountText.setText(String.format(context.getResources().getString(R.string.like_count), toPic.getSupportsCnt()));
		mHolder.mShareLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mHolder.mPostsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		
		mHolder.mSupportsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
			}
		});
		return v;
	}

	private static class PrivateCircleHolder {
		public ImageView mUserAvatarImage;
		public TextView mUserNameText;
		public TextView mGroupNameText;
		public TextView mCreateTimeText;
		public TextView mTitleText;
		public TextView mDescText;
		public ImageView mContentImage;
		public TextView mPostsCountText;
		public TextView mSupportsCountText;
		public RelativeLayout mShareLayout;
		public RelativeLayout mPostsLayout;
		public RelativeLayout mSupportsLayout;
		public FrameLayout mFrameLayout;
		public ImageView mLikeImage;
	}
}
