package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Group;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SearchCircleAdapter extends BaseAdapter {

	private List<Group> groups;
	private Context context;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	
	public SearchCircleAdapter(Context context, List<Group> groups) {
		this.context = context;
		this.groups = groups;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 0);
	}
	
	public void addGroupItem(List<Group> groups) {
		for(Group group : groups) {
			this.groups.add(group);
		}
		notifyDataSetChanged();
	}
	

	public void clear() {
		if(this.groups != null) {
			this.groups.clear();
		}
	}
	
	@Override
	public int getCount() {
		return groups.size();
	}

	@Override
	public Object getItem(int position) {
		return groups.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHoler mHoler = null;
		if(v == null) {
			mHoler = new ViewHoler();
			v = View.inflate(context, R.layout.search_circle_list_item, null);
			mHoler.mSearchCircleIcon = (ImageView) v.findViewById(R.id.search_circle_list_item_icon);
			mHoler.mSearchCircleImage = (ImageView) v.findViewById(R.id.search_circle_list_item_image);
			mHoler.mSearchCircleTitleText = (TextView) v.findViewById(R.id.search_circle_list_item_title);
			mHoler.mSearchCircleMembersText = (TextView) v.findViewById(R.id.search_circle_list_item_members);
			mHoler.mSearchCircleTopicsText = (TextView) v.findViewById(R.id.search_circle_list_item_topics);
			v.setTag(mHoler);
		}else {
			mHoler = (ViewHoler) v.getTag();
		}
		Group mGroup = groups.get(position);
		if(!"OFFICIAL".equals(mGroup.getType())) {
			mHoler.mSearchCircleIcon.setVisibility(View.GONE);
		}
		mImageLoader.displayImage(mGroup.getCoverImg(), mHoler.mSearchCircleImage, options);
		mHoler.mSearchCircleTitleText.setText(mGroup.getName());
		mHoler.mSearchCircleMembersText.setText(mGroup.getMembersCnt()+"");
		mHoler.mSearchCircleTopicsText.setText(mGroup.getTopicsCnt()+"");
		return v;
	}
	
	private static class ViewHoler {
		public ImageView mSearchCircleIcon;
		public ImageView mSearchCircleImage;
		public TextView mSearchCircleTitleText;
		public TextView mSearchCircleMembersText;
		public TextView mSearchCircleTopicsText;
	}

}
