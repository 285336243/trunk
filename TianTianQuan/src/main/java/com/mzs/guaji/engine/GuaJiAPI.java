package com.mzs.guaji.engine;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.mzs.guaji.http.BodyRequest;
import com.mzs.guaji.http.GsonRequest;
import com.mzs.guaji.http.HttpUtils;
import com.mzs.guaji.http.MultipartRequest;

import java.util.Map;

public class GuaJiAPI {
	
	private static GuaJiAPI mApi;
	private RequestQueue mQueue;
	public Object object;
//	private final static String tag = GuaJiAPI.class.getSimpleName();
	
	private GuaJiAPI(Context context) {
		mQueue = Volley.newRequestQueue(context, new HttpClientStack(HttpUtils.getHttpClient(context)));
		object = new Object();
	}

	public static GuaJiAPI newInstance(Context context) {
		if(mApi == null) {
			mApi = new GuaJiAPI(context);
		}
		return mApi;
	}
	
	public <T> GsonRequest<T> requestGetData(String requestUrl, Class<T> clazz, final Response.Listener<T> succeedListener, final Response.ErrorListener errorListener) {
		GsonRequest<T> mGsonRequest = new GsonRequest<T>(Method.GET, requestUrl, clazz, null,  succeedListener, errorListener);
		mGsonRequest.setTag(object);
		mQueue.add(mGsonRequest);
		return mGsonRequest;
	}
	
	public <T> GsonRequest<T> requestPostData(String requestUrl, Class<T> clazz, Map<String, String> headers, final Response.Listener<T> succeedListener,final Response.ErrorListener errorListener) {
		GsonRequest<T> mGsonRequest = new GsonRequest<T>(Method.POST, requestUrl, clazz, headers, succeedListener, errorListener);
		mGsonRequest.setTag(object);
		mQueue.add(mGsonRequest);
		return mGsonRequest;
	}
	
	public <T> GsonRequest<T> requestDeleteData(String requestUrl, Class<T> clazz, final Response.Listener<T> succeedListener,final Response.ErrorListener errorListener) {
		GsonRequest<T> mGsonRequest = new GsonRequest<T>(Method.DELETE, requestUrl, clazz, null, succeedListener, errorListener);
		mGsonRequest.setTag(object);
		mQueue.add(mGsonRequest);
		return mGsonRequest;
	}

	public <T> BodyRequest<T> requestJsonPostData(int method, String requestUrl, Object object, Class<T> clazz, final Response.Listener<T> succeedListener,final Response.ErrorListener errorListener) {
		BodyRequest<T> mGsonRequest = new BodyRequest<T>(method, requestUrl, object, clazz, succeedListener, errorListener);
		mGsonRequest.setTag(object);
		mQueue.add(mGsonRequest);
		return mGsonRequest;
	}
	
	public <T> MultipartRequest<T> requestMultipartPostData(String requestUrl, Class<T> clazz, final Response.Listener<T> succeedListener,final Response.ErrorListener errorListener) {
		MultipartRequest<T> mGsonRequest = new MultipartRequest<T>(requestUrl, clazz, succeedListener, errorListener);
		mGsonRequest.setTag(object);
		return mGsonRequest;
	}
	
	public <T> void addRequest(Request<T> request){
		mQueue.add(request);
	}
	
	public void stopAllRequest() {
		mQueue.cancelAll(object);
	}

    public void clearCookie(){
        HttpUtils.clearCookie();
    }
}
