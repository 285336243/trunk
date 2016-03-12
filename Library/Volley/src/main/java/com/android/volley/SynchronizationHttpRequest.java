package com.android.volley;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by wlanjie on 14-3-1.
 */
public class SynchronizationHttpRequest<V> extends Request<String> {

    /** The cache triage queue. */
    private final PriorityBlockingQueue<Request> mCacheQueue =
            new PriorityBlockingQueue<Request>();

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request> mNetworkQueue =
            new PriorityBlockingQueue<Request>();

    /** The cache dispatcher. */
    private CacheDispatcher mCacheDispatcher;

    private static Network network;

    private static DiskBasedCache mCache;
    private static final ExecutorDelivery mDelivery = new ExecutorDelivery(new Handler(Looper.getMainLooper()));

    protected NetworkResponse networkResponse;

    protected Gson gson = GsonUtils.getGson();

    protected Map<String, String> headers;

    protected Type type;

    protected Class<V> clazz;

    public SynchronizationHttpRequest(int method, String url, Map<String, String> headers, Response.ErrorListener listener) {
        super(method, url, listener);
        this.headers = headers;
        setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public SynchronizationHttpRequest createHttp(Context context, HttpStack stack) {
        File cacheDir = new File(context.getCacheDir(), "volly");

        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }

        if (stack == null) {
            if (Build.VERSION.SDK_INT >= 9) {
                stack = new HurlStack();
            } else {
                // Prior to Gingerbread, HttpUrlConnection was unreliable.
                // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
                stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
            }
        }

        network = new BasicNetwork(stack);
        mCache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.add(this);
        synchronizationStart(queue);
        return this;
    }

    public void synchronizationStart(RequestQueue queue) {
        queue.stop();
        mCacheDispatcher = new CacheDispatcher(mCacheQueue, mNetworkQueue, mCache, mDelivery);
        mCacheDispatcher.start();
        networkRequest(mCache, mDelivery);
    }


    public void networkRequest(DiskBasedCache mCache, ExecutorDelivery mDelivery) {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        try {
            addMarker("network-queue-take");

            // If the request was cancelled already, do not perform the
            // network request.
            if (isCanceled()) {
                finish("network-discard-cancelled");
            }

            // Tag the request (if API >= 14)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                TrafficStats.setThreadStatsTag(getTrafficStatsTag());
            }

            // Perform the network request.
            networkResponse = network.performRequest(this);
            addMarker("network-http-complete");

            // If the server returned 304 AND we delivered a response already,
            // we're done -- don't deliver a second identical response.
            if (networkResponse.notModified && hasHadResponseDelivered()) {
                finish("not-modified");
            }

            // Parse the response here on the worker thread.
            Response<?> response = parseNetworkResponse(networkResponse);
            addMarker("network-parse-complete");

            // Write to cache if applicable.
            if (shouldCache() && response.cacheEntry != null) {
                mCache.put(getCacheKey(), response.cacheEntry);
                addMarker("network-cache-written");
            }

            // Post the response back.
            markDelivered();
//            mDelivery.postResponse(this, response);
//            responseDelivery(response, this);
        } catch (VolleyError volleyError) {
            parseAndDeliverNetworkError(this, volleyError);
        } catch (Exception e) {
            VolleyLog.e(e, "Unhandled exception %s", e.toString());
            mDelivery.postError(this, new VolleyError(e));
        }
    }

    private void parseAndDeliverNetworkError(Request<?> request, VolleyError error) {
        error = request.parseNetworkError(error);
        mDelivery.postError(request, error);
    }

    private void responseDelivery(Response<?> response, Request<String> request) {
        if (response.isSuccess()) {
            request.deliverResponse((String) response.result);
        } else {
            request.deliverError(response.error);
        }

        if (response.intermediate) {
            request.addMarker("intermediate-response");
        } else {
            request.finish("done");
        }
    }

    public Object getResponse(PagedRequest request) {
        String parsed;
        if (networkResponse != null && networkResponse.data != null) {
            try {
                parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
        Type type = request.getType();
        if (type != null) {
            return parseJson(parsed, type, request.getArrayType());
        } else {
            return parseJson(parsed, request.getClazz());
        }
    }

    public V getResponse() {
        String parsed;
        if (networkResponse != null && networkResponse.data != null) {
            try {
                parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
        if (clazz != null) {
            return parseJson(parsed, clazz);
        } else {
            return parseJson(parsed, type, null);
        }
    }

    public <V> V parseJson(String response, Type type, Type listType) {
        if (listType == null) {
            try {
                return gson.fromJson(response, type);
            } catch (JsonParseException jpe) {
                return null;
            }
        } else {
            return gson.fromJson(response, listType);
        }
    }

    public <V> V parseJson(String response, Class<V> clazz) {
        try {
            return gson.fromJson(response, clazz);
        } catch (JsonParseException jpe) {
            return null;
        }
    }

    public SynchronizationHttpRequest setType(Type type) {
        this.type = type;
        return this;
    }

    public SynchronizationHttpRequest setClazz(Class<V> clazz) {
        this.clazz = clazz;
        return this;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return super.getHeaders();
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return headers;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
//        System.out.println(response);
    }
}
