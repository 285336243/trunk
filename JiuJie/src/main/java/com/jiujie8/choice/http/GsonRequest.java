package com.jiujie8.choice.http;

import com.android.volley.AuthFailureError;
import com.jiujie8.choice.ChoiceApplication;
import com.jiujie8.choice.util.IConstant;

import java.util.Map;

/**
 * Volley adapter for JSON requests that will be parsed into Java objects by
 * Gson.
 */
public class GsonRequest<T> extends BaseRequest<T> {
	private Map<String, String> bodys;

    public final static <T> GsonRequest<T> getInstance(final int method, final String url) {
        GsonRequest<T> mRequest = new GsonRequest<T>(method, url);
        return mRequest;
    }

	/**
	 * Make a GET request and return a parsed object from JSON.
	 * 
	 * @param url
	 *            URL of the request to make
	 */
	private GsonRequest(int method, String url) {
		super(method,  IConstant.DOMAIN + url);
	}

    public GsonRequest<T> setBodys(Map<String, String> bodys) {
        this.bodys = bodys;
        return this;
    }

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return bodys;
	}
}