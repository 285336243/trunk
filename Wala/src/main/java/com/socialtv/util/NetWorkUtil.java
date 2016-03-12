package com.socialtv.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {

	/**
	 * 获取网络类型 
	 * @param context
	 * @return 网络类型  -1: 没有网络, 0:net网络, 1: wifi网络
	 */
	public static int getAPNType(Context context) {
		int netType = -1;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo == null) {
			return netType;
		}
		int type = networkInfo.getType();
		if(type == ConnectivityManager.TYPE_MOBILE) {
			netType = ConnectivityManager.TYPE_MOBILE ;
		}
		if(type == ConnectivityManager.TYPE_WIFI) {
			netType = ConnectivityManager.TYPE_WIFI ;
		}
		return netType;
	}
	
	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {  
         ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
         NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();  
        if (mNetworkInfo != null) {  
             return mNetworkInfo.isAvailable();  
        }  
		return false;  
	}  
	
	/**
	 * 判断mobile网络是否可用
	 * @param context
	 * @return
	 */
	public static boolean isMobileConnected(Context context) {  
         ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
         NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
         if (mMobileNetworkInfo != null) {  
             return mMobileNetworkInfo.isAvailable();  
         }  
	     return false;  
	 }
	
	/**
	 * 判断wifi网络是否可用
	 * @param context
	 * @return
	 */
	 public static boolean isWifiConnected(Context context) {  
         ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
         NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
         if (mWiFiNetworkInfo != null) {  
             return mWiFiNetworkInfo.isAvailable();  
         }  
	     return false;  
	 } 
}
