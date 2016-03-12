package com.shengzhish.xyj.core;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.SynchronizationHttpRequest;
import com.android.volley.toolbox.HttpClientStack;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.util.IConstant;

import java.util.Map;

/**
 * Created by wlanjie on 14-3-7.
 */
public class RequestUtils<V> {

    public SynchronizationHttpRequest<V> createGet(Context context, String uri, Response.ErrorListener errorListener) {
        SynchronizationHttpRequest<V> httpRequest = new SynchronizationHttpRequest(Request.Method.GET, IConstant.DOMAIN + uri, null, errorListener);
        httpRequest.createHttp(context, new HttpClientStack(HttpUtils.getHttpClient(context)));
        return httpRequest;
    }

    public SynchronizationHttpRequest<V> createPost(Context context, String uri, Map<String, String> bodys, Response.ErrorListener errorListener) {
        SynchronizationHttpRequest<V> httpRequest = new SynchronizationHttpRequest(Request.Method.POST, IConstant.DOMAIN + uri, bodys, errorListener);
        httpRequest.createHttp(context, new HttpClientStack(HttpUtils.getHttpClient(context)));
        return httpRequest;
    }

    public SynchronizationHttpRequest<V> createDelete(Context context, String uri, Response.ErrorListener errorListener) {
        SynchronizationHttpRequest<V> httpRequest = new SynchronizationHttpRequest(Request.Method.DELETE, IConstant.DOMAIN + uri, null, errorListener);
        httpRequest.createHttp(context, new HttpClientStack(HttpUtils.getHttpClient(context)));
        return httpRequest;
    }

    public static RequestUtils getInstance() {
        return new RequestUtils();
    }

}
