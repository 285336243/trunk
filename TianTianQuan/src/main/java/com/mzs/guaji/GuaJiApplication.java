package com.mzs.guaji;

import android.app.Application;
import android.content.Intent;

import cn.jpush.android.api.JPushInterface;

/**
 * 天天圈全局应用类
 */
public class GuaJiApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init();
        JPushInterface.setDebugMode(false);
        JPushInterface.init(this);
        JPushInterface.setLatestNotifactionNumber(this, 10);
	}
	
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		Intent intent = new Intent();
		intent.setAction("com.mzs.guaji.lowmemory");
		sendBroadcast(intent);
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		//ImageLoader.getInstance().clearMemoryCache();
		System.exit(0);
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
