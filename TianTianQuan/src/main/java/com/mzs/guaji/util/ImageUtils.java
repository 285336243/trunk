package com.mzs.guaji.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.mzs.guaji.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ImageUtils {
	public final static String SDCARD_MNT = "/mnt/sdcard";

	public static int getDefaultMemoryCacheSize(Context context) {
		int heightPixels = context.getResources().getDisplayMetrics().heightPixels;
		int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
		return heightPixels * widthPixels * 8;
	}

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
		initImageLoader(context);
		return mImageOptions;
	}

	public static void initImageLoader(Context context) {
		if (!ImageLoader.getInstance().isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					context)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(new WeakMemoryCache())
					.discCacheSize(1000000000)
					.discCacheFileNameGenerator(new Md5FileNameGenerator())
					.tasksProcessingOrder(QueueProcessingType.LIFO)
//					.writeDebugLogs() // Remove for release app
					.build();
			ImageLoader.getInstance().init(config);
		}
	}

//	/**
//	 * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
//	 * 
//	 * @param uri
//	 * @return
//	 */
//	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
//		String filePath = null;
//
//		String mUriString = mUri.toString();
//		mUriString = Uri.decode(mUriString);
//
//		String pre1 = "file://"
//				+ Environment.getExternalStorageDirectory().getPath()
//				+ File.separator;
//		String pre2 = "file://" + SDCARD_MNT + File.separator;
//
//		if (mUriString.startsWith(pre1)) {
//			filePath = Environment.getExternalStorageDirectory().getPath()
//					+ File.separator + mUriString.substring(pre1.length());
//		} else if (mUriString.startsWith(pre2)) {
//			filePath = Environment.getExternalStorageDirectory().getPath()
//					+ File.separator + mUriString.substring(pre2.length());
//		}
//		return filePath;
//	}
//
//	/**
//	 * 通过uri获取文件的绝对路径
//	 * 
//	 * @param uri
//	 * @return
//	 */
//	public static String getAbsoluteImagePath(Activity context, Uri uri) {
//		String imagePath = "";
//		String[] proj = { MediaStore.Images.Media.DATA };
//		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
//														// return
//				null, // WHERE clause; which rows to return (all rows)
//				null, // WHERE clause selection arguments (none)
//				null); // Order-by clause (ascending by name)
//
//		if (cursor != null) {
//			int column_index = cursor
//					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
//				imagePath = cursor.getString(column_index);
//			}
//		}
//
//		return imagePath;
//	}
//
//	/**
//	 * 获取图片缩略图 只有Android2.1以上版本支持
//	 * 
//	 * @param imgName
//	 * @param kind
//	 *            MediaStore.Images.Thumbnails.MICRO_KIND
//	 * @return
//	 */
//	public static Bitmap loadImgThumbnail(Activity context, String imgName,
//			int kind) {
//		Bitmap bitmap = null;
//
//		String[] proj = { MediaStore.Images.Media._ID,
//				MediaStore.Images.Media.DISPLAY_NAME };
//
//		Cursor cursor = context.managedQuery(
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
//				MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName + "'",
//				null, null);
//
//		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//			ContentResolver crThumb = context.getContentResolver();
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 1;
//			bitmap = getThumbnail(crThumb, cursor.getInt(0),
//					kind, options);
//		}
//		return bitmap;
//	}
//	
//	 public static Bitmap getThumbnail(ContentResolver cr, long origId, int kind, Options options) {
//	       	return MediaStore.Images.Thumbnails.getThumbnail(cr,origId,kind, options);
//	    }
}