package com.example.xiadan.loginregister;


import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.xiadan.R;
import com.example.xiadan.menucontent.ProgressWebView;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.whole.BaseActivity;

/**
 * Created by 51wanh on 2015/3/16.
 */
public class UserProtocal extends BaseActivity {
    private ProgressWebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us);
        actionBar.setTitle("用户协议");
        mWeb = (ProgressWebView) findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        mWeb.getSettings().setJavaScriptEnabled(true);
//        mWeb.loadUrl(String.format(WEB_URL,data.getId()));
        mWeb.loadUrl(IConstant.DOMAIN + "api/v1/deal/user");
//        mWeb.loadUrl("http://www.baidu.com/");
        //设置Web视图
        mWeb.setWebViewClient(new HelloWebViewClient());
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
