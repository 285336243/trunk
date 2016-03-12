package com.shengzhish.xyj.persionalcore;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.DialogFragmentActivity;
import com.shengzhish.xyj.util.IConstant;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-6-18.
 */
@ContentView(R.layout.about)
public class AboutActivity extends DialogFragmentActivity {

    private final static String ABOUT_URL = IConstant.DOMAIN + "system/about.html";

    @InjectView(R.id.about_back)
    private View backView;

    @InjectView(R.id.about_webview)
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView.loadUrl(ABOUT_URL);
    }
}
