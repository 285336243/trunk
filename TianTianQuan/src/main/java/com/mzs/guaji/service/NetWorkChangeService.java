package com.mzs.guaji.service;

import com.mzs.guaji.util.NetWorkUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.widget.Toast;

/**
 * 监听网络状态service
 * @author lenovo
 *
 */
public class NetWorkChangeService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		IntentFilter netWorkChangeFilter = new IntentFilter();
		netWorkChangeFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, netWorkChangeFilter);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
		
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				if(NetWorkUtil.getAPNType(context) == -1) {
					Toast.makeText(context, "连接失败,请检查您的网络", Toast.LENGTH_SHORT).show();
				}else if(NetWorkUtil.getAPNType(context) == 0) {
					if(NetWorkUtil.isNetworkConnected(context)) {
						Toast.makeText(context, "您已切换到3G状态", Toast.LENGTH_SHORT).show();
					}
				}else {
					Toast.makeText(context, "您已切换到wifi状态", Toast.LENGTH_SHORT).show();
				}
			}
		}
		
	};

}
