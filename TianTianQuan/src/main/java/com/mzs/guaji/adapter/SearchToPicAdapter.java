package com.mzs.guaji.adapter;

import android.app.Dialog;
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
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.topic.entity.Topic;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class SearchToPicAdapter extends BaseAdapter {

	private Context context;
	protected List<Topic> topics;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
    private GuaJiAPI mApi;
    private Dialog mDialog;
	public SearchToPicAdapter(Context context, List<Topic> topics) {
		this.context = context;
		this.topics = topics;
        mApi = GuaJiAPI.newInstance(context);
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 4);
		
	}
	
	public void addToPicItem(List<Topic> toPics) {
		for(Topic toPic : toPics) {
			this.topics.add(toPic);
		}
		notifyDataSetChanged();
	}
	
	public void clear() {
		if(this.topics != null) {
			this.topics.clear();
		}
	}
	
	@Override
	public int getCount() {
		return topics.size();
	}

	@Override
	public Object getItem(int position) {
		return topics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder mHolder = null;
		if(v == null) {
			mHolder = new ViewHolder();
			v = View.inflate(context, R.layout.find_topic, null);
			mHolder.mUserAvatarImage = (ImageView) v.findViewById(R.id.find_avatar);
			mHolder.mUserNameText = (TextView) v.findViewById(R.id.find_nickname);
			mHolder.mGroupNameText = (TextView) v.findViewById(R.id.find_action);
			mHolder.mCreateTimeText = (TextView) v.findViewById(R.id.find_createtime);
			mHolder.mTitleText = (TextView) v.findViewById(R.id.find_title);
			mHolder.mDescText = (TextView) v.findViewById(R.id.find_desc);
			mHolder.mContentImage = (ImageView) v.findViewById(R.id.find_image);
			mHolder.mPostsCountText = (TextView) v.findViewById(R.id.find_post_cnt);
			mHolder.mSupportsCountText = (TextView) v.findViewById(R.id.find_like_cnt);
			mHolder.mFrameLayout = (FrameLayout) v.findViewById(R.id.find_image_layout);
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		Topic toPic = topics.get(position);
        mHolder.mTopic = toPic;
		if(toPic.getUserAvatar() != null && !"".equals(toPic.getUserAvatar())) {
			mImageLoader.displayImage(toPic.getUserAvatar(), mHolder.mUserAvatarImage, options);
		}
        mHolder.mUserAvatarImage.setTag(toPic);
        mHolder.mUserAvatarImage.setOnClickListener(mAvatarClickListener);
		mHolder.mUserNameText.setText(toPic.getUserNickname());
		mHolder.mGroupNameText.setText("来自:"+toPic.getGroupName());
		mHolder.mCreateTimeText.setText(toPic.getCreateTime());
		mHolder.mTitleText.setText(toPic.getTitle());
		mHolder.mDescText.setText(toPic.getDesc()+"");
		if(toPic.getImg() != null && !"".equals(toPic.getImg())) {
			mHolder.mFrameLayout.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(toPic.getImg(), mHolder.mContentImage, ImageUtils.imageLoader(context, 0));
            mHolder.mContentImage.setTag(toPic);
            mHolder.mContentImage.setOnClickListener(mContentImageClickListener);
		}else {
			mHolder.mFrameLayout.setVisibility(View.GONE);
		}
		mHolder.mPostsCountText.setText(String.format(context.getResources().getString(R.string.comment_count), toPic.getPostsCnt()));
		mHolder.mSupportsCountText.setText(String.format(context.getResources().getString(R.string.like_count), toPic.getSupportsCnt()));
		return v;
	}

    View.OnClickListener mAvatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            SkipPersonalCenterUtil.startPersonalCore(context, mTopic.getUserId(), mTopic.getUserRenderTo());
        }
    };

    /**
     * 点击图片查看大图
     */
    View.OnClickListener mContentImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Topic mTopic = (Topic) v.getTag();
            Intent mIntent = new Intent(context, ImageDetailsActivity.class);
            mIntent.putExtra("imageUrl", mTopic.getImg());
            context.startActivity(mIntent);
        }
    };

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
		public FrameLayout mFrameLayout;
		public ImageView mLikeImage;
        public Topic mTopic;
	}
}
