package com.socialtv.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.socialtv.R;

public class ImageUtils {

	public static DisplayImageOptions imageLoader(Context context, int rounded) {
		DisplayImageOptions mImageOptions;
		if (rounded == 0) {
			mImageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.default_image)
					.showImageForEmptyUri(R.drawable.default_image)
					.showImageOnFail(R.drawable.default_image)
					.cacheInMemory(true).cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .considerExifParams(true)
					.displayer(new FadeInBitmapDisplayer(300))
					.build();
		} else {
			mImageOptions = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.default_image)
					.showImageForEmptyUri(R.drawable.default_image)
					.showImageOnFail(R.drawable.default_image)
					.cacheInMemory(true).cacheOnDisc(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new FadeInBitmapDisplayer(300))
					.displayer(new RoundedBitmapDisplayer(rounded)).build();
		}
		return mImageOptions;
	}

    public static DisplayImageOptions avatarImageLoader() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.headpic)
                .showImageForEmptyUri(R.drawable.headpic)
                .showImageOnFail(R.drawable.headpic)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .displayer(new RoundedBitmapDisplayer(1000)).build();
    }
}