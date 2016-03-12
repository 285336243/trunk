package com.example.xiadan.whole;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.xiadan.R;
import com.example.xiadan.core.ToastUtils;
import com.example.xiadan.loginregister.LoginActivity;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.util.NetWorkConnect;
import com.example.xiadan.util.ProgressDlgUtil;
import com.example.xiadan.util.StringUtils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 51wanh on 2015/3/5.
 */
public class BaseActivity extends SherlockFragmentActivity {


    protected Activity activity;
    protected ActionBar actionBar;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        actionBar = getSupportActionBar();
        actionBar.setTitle("返回");
        //设置可点击
        actionBar.setDisplayHomeAsUpEnabled(true);
        activity = BaseActivity.this;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mRequestQueue) {
            mRequestQueue.cancelAll(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IConstant.LOGIN_CODE) {
            onceGetData();
        }
    }

    protected void onceGetData() {

    }

    protected void startActivityForResult() {
        startActivityForResult(new Intent(this, LoginActivity.class), IConstant.LOGIN_CODE);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    protected void startActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    /**
     * 检查是否为空，并提示
     *
     * @param str 字符产
     * @param msg 提示toast
     * @return 是空提示，返回true     不是空返回false
     */
    protected boolean checkIsEmpty(String str, String msg) {
        if (TextUtils.isEmpty(str)) {
            ToastUtils.show(this, msg);
            return true;
        } else {
            return false;
        }
    }

    protected void showCancelDialog() {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.edit_cancel_dialog);
        dialog.findViewById(R.id.edit_cancel_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        dialog.findViewById(R.id.edit_continue_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        if (!dialog.isShowing())
            dialog.show();
    }

    /**
     * 得到输入内容
     *
     * @param editText edittext
     * @return 输入内容
     */
    protected String getInputStr(EditText editText) {
        return editText.getText().toString().trim();
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
                errorResponse(error);
//                System.out.println("请求错误:" + error.toString());
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
        if (error.networkResponse == null) {
            if (!NetWorkConnect.isConnect(activity)) {
                ToastUtils.show(activity, "没有网络连接，请检查");
                return;
            }
        } else {
            if (error.networkResponse.statusCode == 401) {
                startActivity(LoginActivity.class);
            }
        }
    }
}
