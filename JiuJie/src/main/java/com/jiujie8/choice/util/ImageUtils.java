package com.jiujie8.choice.util;

import android.graphics.Bitmap;

import com.jiujie8.choice.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 图片工具类
 */
public class ImageUtils {

    /**
     * 普通图片的配置
     * @param context
     * @param rounded
     * @return
     */
	public static DisplayImageOptions defaultImageLoader() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
	}

    public static DisplayImageOptions roundedImageLoaderI(int rounded) {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .displayer(new RoundedBitmapDisplayer(rounded)).build();
    }

    /**
     * 头像图片的配置
     * @return
     */
    public static DisplayImageOptions avatarImageLoader() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.round_default_image)
                .showImageForEmptyUri(R.drawable.round_default_image)
                .showImageOnFail(R.drawable.round_default_image)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .displayer(new RoundedBitmapDisplayer(1000)).build();
    }
}