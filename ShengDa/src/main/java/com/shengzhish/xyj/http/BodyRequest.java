package com.shengzhish.xyj.http;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.GsonUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.shengzhish.xyj.util.IConstant;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class BodyRequest<T> extends Request<T> {

	private final Gson gson;

	private final Response.Listener<T> mListener;
	private final Class<T> clazz;
	private Object requestBody;

	public BodyRequest(int method, String url, Object requestBody, Class<T> clazz,
			Response.Listener<T> listener) {
		super(method, IConstant.DOMAIN + url, null);
		mListener = listener;
		this.clazz = clazz;
		this.requestBody = requestBody;
		gson = GsonUtils.createGson();
		setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}


	@Override
	public String getBodyContentType() {
		return "application/json";
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			bos.write(gson.toJson(requestBody).getBytes("UTF-8"));
		} catch (Exception e) {
			VolleyLog.e("Exception writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			return Response.success(gson.fromJson(json, clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}
}
