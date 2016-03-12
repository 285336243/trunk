package com.shengzhish.xyj.news;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.util.IConstant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-6-17.
 */

@ContentView(R.layout.dissertation)
public class DissertationActivity extends DialogFragmentActivity {

    @InjectView(R.id.dissertation_webview)
    private WebView webView;

    @InjectView(R.id.dissertation_back)
    private View backView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    private String id;

    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        id = getStringExtra(IConstant.DISSERTATION_ID);

        url = getStringExtra(IConstant.DISSERTATION_EXTRA);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hide(progressBar);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                hide(progressBar);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this, "sdo");

        webView.loadUrl(url);
    }

    @JavascriptInterface
    public void openNews(String id) {
        startActivity(new Intents(this, DissertationActivity.class).add(IConstant.DISSERTATION_ID, id).add(IConstant.DISSERTATION_EXTRA, String.format(IConstant.NEWS_URL, id)).toIntent());
    }
}
