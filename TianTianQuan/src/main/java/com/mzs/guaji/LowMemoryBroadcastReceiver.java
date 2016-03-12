package com.mzs.guaji;

import com.mzs.guaji.util.Log;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LowMemoryBroadcastReceiver extends BroadcastReceiver {

	private final static String TAG = LowMemoryBroadcastReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "LowMemoryBroadcastReceiver clear memory");
		String action = intent.getAction();
		ImageLoader imageLoader = ImageLoader.getInstance();
		if("com.mzs.guaji.lowmemory".equals(action)) {
			imageLoader.clearMemoryCache();
			System.gc();
		}
	}
}
