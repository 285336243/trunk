package com.mzs.guaji.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.entity.User;
import com.mzs.guaji.util.BroadcastActionUtil;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PersonalFansAdapter extends BaseAdapter{

	private Context context;
	private List<User> mUsers;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	private GuaJiAPI mApi;
	private PopupWindow mPopupWindow;
	private LinearLayout mRootLayout;
	public PersonalFansAdapter(Context context, List<User> mUsers, LinearLayout mRootLayout) {
		this.context = context;
		this.mUsers = mUsers;
		this.mRootLayout = mRootLayout;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 4);
		mApi = GuaJiAPI.newInstance(context);
	}
	
	public void clear() {
		this.mUsers.clear();
	}
	
	public void addUser(List<User> mUsers) {
		for(User mUser : mUsers) {
			this.mUsers.add(mUser);
		}
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mUsers.size();
	}

	@Override
	public Object getItem(int position) {
		return mUsers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		FollowFansViewHolder mHolder = null;
		if(v == null) {
			mHolder = new FollowFansViewHolder();
			v = View.inflate(context, R.layout.follow_fans_item, null);
			mHolder.mAvatarImage = (ImageView) v.findViewById(R.id.follow_fans_avatar);
			mHolder.mNickNameText = (TextView) v.findViewById(R.id.follow_fans_nickname);
			mHolder.mFollowButton = (ImageView) v.findViewById(R.id.follow_button);
			mHolder.mFollowShadowImage = (ImageView) v.findViewById(R.id.follow_fans_shadow_image);
			v.setTag(mHolder);
		}else {
			mHolder = (FollowFansViewHolder) v.getTag();
		}
		final User mUser = mUsers.get(position);
        if(LoginUtil.getUserId(context) == mUser.getUserId()) {
            mHolder.mFollowButton.setVisibility(View.GONE);
        }
		mImageLoader.displayImage(mUser.getAvatar(), mHolder.mAvatarImage, options);
		mHolder.mNickNameText.setText(mUser.getNickname());
		if(mUser.getIsFollowed() == 0) {
			mHolder.mFollowButton.setImageResource(R.drawable.btn_addattention_tj);
		}else {
			mHolder.mFollowButton.setImageResource(R.drawable.btn_calattention_tj);
		}
		if((position + 1) == getCount()) {
			mHolder.mFollowShadowImage.setVisibility(View.VISIBLE);
		}
		mHolder.mFollowButton.setOnClickListener(new FollowListener(mUser, mHolder));
		return v;
	}
	
	/**
	 * 添加关注
	 * @return
	 */
	private String getFollowRequestUrl() {
		return "http://social.api.ttq2014.com/" + "user/follow.json";
	}
	
	/**
	 * 取消关注
	 * @return
	 */
	private String getUnFollowRequestUrl() {
		return "http://social.api.ttq2014.com/" + "user/unfollow.json";
	}
	
	/**
	 * 发送广播,通知关注列表刷新
	 */
	private void sendFollowBroadcast() {
		Intent mIntent = new Intent(BroadcastActionUtil.REFRESHFOLLOWACTION);
		context.sendBroadcast(mIntent);
	}

	private void showPopupWindow(int textId) {
		View v = View.inflate(context, R.layout.image_pop, null);
		mPopupWindow = new PopupWindow(v, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		TextView mDialogMessageText = (TextView) v.findViewById(R.id.dialog_message_text);
		mDialogMessageText.setText(textId);
		if(!mPopupWindow.isShowing()) {
			mPopupWindow.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
		}
	}
	
	private void dismissPopupWindow() {
		if(mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	
	public static class FollowFansViewHolder {
		public ImageView mAvatarImage;
		public TextView mNickNameText;
		public ImageView mFollowButton;
		public ImageView mFollowShadowImage;
	}

    /**
     * 添加关注
     */
    private void execAddFollow(final User mUser, final FollowFansViewHolder mHolder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("userId", mUser.getUserId()+"");
        showPopupWindow(R.string.add_follow);
        mApi.requestPostData(getFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                mHolder.mFollowButton.setEnabled(true);
                if(response != null && response.getResponseCode() == 0) {
                    mUser.setIsFollowed(1);
                    dismissPopupWindow();
                    sendFollowBroadcast();
                    mHolder.mFollowButton.setImageResource(R.drawable.btn_calattention_tj);
                }else {
                    dismissPopupWindow();
                    ToastUtil.showToast(context, response.getResponseMessage());
                }
            }
        }, null);
    }

    /**
     * 取消关注
     */
    private void execUnFollow(final User mUser, final FollowFansViewHolder mHolder) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("userId", mUser.getUserId()+"");
        showPopupWindow(R.string.delete_follow);
        mApi.requestPostData(getUnFollowRequestUrl(), DefaultReponse.class, headers, new Response.Listener<DefaultReponse>() {
            @Override
            public void onResponse(DefaultReponse response) {
                mHolder.mFollowButton.setEnabled(true);
                if(response != null && response.getResponseCode() == 0) {
                    mUser.setIsFollowed(0);
                    dismissPopupWindow();
                    sendFollowBroadcast();
                    mHolder.mFollowButton.setImageResource(R.drawable.btn_addattention_tj);
                }else {
                    dismissPopupWindow();
                    ToastUtil.showToast(context, response.getResponseMessage());
                }
            }
        }, null);
    }

	private class FollowListener implements View.OnClickListener {

		private User mUser;
		private FollowFansViewHolder mHolder;
		public FollowListener(User mUser, FollowFansViewHolder mHolder) {
			this.mUser = mUser;
			this.mHolder = mHolder;
		}
		
		@Override
		public void onClick(View v) {
			mHolder.mFollowButton.setEnabled(false);
			if(mUser.getIsFollowed() == 0) {
                execAddFollow(mUser, mHolder);
			}else {
                execUnFollow(mUser, mHolder);
			}
		}
		
	}

}
