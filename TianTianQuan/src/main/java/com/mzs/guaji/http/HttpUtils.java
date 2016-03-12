 package com.mzs.guaji.http;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class HttpUtils {
	
	private static DefaultHttpClient httpClient;

	private static IdleConnectionMonitorThread idleConnMonitor;

	@SuppressWarnings("unused")
	private static ExecutorService executorService;
	
	private static SQLiteCookieStore cookieStore;

	public static void init(Context context) {
		executorService = Executors.newFixedThreadPool(5);
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 20000);
		HttpConnectionParams.setSoTimeout(params, 20000);
		HttpConnectionParams.setTcpNoDelay(params, true);
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
		try {
			String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
			String mobileModel = Build.MODEL;
			String mobileBrand = Build.MANUFACTURER;
			String sdkVersion = Build.VERSION.RELEASE;
			String useragent = "GuajiVersion/" + versionName + "/mobilebrand/" + mobileBrand + "/mobileModel/" + mobileModel + "/android/" + sdkVersion;
			HttpProtocolParams.setUserAgent(params, useragent);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		idleConnMonitor = new IdleConnectionMonitorThread(connManager);
		idleConnMonitor.start();
		httpClient = new DefaultHttpClient(connManager, params);
		cookieStore = new SQLiteCookieStore(context);
		httpClient.setCookieStore(cookieStore);
	}

	public static void shutdown() {
		if (idleConnMonitor != null) {
			idleConnMonitor.shutdown();
		}
		idleConnMonitor = null;
		httpClient = null;
	}

	public static HttpClient getHttpClient(Context context) {
		init(context);
		return httpClient;
	}
	
	public static void clearCookie() {
		cookieStore.clear();
	}
	
	public static SQLiteCookieStore getCookieStore() {
		return cookieStore;
	}
}
