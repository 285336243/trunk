package com.mzs.guaji.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.entity.SimpleShop;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

public class ShopsAdapter extends BaseAdapter {

	private Context context;
	private List<SimpleShop> shops;
	private ImageLoader mImageLoader;
	public ShopsAdapter(Context context, List<SimpleShop> shops) {
		this.context = context;
		this.shops = shops;
		mImageLoader = ImageLoader.getInstance();
	}

	public void clear() {
		this.shops.clear();
	}

	public void addSimpleShop(List<SimpleShop> shops) {
		for (SimpleShop mShop : shops) {
			this.shops.add(mShop);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return shops.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return shops.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ShopViewHolder mHolder = null;
		if (v == null) {
			mHolder = new ShopViewHolder();
			v = View.inflate(context, R.layout.shop_list_item, null);
			mHolder.mImageView = (ImageView) v.findViewById(R.id.shop_item_image);
			mHolder.mPriceText = (TextView) v.findViewById(R.id.shop_price_text);
			v.setTag(mHolder);
		} else {
			mHolder = (ShopViewHolder) v.getTag();
		}
		SimpleShop mShop = shops.get(position);
		mImageLoader.displayImage(mShop.getCoverImg(), mHolder.mImageView,
				new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.NONE).showImageOnLoading(R.drawable.default_image)
				.showImageForEmptyUri(R.drawable.default_image)
				.showImageOnFail(R.drawable.default_image)
				.cacheInMemory(true).cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565).postProcessor(new BitmapProcessor() {

					public int px2dip(float scale, float pxValue) {  
				       // return (int) (pxValue / scale + 0.5f);
						return (int)pxValue;
				    }  
					
					@Override
					public Bitmap process(Bitmap bitmap) {
						Bitmap ninePatchBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.feb_shoplist_tj);
						byte[] chunk = ninePatchBitmap.getNinePatchChunk();
						NinePatchDrawable np_drawable = new NinePatchDrawable(context.getResources(), ninePatchBitmap, chunk, new Rect(), null);
						
						int paddingLeft = Float.valueOf(16f*(float)ninePatchBitmap.getWidth()/82.0f).intValue();
						int paddingTop  = Float.valueOf(8f*(float)ninePatchBitmap.getWidth()/82.0f).intValue();
						DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
						
						int width =displayMetrics.widthPixels-px2dip(displayMetrics.density,paddingLeft*2);
						int height = (int)(( (float)width / (float)bitmap.getWidth() )* (float)bitmap.getHeight());
						
						np_drawable.setBounds(0, 0, displayMetrics.widthPixels, height+px2dip(displayMetrics.density,paddingTop*2));
						np_drawable.setTargetDensity(displayMetrics);
						Bitmap output_bitmap = Bitmap.createBitmap(displayMetrics.widthPixels,height+px2dip(displayMetrics.density,paddingTop*2), Bitmap.Config.ARGB_8888);
						Canvas canvas = new Canvas(output_bitmap);
						Matrix matrix = new Matrix();
						matrix.postScale((float)width / (float)bitmap.getWidth(), (float)width / (float)bitmap.getWidth());
						matrix.postTranslate(px2dip(displayMetrics.density,paddingLeft), px2dip(displayMetrics.density,paddingTop));
						canvas.drawBitmap(bitmap, matrix, null);
						
						np_drawable.draw(canvas);
						
						chunk = null;
						np_drawable = null;
						ninePatchBitmap.recycle();
						return output_bitmap;
					}
				}).build());
		mHolder.mPriceText.setText(mShop.getPrice());
		return v;
	}

	private static class ShopViewHolder {
		public ImageView mImageView;
		public TextView mPriceText;
	}
}
