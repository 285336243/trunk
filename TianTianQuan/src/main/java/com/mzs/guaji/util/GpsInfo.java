package com.mzs.guaji.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.android.volley.SynchronizationHttpRequest;
import com.mzs.guaji.core.RequestUtils;
import com.mzs.guaji.entity.DefaultReponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 保证此类只有一个实例
 * @author wanglianjie
 *
 */
public class GpsInfo {

	private GpsInfo(){}
	private static GpsInfo gpsInfo = null;
	private static Context context;
	private AndYouLocationListener mListener;
	private LocationManager locationManager;
	private List<String> gpsInfoList = new ArrayList<String>();
	
	public static synchronized GpsInfo getInstance(Context _context) {
		context = _context;
		if(gpsInfo == null) {
			gpsInfo = new GpsInfo();
		}
		return gpsInfo;
	}
	
	/**
	 * 获取手机的gps信息
	 * @return 手机gps的经纬度,list第一个元素为纬度,第二个为经度
	 */
	public List<String> getLocation() {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			String provider = getProvider(locationManager);
			//监听位置变化,2秒一次,距离10米
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, getListener());
			SharedPreferences preferences = context.getSharedPreferences("gpsinfo", Context.MODE_PRIVATE);
			String latitude = preferences.getString("latitude", "");
			String longitude = preferences.getString("longitude", "");
			gpsInfoList.add(latitude);
			gpsInfoList.add(longitude);
			return gpsInfoList;
		}
		return null;
	}
	
	/**
	 * 停止gps监听
	 */
	public void stopGPSListener() {
		if(locationManager != null) {
			locationManager.removeUpdates(getListener());
		}
	}
	
	/**
	 * 获取最好的位置提供者
	 * @param manager
	 * @return
	 */
	private String getProvider(LocationManager manager) {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		criteria.setSpeedRequired(false);
		criteria.setCostAllowed(false);
		return manager.getBestProvider(criteria, true);
	}
	
	private synchronized AndYouLocationListener getListener() {
		if(mListener == null) {
			mListener = new AndYouLocationListener();
		}
		return mListener;
	}
	
	private class AndYouLocationListener implements LocationListener {

		/**
		 * 当手机位置改变时调用
		 */
		@Override
		public void onLocationChanged(Location location) {
            //手机的纬度
            final String latitude = location.getLatitude() + "";
            //手机的经度
            final String longitude = location.getLongitude() + "";
            SharedPreferences preferences = context.getSharedPreferences("gpsinfo", Context.MODE_PRIVATE);
            Editor editor = preferences.edit();
            editor.putString("latitude", latitude);
            editor.putString("longitude", longitude);
            editor.commit();
            if (location.getAccuracy() < 50) {
                stopGPSListener();
                new AsyncTask<Void, Void, DefaultReponse>() {
                    @Override
                    protected DefaultReponse doInBackground(Void... params) {
                        SynchronizationHttpRequest<DefaultReponse> request = RequestUtils.getInstance().createGet(context, getGpsRequest(latitude, longitude), null);
                        request.setClazz(DefaultReponse.class);
                        return request.getResponse();
                    }
                }.execute();
            }
        }

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
	}

    private String getGpsRequest(String latitude, String longitude) {
        return "gps/sync.json?lat="+ latitude+"&lon="+ longitude +"";
    }
 }
