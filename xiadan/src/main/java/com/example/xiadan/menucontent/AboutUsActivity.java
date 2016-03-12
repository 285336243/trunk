package com.example.xiadan.menucontent;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.xiadan.R;
import com.example.xiadan.util.IConstant;
import com.example.xiadan.whole.BaseActivity;

/**
 * Created by 51wanh on 2015/3/4.
 */
public class AboutUsActivity extends BaseActivity {

    private ProgressWebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us);
        actionBar.setTitle("关于我们");
        mWeb = (ProgressWebView) findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        mWeb.getSettings().setJavaScriptEnabled(true);
        mWeb.loadUrl(IConstant.DOMAIN+"api/v1/we");
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
