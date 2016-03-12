package com.mzs.guaji.adapter;

import java.util.List;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.User;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchUserAdapter extends BaseAdapter {

	private Context context;
	private List<User> users;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	public SearchUserAdapter(Context context, List<User> users) {
		this.context = context;
		this.users = users;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 4);
	}
	
	public void addUserItem(List<User> users) {
		for(User user : users) {
			this.users.add(user);
		}
		notifyDataSetChanged();
	}
	

	public void clear() {
		if(this.users != null) {
			this.users.clear();
			notifyDataSetChanged();
		}
	}

	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return users.get(position);
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
			v = View.inflate(context, R.layout.search_user_list_item, null);
			mHolder.mAvatarImage = (ImageView) v.findViewById(R.id.search_user_list_item_avatar);
			mHolder.mNickNameText = (TextView) v.findViewById(R.id.search_user_list_item_nickname);
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		User user = users.get(position);
		mImageLoader.displayImage(user.getAvatar(), mHolder.mAvatarImage, options);
		mHolder.mNickNameText.setText(user.getNickname());
		return v;
	}
	 
	private static class ViewHolder {
		public ImageView mAvatarImage;
		public TextView mNickNameText;
	}
}
