package com.mzs.guaji.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.Pic;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.List;

public class PersonalGralleryGridAdapter extends BaseAdapter {

	private Context context;
	private List<Pic> pics;
	private ImageLoader mImageLoader;
	private DisplayImageOptions options;
	public PersonalGralleryGridAdapter(Context context, List<Pic> pics) {
		this.context = context;
		this.pics = pics;
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                        // .displayer(new FadeInBitmapDisplayer(300))
                .imageScaleType(ImageScaleType.NONE).build();
	}
	
	public void clear() {
		this.pics.clear();
	}
	
	public void addPic(List<Pic> mPics) {
        clear();
		for(Pic mPic : mPics) {
			this.pics.add(mPic);
		}
		this.notifyDataSetChanged();
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
		View v = convertView;
		ViewHolder mHolder;
		if(v == null) {
			mHolder = new ViewHolder();
			v = View.inflate(context, R.layout.personal_grallery_item, null);
			mHolder.mImageView = (ImageView) v.findViewById(R.id.personal_grallery_item_image);
			v.setTag(mHolder);
		}else {
			mHolder = (ViewHolder) v.getTag();
		}
		if(pics.get(position) != null) {
			if(!"".equals(pics.get(position).getImg()) || pics.get(position).getImg() != null) {
				mImageLoader.displayImage(pics.get(position).getImg(), mHolder.mImageView, options);
			}
		}
		return v;
	}

	private static class ViewHolder {
		public ImageView mImageView;
	}
}
