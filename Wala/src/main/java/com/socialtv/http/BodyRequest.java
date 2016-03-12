package com.socialtv.http;

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
import com.socialtv.util.IConstant;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class BodyRequest<T> extends BaseRequest<T> {

//	private final Gson gson;

//	private Class<T> clazz;
	private Object requestBody;

	public BodyRequest(int method, String url, Object requestBody) {
		super(method, IConstant.DOMAIN + url, null);
		this.requestBody = requestBody;
//		gson = GsonUtils.createGson();
		setRetryPolicy(new DefaultRetryPolicy(20000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

//    public final void setClazz(Class<T> clazz) {
//        this.clazz = clazz;
//    }

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

//	@Override
//	protected Response<T> parseNetworkResponse(NetworkResponse response) {
//		try {
//			String json = new String(response.data,
//					HttpHeaderParser.parseCharset(response.headers));
//			return Response.success(gson.fromJson(json, clazz),
//					HttpHeaderParser.parseCacheHeaders(response));
//		} catch (UnsupportedEncodingException e) {
//			return Response.error(new ParseError(e));
//		} catch (JsonSyntaxException e) {
//			return Response.error(new ParseError(e));
//		}
//	}
//
//	@Override
//	protected void deliverResponse(T response) {
//
//	}
}
