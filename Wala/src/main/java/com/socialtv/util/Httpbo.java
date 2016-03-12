package com.socialtv.util;

import android.content.Context;
import android.text.TextUtils;


import com.android.volley.Request;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.ProgressDialogTask;

import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.http.MultipartRequest;
import com.socialtv.personcenter.entity.UserResponse;

import java.util.Map;


/**
 * 网络请求封装
 *
 * @param <T>传入解析类 response
 */
public class Httpbo<T> {
    private OnCompleteListener<T> listener;

    public void setListener(OnCompleteListener<T> listener) {
        this.listener = listener;
    }

    /**
     * @param context 上下文context
     * @param clazz   传入解析类 response
     * @param url     请求 URL 地址
     */
    public void getHttp(Context context, final Class<T> clazz, final String url) {
        new AbstractRoboAsyncTask<T>(context) {
            @Override
            protected T run(Object data) throws Exception {
                GsonRequest<T> request = new GsonRequest<T>(Request.Method.GET, url);
                request.setClazz(clazz);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(T response) throws Exception {
                super.onSuccess(response);
                if (response != null) {

                    listener.onComplete(response);
                }
            }
        }.execute();
    }

    /**
     * @param context 上下文context
     * @param clazz   传入解析类 response
     * @param url     请求 URL 地址
     * @param message  加载中show 信息
     */
    public void getHttpDialog(Context context, final Class<T> clazz, final String url,String message) {
        new ProgressDialogTask<T>(context) {
            @Override
            protected T run(Object data) throws Exception {
                GsonRequest<T> request = new GsonRequest<T>(Request.Method.GET, url);
                request.setClazz(clazz);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(T response) throws Exception {
                super.onSuccess(response);
                if (response != null) {

                    listener.onComplete(response);
                }
            }
        }.start(message);
    }

    /**
     * @param context 上下文context
     * @param clazz   传入解析类 response
     * @param url     请求 URL 地址
     * @param message 上传中 show 信息
     * @param bodyMap 传入参数，
     */
    public void postHttp(Context context, final Class<T> clazz, final String url, String message, final Map<String, String> bodyMap) {
        new ProgressDialogTask<T>(context) {

            @Override
            protected T run(Object data) {
                GsonRequest<T> request = new GsonRequest<T>(Request.Method.POST, url);
                request.setClazz(clazz);
                request.setHeaders(bodyMap);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(T response) throws Exception {
                super.onSuccess(response);
                if (response != null) {

                    listener.onComplete(response);
                }
            }
        }.start(message);
    }


    /**
     * @param <T> 传入解析类
     */
    public interface OnCompleteListener<T> {


        /**
         * @param response 请求数据
         */
        public void onComplete(T response);
    }
}
