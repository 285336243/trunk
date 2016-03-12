package feipai.qiangdan.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装的Volley
 */
public class VolleyUtil {
    private Context context;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    private VolleyUtil(Context context) {
        this.context = context;
    }

    public static VolleyUtil getInstance(Context context) {
        VolleyUtil vu = new VolleyUtil(context);
        return vu;
    }

    /**
     * 利用Volley实现Put请求
     *
     * @param url      地址
     * @param hashMap  参数
     * @param listener 监听
     */
    public void volley_get(String url, final HashMap<String, String> hashMap, final OnCompleteListener listener) {
//        ProgressDlgUtil.showProgressDlg(context, "请稍后...");

        //加载提示框
//        ProgressDlgUtil.showProgressDlg(activity, "正在加载");
        Map<String, String> params = StringUtils.sorting(hashMap);
        // StringBuilder是用来组拼请求地址和参数
        final StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
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

        mRequestQueue = Volley.newRequestQueue(context);
        mStringRequest = new StringRequest(IConstant.DOMAIN + sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        ProgressDlgUtil.stopProgressDlg();
                        listener.correct(response);
                        // Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        ProgressDlgUtil.stopProgressDlg();
                        listener.error(error);
                        // Log.d("Error.Response", response);
                    }
                }
        ) /*{

            @Override
            protected Map<String, String> getParams() {
     *//*           Map<String, String>  params = new HashMap<String, String> ();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;*//*
                HashMap<String, String> rehashMap = (HashMap<String, String>) StringUtils.sorting(hashMap);
                return rehashMap;
            }

        }*/;

        mRequestQueue.add(mStringRequest);
    }

    /**
     * 利用Volley实现Put请求
     *
     * @param url      地址
     * @param hashMap  参数
     * @param listener 监听
     */
    public void volley_put(String url, final HashMap<String, String> hashMap, final OnCompleteListener listener) {
        ProgressDlgUtil.showProgressDlg(context, "请稍后...");
        mRequestQueue = Volley.newRequestQueue(context);
        mStringRequest = new StringRequest(Request.Method.PUT, IConstant.DOMAIN + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressDlgUtil.stopProgressDlg();
                        listener.correct(response);
                        // Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ProgressDlgUtil.stopProgressDlg();
                        listener.error(error);
                        // Log.d("Error.Response", response);
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
     /*           Map<String, String>  params = new HashMap<String, String> ();
                params.put("name", "Alif");
                params.put("domain", "http://itsalif.info");

                return params;*/
                HashMap<String, String> rehashMap = (HashMap<String, String>) StringUtils.sorting(hashMap);
                return rehashMap;
            }

        };

        mRequestQueue.add(mStringRequest);
    }

    /**
     * 利用Volley实现Post请求
     *
     * @param url     地址
     * @param hashMap 参数
     */
    public void volley_post(String url, final HashMap<String, String> hashMap, final OnCompleteListener listener) {
        ProgressDlgUtil.showProgressDlg(context, "请稍后...");
        mRequestQueue = Volley.newRequestQueue(context);
        mStringRequest = new StringRequest(Request.Method.POST, IConstant.DOMAIN + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressDlgUtil.stopProgressDlg();
//                        System.out.println("请求结果:" + response);
                        listener.correct(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProgressDlgUtil.stopProgressDlg();
//                System.out.println("请求错误:" + error.toString());
                listener.error(error);
            }
        }) {
            // 携带参数
            @Override
            protected HashMap<String, String> getParams()
                    throws AuthFailureError {
/*                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("un", "852041173");
                hashMap.put("pw", "852041173abc");*/
                HashMap<String, String> rehashMap = (HashMap<String, String>) StringUtils.sorting(hashMap);
                return rehashMap;
            }

            // Volley请求类提供了一个 getHeaders（）的方法，重载这个方法可以自定义HTTP 的头信息。（也可不实现）
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

        };

        mRequestQueue.add(mStringRequest);

    }
    public void cancelVolley(){
        if (null != mRequestQueue) {
            mRequestQueue.cancelAll(this);
        }
    }
    public interface OnCompleteListener {
        /**
         * @param str 传入字符串
         */
        public void correct(String str);

        /**
         * @param error 错误
         */
        public void error(VolleyError error);

    }
}
