package com.example.xiadan.home;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.example.xiadan.R;
import com.example.xiadan.menucontent.ProgressWebView;

/**
 * Created by 51wanh on 2015/3/4.
 */
public class QuestionActivity extends SherlockFragmentActivity {

    private ProgressWebView mWeb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about_us);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("返回");
        //设置可点击
        actionBar.setDisplayHomeAsUpEnabled(true);
        mWeb = (ProgressWebView) findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        mWeb.getSettings().setJavaScriptEnabled(true);
//        mWeb.loadUrl(String.format(WEB_URL,data.getId()));
        mWeb.loadUrl("http://www.ifeipai.com/api/v1/req");
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
