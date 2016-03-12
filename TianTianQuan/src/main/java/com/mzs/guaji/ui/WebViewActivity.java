package com.mzs.guaji.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mzs.guaji.R;
import com.mzs.guaji.http.HttpUtils;
import com.mzs.guaji.http.SQLiteCookieStore;
import com.mzs.guaji.util.LoginUtil;
import com.mzs.guaji.util.SkipPersonalCenterUtil;
import com.mzs.guaji.util.ToastUtil;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.List;


/**
 * Created by sunjian on 13-12-26.
 */
public class WebViewActivity extends GuaJiActivity implements View.OnClickListener{

    private LinearLayout backLayout;
    private TextView titleTextView;
    private WebView webView;
    private MediaPlayer mediaPlayer;
    private String callback;
    private View titleLayout;
    private String backKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_layout);
        titleLayout = findViewById(R.id.web_title_layout);
        backLayout = (LinearLayout)findViewById(R.id.web_view_back);
        backLayout.setOnClickListener(this);
        titleTextView = (TextView)findViewById(R.id.web_view_title);
        webView = (WebView) findViewById(R.id.web_view);
        mLoadingLayout = (RelativeLayout) findViewById(R.id.loading);
        String title = getIntent().getStringExtra("title");
        String webUrl = getIntent().getStringExtra("url");
        String noTitle = getIntent().getStringExtra("noTitle");
        backKey = getIntent().getStringExtra("backKey");
        titleTextView.setText(title);
        if ("true".equals(noTitle)) {
            titleLayout.setVisibility(View.GONE);
        } else {
            titleLayout.setVisibility(View.VISIBLE);
        }

        webView.setWebChromeClient(new WebChromeClient());
        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this, "ttq");
        SQLiteCookieStore cookieStore = HttpUtils.getCookieStore();
        CookieManager cookieManager = CookieManager.getInstance();
        List<Cookie> cookies = cookieStore.getCookies();
        for(int i=0; i<cookies.size(); i++) {
            cookieManager.setCookie(webUrl, cookies.get(i).getName() + "="+ cookies.get(i).getValue()+"; domain="+cookies.get(i).getDomain());
            CookieSyncManager manager = CookieSyncManager.getInstance();
            if (manager != null) {
                manager.sync();
            }
        }
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webView.setWebViewClient(new WebViewClient(){

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
        webView.loadUrl(webUrl);
//        webView.loadUrl("http://social.api.ttq2014.com/vote/home.html?vid=1&tp=zhaml");
    }


    @Override
    public void onClick(View v) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

//    private Handler

    /**
     * 开始播放音频
     * @param audioUrl
     */
    @JavascriptInterface
    public void playAudio(String audioUrl, final String callback) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
//            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    webView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:"+ callback);
                        }
                    }, 1);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    ToastUtil.showToast(WebViewActivity.this, "播放出错了");
                    return false;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 暂停播放音频
     */
    @JavascriptInterface
    public void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @JavascriptInterface
    public void resumeAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    /**
     * 停止播放音频
     */
    @JavascriptInterface
    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    /**
     * 根据userId进入个人中心
     * @param userId
     */
    @JavascriptInterface
    public void enterPersonInfo(long userId) {
        if (userId != LoginUtil.getUserId(this)) {
            SkipPersonalCenterUtil.startPersonalCore(this, userId, "USER");
        }
    }

    /**
     * 返回上一个界面
     */
    @JavascriptInterface
    public void windowClose() {
        finish();
    }

    /**
     * 点击查看大图
     * @param img
     */
    @JavascriptInterface
    public void viewLargePic(String img) {
        Intent mIntent = new Intent(this, ImageDetailsActivity.class);
        mIntent.putExtra("imageUrl", img);
        startActivity(mIntent);
    }

    /**
     * 打开评论页面
     * @param url
     * @param callback
     */
    @JavascriptInterface
    public void postComment(String url, String callback) {
        this.callback = callback;
        Intent intent = new Intent(this, VoteCommentActivity.class);
        intent.putExtra("vote_url", url);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(callback))
                        webView.loadUrl("javascript:"+ callback);
                }
            }, 1);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (TextUtils.isEmpty(backKey)) {
                finish();
            } else {
                webView.loadUrl("javascript:" + backKey);
            }
        }
        return false;
    }
}
