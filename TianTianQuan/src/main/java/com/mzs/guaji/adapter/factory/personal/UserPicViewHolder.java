package com.mzs.guaji.adapter.factory.personal;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.factory.ViewHolder;
import com.mzs.guaji.entity.feed.UserPic;
import com.mzs.guaji.entity.feed.UserPicFeed;
import com.mzs.guaji.ui.ImageDetailsActivity;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserPicViewHolder implements ViewHolder {
	private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
			.serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
			.setPrettyPrinting().setVersion(1.0).create();
	private ImageView mUserAvatarImage;
	private TextView mUserNameText;
	private TextView mGroupNameText;
	private TextView mCreateTimeText;
	private FrameLayout mFrameLayout;
	private ImageView mContentImage;
	private View view;
	
	@Override
	public void build(View v) {
		view = v;
		mUserAvatarImage = (ImageView) v.findViewById(R.id.personal_user_pic_list_item_user_avatar);
		mUserNameText = (TextView) v.findViewById(R.id.personal_user_pic_list_item_usre_name);
		mGroupNameText = (TextView) v.findViewById(R.id.personal_user_pic_list_item_group_name);
		mCreateTimeText = (TextView) v.findViewById(R.id.personal_user_pic_list_item_create_time);
		mFrameLayout = (FrameLayout) v.findViewById(R.id.personal_user_pic_list_item_content_img_layout);
		mContentImage = (ImageView) v.findViewById(R.id.personal_user_pic_list_item_content_img);
	}

	@Override
	public View renderView(final Context context, JsonElement param) {
		UserPicFeed mUserPicFeed = mGson.fromJson(param, UserPicFeed.class);
		ImageLoader mImageLoader = ImageLoader.getInstance();
		final UserPic mUserPic = mUserPicFeed.getTarget();
		if(mUserPic.getUserAvatar() != null && !"".equals(mUserPic.getUserAvatar())) {
			mImageLoader.displayImage(mUserPic.getUserAvatar(), mUserAvatarImage, ImageUtils.imageLoader(context, 4));
		}
		mUserNameText.setText(mUserPic.getUserNickname());
		mGroupNameText.setText(mUserPicFeed.getAction());
		mCreateTimeText.setText(mUserPic.getCreateTime());
		if(mUserPic.getImg() != null && !"".equals(mUserPic.getImg())) {
			mFrameLayout.setVisibility(View.VISIBLE);
			mImageLoader.displayImage(mUserPic.getImg(), mContentImage, ImageUtils.imageLoader(context, 0));
			mContentImage.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent mIntent = new Intent(context, ImageDetailsActivity.class);
					mIntent.putExtra("imageUrl", mUserPic.getImg());
					context.startActivity(mIntent);
				}
			});
		}else {
			mFrameLayout.setVisibility(View.GONE);
		}
		return view;
	}

    @Override
    public String getType() {
        return "USER_PIC";
    }
}
