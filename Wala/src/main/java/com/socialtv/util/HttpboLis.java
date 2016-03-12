package com.socialtv.util;

import android.content.Context;

import com.android.volley.Request;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;

import java.util.Map;


/**
 * 网络请求封装
 *
 *
 */
public class HttpboLis {

    static HttpboLis instance=new HttpboLis();


    public static HttpboLis getInstance() {
        return instance;
    }


    /**
     * @param context 上下文context
     * @param clazz   传入解析类 response
     * @param url     请求 URL 地址
     * @param listener 请求网络结果监听
     */
    public <T> void getHttp(Context context, final Class<T> clazz, final String url, final OnCompleteListener<T> listener) {
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
     * @param message 加载中show 信息
     * @param listener 请求网络结果监听
     *
     */
    public <T> void getHttpDialog(Context context, final Class<T> clazz, final String url, String message,final OnCompleteListener<T> listener) {
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
     * @param bodyMap 传入参数
     * @param listener 请求网络结果监听
     *
     */
    public <T> void postHttp(Context context, final Class<T> clazz, final String url, String message, final Map<String, String> bodyMap, final OnCompleteListener<T> listener) {
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
