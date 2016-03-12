package com.mzs.guaji.adapter;

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

import java.util.List;

public class TvCircleListGridAdapter extends BaseAdapter {
	private Context context;
	private List<Group> groups;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;

	public TvCircleListGridAdapter(Context context, List<Group> groups) {
		this.context = context;
		this.groups = groups;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 0);
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
		View view = convertView;
		ViewHolder mHolder = null;
		if(view == null) {
			mHolder = new ViewHolder();
			view = View.inflate(context, R.layout.tvcircle_list_grid_item, null);
			mHolder.coverImage = (ImageView) view.findViewById(R.id.tvcircle_grid_item_image);
			mHolder.officialImage = (ImageView) view.findViewById(R.id.tvcircle_grid_item_official);
			mHolder.nameText = (TextView) view.findViewById(R.id.tvcircle_grid_item_name);
			mHolder.membersText = (TextView) view.findViewById(R.id.tvcircle_grid_item_members);
			mHolder.topicsText = (TextView) view.findViewById(R.id.tvcircle_grid_item_topics);
			view.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) view.getTag();
		}
		Group mGroup = groups.get(position);
		if(!"OFFICIAL".equals(mGroup.getType())) {
			mHolder.officialImage.setVisibility(View.GONE);
		}
		mImageLoader.displayImage(mGroup.getCoverImg(), mHolder.coverImage, options);
		mHolder.nameText.setText(mGroup.getName());
		mHolder.membersText.setText(mGroup.getMembersCnt()+"");
		mHolder.topicsText.setText(mGroup.getTopicsCnt()+"");
		return view;
	}

	private class ViewHolder {
		public ImageView coverImage;
		public ImageView officialImage;
		public TextView nameText;
		public TextView membersText;
		public TextView topicsText;
	}
}
