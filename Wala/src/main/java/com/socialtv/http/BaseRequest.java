package com.socialtv.http;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.android.volley.GsonUtils;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.socialtv.WalaApplication;
import com.socialtv.util.AppManager;
import com.socialtv.util.DialogUtils;
import com.socialtv.util.IConstant;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by wlanjie on 14/10/21.
 */
public abstract class BaseRequest<T> extends Request<T> {

    protected final Gson gson;
    protected Class<T> clazz;

    public BaseRequest(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
        gson = GsonUtils.createGson();
    }

    public final void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    protected void deliverResponse(T response) {

    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (jsonObject != null) {
                    JSONObject scoreObject = jsonObject.getJSONObject("givenScore");
                    if (scoreObject != null) {
                        Intent intent = new Intent(IConstant.SHOW_SCORE);
                        intent.putExtra(IConstant.SCORE, scoreObject.getString("message"));
                        WalaApplication.getContext().sendBroadcast(intent);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Response.success(gson.fromJson(json, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
