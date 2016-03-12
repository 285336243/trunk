package com.mzs.guaji.adapter;

import java.util.List;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Pic;
import com.mzs.guaji.util.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PhotoGralleryAdapter extends BaseAdapter{

	private Context context;
	private List<Pic> pics;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	public PhotoGralleryAdapter(Context context, List<Pic> pics) {
		this.context = context;
		this.pics = pics;
		mImageLoader = ImageLoader.getInstance();
		options = ImageUtils.imageLoader(context, 0);
	}
	
	public void addPic(List<Pic> pics) {
		for(Pic pic : pics) {
			this.pics.add(pic);
		}
		notifyDataSetChanged();
	}
	
	public List<Pic> getListPic() {
		return pics;
	}
	
	public void clear() {
		this.pics.clear();
	}
	
	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public Object getItem(int position) {
		return pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			imageView = (ImageView) mInflater.inflate(R.layout.photo_list_item, parent, false);
		} else {
			imageView = (ImageView) convertView;
		}

		mImageLoader.displayImage(pics.get(position).getImg(), imageView, options);
		return imageView;
	}
}
