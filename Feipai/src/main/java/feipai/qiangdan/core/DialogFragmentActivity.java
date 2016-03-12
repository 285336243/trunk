/*
 * Copyright 2012 GitHub Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package feipai.qiangdan.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.kevinsawicki.wishlist.ViewFinder;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

import feipai.qiangdan.crashcatch.AppManager;
import feipai.qiangdan.my.LoginActivity;
import feipai.qiangdan.util.IConstant;
import feipai.qiangdan.util.ProgressDlgUtil;
import feipai.qiangdan.util.StringUtils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS;

/**
 * Activity that display dialogs
 */
public abstract class DialogFragmentActivity<E> extends
        RoboSherlockFragmentActivity implements DialogResultListener {

    /**
     * Finder bound to this activity's view
     */
    protected ViewFinder finder;

    protected CacheRepository repository;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    protected int requestCount = 20;

    protected int requestPage = 1;
    protected DialogFragmentActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        activity = this;
        finder = new ViewFinder(this);

        repository = CacheRepository.getInstance().fromContext(this);
        AppManager.getAppManager().addActivity(this);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return serializable
     */
    @SuppressWarnings("unchecked")
    protected <V extends Serializable> V getSerializableExtra(final String name) {
        return (V) getIntent().getSerializableExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return int
     */
    protected int getIntExtra(final String name) {
        return getIntent().getIntExtra(name, -1);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return
     */
    protected long getLongExtra(final String name) {
        return getIntent().getLongExtra(name, -1);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return int array
     */
    protected int[] getIntArrayExtra(final String name) {
        return getIntent().getIntArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return boolean array
     */
    protected boolean[] getBooleanArrayExtra(final String name) {
        return getIntent().getBooleanArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string
     */
    protected String getStringExtra(final String name) {
        return getIntent().getStringExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return string array
     */
    protected String[] getStringArrayExtra(final String name) {
        return getIntent().getStringArrayExtra(name);
    }

    /**
     * Get intent extra
     *
     * @param name
     * @return char sequence array
     */
    protected CharSequence[] getCharSequenceArrayExtra(final String name) {
        return getIntent().getCharSequenceArrayExtra(name);
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        // Intentionally left blank
    }

    /**
     * Set view is gone
     *
     * @param view
     * @return
     */
    protected DialogFragmentActivity hide(final View view) {
        ViewUtils.setGone(view, true);
        return this;
    }

    /**
     * Set view is visible
     *
     * @param view
     * @return
     */
    protected DialogFragmentActivity show(final View view) {
        ViewUtils.setGone(view, false);
        return this;
    }

    /**
     * 利用Volley实现Post请求
     *
     * @param url     地址
     * @param hashMap 参数
     */
    public void volley_post(String url, final HashMap<String, String> hashMap) {
        ProgressDlgUtil.showProgressDlg(activity, "请稍后...");
        mRequestQueue = Volley.newRequestQueue(activity);
        mStringRequest = new StringRequest(Request.Method.POST, IConstant.DOMAIN + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ProgressDlgUtil.stopProgressDlg();
//                        System.out.println("请求结果:" + response);
                        correcttResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ProgressDlgUtil.stopProgressDlg();
//                System.out.println("请求错误:" + error.toString());
                errorResponse(error);
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


    /**
     * 利用Volley实现Get请求
     *
     * @param uri 地址
     * @param map 参数
     */
    public void volley_get(String uri, Map<String, String> map) {
        //加载提示框
//        ProgressDlgUtil.showProgressDlg(activity, "正在加载");
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
//        String url = "http://www.aplesson.com/";
        // 1 创建RequestQueue对象
        mRequestQueue = Volley.newRequestQueue(activity);
        // 2 创建StringRequest对象
        mStringRequest = new StringRequest(IConstant.DOMAIN + sb.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        ProgressDlgUtil.stopProgressDlg();
//                        System.out.println("请求结果:" + response);
                        correcttResponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                ProgressDlgUtil.stopProgressDlg();
                System.out.println("请求错误:" + error.toString());
            }
        });
        // 3 将StringRequest添加到RequestQueue
        mRequestQueue.add(mStringRequest);
    }

    /**
     * 正确结果
     *
     * @param response 结果
     */
    public void correcttResponse(String response) {

    }

    /**
     * 错误结果
     *
     * @param error 错误
     */
    public void errorResponse(VolleyError error) {
        //HTTP Status: 401                    // 登陆失败
        //Response Body: "失败原因，如 用户名或密码错误,用户不存在"
        if (error.networkResponse.statusCode == 401) {
            ToastUtils.show(activity, " 登陆超时,请重登陆");
            startedActivity(LoginActivity.class);
        }
    }

    protected void startedActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mRequestQueue) {
            mRequestQueue.cancelAll(this);
        }
    }
}
