package com.jiujie8.choice.http;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.GsonUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.core.Log;
import com.jiujie8.choice.util.IConstant;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by wlanjie on 14/10/21.
 */
public abstract class BaseRequest<T> extends Request<T> implements BasicNetwork.OnProgressListener {

    protected final Gson gson;
    private BasicNetwork.OnProgressListener mProgressListener;
    private Class<T> clazz;

    public BaseRequest(int method, String url) {
        super(method, url, null);
        gson = GsonUtils.createGson();
        setRetryPolicy(new DefaultRetryPolicy(20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public final void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void deliverResponse(T response) {

    }

    public void setOnProgressListener(BasicNetwork.OnProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return ChoiceApplication.getSignMap();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            if (IConstant.DEBUG) {
                Log.e("Volley", json);
                System.out.println("json = " + json);
            }
            return Response.success(gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public void onProgress(long current, long total) {
        if (mProgressListener != null) {
            mProgressListener.onProgress(current, total);
        }
    }
}
