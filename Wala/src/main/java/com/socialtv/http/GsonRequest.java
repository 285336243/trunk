package com.socialtv.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.GsonUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.socialtv.util.AppManager;
import com.socialtv.util.IConstant;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by
 * Gson.
 */
public class GsonRequest<T> extends BaseRequest<T> {
//	private final Gson gson;
//	private Class<T> clazz;
	private Map<String, String> headers;
    private Type type;

	/**
	 * Make a GET request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 */
	public GsonRequest(int method, String url) {
		super(method, IConstant.DOMAIN + url, null);
//        gson = GsonUtils.createGson();
	}

//    public GsonRequest<T> setClazz(Class<T> clazz) {
//        this.clazz = clazz;
//        return this;
//    }

    public GsonRequest<T> setType(Type type) {
        this.type = type;
        return this;
    }

    public GsonRequest<T> setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return super.getHeaders();
	}

//	@Override
//	protected void deliverResponse(T response) {
//		listener.onResponse(response);
//	}
	
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return headers;
	}
	
//	@Override
//	protected Response<T> parseNetworkResponse(NetworkResponse response) {
//		try {
//			String json = new String(response.data,
//					HttpHeaderParser.parseCharset(response.headers));
//            return Response.success(gson.fromJson(json, clazz),
//                    HttpHeaderParser.parseCacheHeaders(response));
//		} catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//			return Response.error(new ParseError(e));
//		} catch (JsonSyntaxException e) {
//            e.printStackTrace();
//			return Response.error(new ParseError(e));
//		}
//	}
}