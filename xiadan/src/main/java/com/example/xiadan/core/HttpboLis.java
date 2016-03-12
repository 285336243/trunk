package com.example.xiadan.core;

import android.content.Context;


import com.android.volley.Request;
import com.example.xiadan.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
     * @param uri     请求 URL 地址
     * @param message 加载中show 信息
     * @param listener 请求网络结果监听
     *
     */
    public <T> void getHttpDialog(Context context, final Class<T> clazz, final String uri,Map<String, String> map, String message,final OnCompleteListener<T> listener)  {
        Map<String, String> params = StringUtils.sorting(map);
        // StringBuilder是用来组拼请求地址和参数
        final StringBuilder sb = new StringBuilder();
        sb.append(uri).append("?");
        if (params != null && params.size() != 0) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //如果请求参数中有中文，需要进行URLEncoder编码
                try {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                sb.append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        new ProgressDialogTask<T>(context) {
            @Override
            protected T run(Object data) throws Exception {
                GsonRequest<T> request = new GsonRequest<T>(Request.Method.GET, sb.toString());
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
