package com.jiujie8.choice.http;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyLog;
import com.jiujie8.choice.util.IConstant;

import java.io.ByteArrayOutputStream;

public class BodyRequest<T> extends BaseRequest<T> {

    private Object requestBody;

	public BodyRequest(int method, String url, Object requestBody) {
		super(method, IConstant.DOMAIN + url);
		this.requestBody = requestBody;
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
}
