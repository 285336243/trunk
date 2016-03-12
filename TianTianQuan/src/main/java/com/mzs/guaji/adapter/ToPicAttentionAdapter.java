package com.mzs.guaji.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;

public class ToPicAttentionAdapter extends BaseAdapter {

	private Context context;
	public ToPicAttentionAdapter(Context context) {
		this.context = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder mHolder = null;
		if(v == null) {
			mHolder = new ViewHolder();
			v = View.inflate(context, R.layout.search_topic_list_item, null);
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
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		return v;
	}

	private static class ViewHolder {
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
	}
}
