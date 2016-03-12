package com.shengzhish.xyj.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.shengzhish.xyj.R;

public class ImageUtils {

	public static DisplayImageOptions imageLoader(Context context, int rounded) {
		DisplayImageOptions mImageOptions;
		if (rounded == 0) {
			mImageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.default_image)
					.showImageForEmptyUri(R.drawable.default_image)
					.showImageOnFail(R.drawable.default_image)
					.cacheInMemory(true).cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
					// .displayer(new FadeInBitmapDisplayer(300))
					.build();
		} else {
			mImageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.default_image)
					.showImageForEmptyUri(R.drawable.default_image)
					.showImageOnFail(R.drawable.default_image)
					.cacheInMemory(true).cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// .displayer(new FadeInBitmapDisplayer(300))
					.displayer(new RoundedBitmapDisplayer(rounded)).build();
		}
//		initImageLoader(context);
		return mImageOptions;
	}

//	public static void initImageLoader(Context context) {
//		if (!ImageLoader.getInstance().isInited()) {
//			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
//					context)
//					.threadPriority(Thread.NORM_PRIORITY - 2)
//					.denyCacheImageMultipleSizesInMemory()
//					.memoryCache(new WeakMemoryCache())
//					.discCacheSize(1000000000)
//					.discCacheFileNameGenerator(new Md5FileNameGenerator())
//					.tasksProcessingOrder(QueueProcessingType.LIFO)
////					.writeDebugLogs() // Remove for release app
//					.build();
//			ImageLoader.getInstance().init(config);
//		}
//	}
}