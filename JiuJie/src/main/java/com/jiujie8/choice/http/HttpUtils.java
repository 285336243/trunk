 package com.jiujie8.choice.http;

 import android.content.Context;
 import android.content.pm.PackageManager;
 import android.content.pm.PackageManager.NameNotFoundException;
 import android.os.Build;

 import com.android.volley.Request;
 import com.android.volley.Response;
 import com.android.volley.SyncRequestQueue;
 import com.android.volley.toolbox.HttpClientStack;
 import com.android.volley.toolbox.Volley;
 import com.jiujie8.choice.util.StringUtils;

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

 import java.io.File;
 import java.util.HashMap;
 import java.util.Map;

 public class HttpUtils {

     private static DefaultHttpClient httpClient;

     private static IdleConnectionMonitorThread idleConnMonitor;

     private static SyncRequestQueue syncRequestQueue;

     public static void init(Context context) {
         HttpParams params = new BasicHttpParams();
         HttpConnectionParams.setConnectionTimeout(params, 20000);
         HttpConnectionParams.setSoTimeout(params, 20000);
         HttpConnectionParams.setTcpNoDelay(params, true);
         HttpConnectionParams.setStaleCheckingEnabled(params, true);
         SchemeRegistry schemeRegistry = new SchemeRegistry();
         schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
         schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
         ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, schemeRegistry);
         try {
             String versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES).versionName;
             String mobileModel = Build.MODEL;
             String mobileBrand = Build.MANUFACTURER;
             String sdkVersion = Build.VERSION.RELEASE;
             String useragent = "JiuJieVersion/" + versionName + "/mobilebrand/" + mobileBrand + "/mobileModel/" + mobileModel + "/android/" + sdkVersion;
             HttpProtocolParams.setUserAgent(params, useragent);
         } catch (NameNotFoundException e) {
             e.printStackTrace();
         }

         idleConnMonitor = new IdleConnectionMonitorThread(connManager);
         idleConnMonitor.start();
         httpClient = new DefaultHttpClient(connManager, params);

         syncRequestQueue = Volley.newSyncRequestQueue(context, null);
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

     public static Response<?> doRequest(Request request){
         return syncRequestQueue.add(request);
     }

     public static <T> GsonRequest<T> createGetRequest(final Class<T> clazz, final String URI, final Map<String, String> map) {
         String sign = "sign=" + StringUtils.securityMd5(StringUtils.mapSortToString(map));
         final String params = StringUtils.mapSortToParams(map);
         if (map == null) {
             sign = "?" + sign;
         }
         final GsonRequest<T> request = GsonRequest.getInstance(Request.Method.GET, URI + params + sign);
         request.setClazz(clazz);
         return request;
     }

     public static <T> GsonRequest<T> createPostRequest(final Class<T> clazz, final String URI, final Map<String, String> body) {
         final String sign = "?sign=" + StringUtils.securityMd5(StringUtils.mapSortToString(body));
         final GsonRequest<T> request = GsonRequest.getInstance(Request.Method.POST, URI + sign);
         request.setBodys(StringUtils.mapSortToMap(body));
         request.setClazz(clazz);
         return request;
     }

     public static <T> GsonRequest<T> createDeleteRequest(final Class<T> clazz, final String URI, final Map<String, String> map) {
         final String sign = "sign=" + StringUtils.securityMd5(StringUtils.mapSortToString(map));
         final String params = StringUtils.mapSortToParams(map);
         final GsonRequest<T> request = GsonRequest.getInstance(Request.Method.DELETE, URI + params + sign);
         request.setClazz(clazz);
         return request;
     }

     public static <T> MultipartRequest<T> createMultipartRequest(final Class<T> clazz, final String URI, final Map<String, Object> map) {
         final Map<String, String> mSignMap = new HashMap<String, String>();
         for (Map.Entry<String, Object> mEntry : map.entrySet()) {
             final Object mValue = mEntry.getValue();
             if (mValue instanceof String) {
                 mSignMap.put(mEntry.getKey(), (String) mValue);
             }
         }

         final String sign = "?sign=" + StringUtils.securityMd5(StringUtils.mapSortToString(mSignMap));
         final MultipartRequest<T> request = new MultipartRequest<T>(URI + sign);
         for (Map.Entry<String, Object> mEntry : map.entrySet()) {
             final Object mValue = mEntry.getValue();
             if (mValue instanceof String) {
                 request.addMultipartStringEntity(mEntry.getKey(), (String) mValue);
             } else if (mValue instanceof File) {
                 request.addMultipartFileEntity(mEntry.getKey(), (File) mValue);
             }
         }
         request.setClazz(clazz);
         return request;
     }
 }
