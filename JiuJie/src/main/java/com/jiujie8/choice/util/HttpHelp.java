package com.jiujie8.choice.util;

import android.content.Context;

import com.android.volley.Request;
import com.jiujie8.choice.Response;
import com.jiujie8.choice.core.AbstractRoboAsyncTask;
import com.jiujie8.choice.core.ProgressDialogTask;
import com.jiujie8.choice.home.entity.PostList;
import com.jiujie8.choice.http.GsonRequest;
import com.jiujie8.choice.http.HttpUtils;
import com.jiujie8.choice.http.MultipartRequest;


import java.util.Map;
import java.util.Objects;


/**
 * 网络请求封装
 */
public class HttpHelp {

    static HttpHelp instance = new HttpHelp();


    public static HttpHelp getInstance() {
        return instance;
    }


    /**
     * @param context  上下文context
     * @param clazz    传入解析类 response
     * @param url      请求 URL 地址
     * @param listener 请求网络结果监听
     */
    public <T> void getHttp(Context context, final Class<T> clazz, final String url, final Map<String, String> map, final OnCompleteListener<T> listener) {
        new AbstractRoboAsyncTask<T>(context) {
            @Override
            protected T run(Object data) throws Exception {
                GsonRequest<T> request = HttpUtils.createGetRequest(clazz, url, map);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccessCallback(T t) {
                listener.onComplete(t);
            }
//
//            @Override
//            protected void onSuccess(T response) throws Exception {
//                super.onSuccess(response);
//                if (response != null) {
//                    listener.onComplete(response);
//                }
//            }
        }.execute();
    }

    /**
     * @param context  上下文context
     * @param clazz    传入解析类 response
     * @param url      请求 URL 地址
     * @param message  上传中 show 信息
     * @param bodyMap  传入参数
     * @param listener 请求网络结果监听
     */

    public <T> void postMutilHttp(Context context, final Class<T> clazz, final String url, String message, final Map<String, Object> bodyMap, final OnCompleteListener<T> listener) {
        new ProgressDialogTask<T>(context) {
            @Override
            protected T run(Object data) {
                MultipartRequest<T> request = HttpUtils.createMultipartRequest(clazz, url, bodyMap);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(T response) throws Exception {
                super.onSuccess(response);
                if (response != null) {

                    listener.onComplete(response);
                }
            }

            @Override
            protected void onSuccessCallback(T t) {

            }


        }.start(message);
    }


    /**
     * @param context 上下文context
     * @param clazz   传入解析类 response
     * @param url     请求 URL 地址
     * @param message 加载中show 信息
     * @param listener 请求网络结果监听
     *
     */
   /* public <T> void getHttpDialog(Context context, final Class<T> clazz, final String url, String message,final OnCompleteListener<T> listener) {
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
    }*/

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
                GsonRequest<T> request = HttpUtils.createPostRequest(clazz,url,bodyMap);
                return (T) HttpUtils.doRequest(request).result;
            }

            @Override
            protected void onSuccess(T response) throws Exception {
                super.onSuccess(response);
                if (response != null) {

                    listener.onComplete(response);
                }
            }

            @Override
            protected void onSuccessCallback(T t) {

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
