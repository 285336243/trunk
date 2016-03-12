package com.mzs.guaji.ui;

import com.mzs.guaji.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

/**
 * 播放web视频 activity
 * @author lenovo
 *
 */
public class WebPlayerVideoActivity extends GuaJiActivity {

	private WebView mWebView;
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.web_player_video);

		String playerUrl = getIntent().getStringExtra("playerUrl");
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading);
		mWebView = (WebView) findViewById(R.id.web_player_video_webview);
		mWebView.setWebChromeClient(new WebChromeClient());
		WebSettings mWebSettings = mWebView.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setLoadWithOverviewMode(true);
		mWebView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
                mLoadingLayout.setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
                mLoadingLayout.setVisibility(View.GONE);
			}
			
		});
		mWebView.loadUrl(playerUrl);
	}
	
	@Override
	protected void onDestroy() {
		mWebView.destroy();
		super.onDestroy();
	}
}
