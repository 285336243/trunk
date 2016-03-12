package com.socialtv.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.MenuItem;
import com.melot.meshow.room.RoomLauncher;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.ImageDetailsActivity;
import com.socialtv.R;
import com.socialtv.core.Intents;
import com.socialtv.core.ThirdPartyShareActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.feed.OthersFeedActivity;
import com.socialtv.http.HttpUtils;
import com.socialtv.http.SQLiteCookieStore;
import com.socialtv.personcenter.LoginActivity;
import com.socialtv.star.StarActivity;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.MD5Util;

import org.apache.http.cookie.Cookie;

import java.io.IOException;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-9.
 * 所有的WebView页面
 */

@ContentView(R.layout.web_view)
public class WebViewActivity extends ThirdPartyShareActivity {

    @InjectExtra(value = IConstant.URL, optional = true)
    private String url;

    @InjectView(R.id.web_view)
    private WebView webView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    private MediaPlayer mediaPlayer;

    private String callback;

    private String backKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);

        long hideTitle = getLongExtra(IConstant.HIDE_TITLE);
        if (hideTitle == 1) {
            getSupportActionBar().hide();
        }

        String title = getStringExtra(IConstant.TITLE);
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(title);
        } else {
            getSupportActionBar().setTitle("");
        }

        long hideStatus = getLongExtra(IConstant.HIDE_STATUS);
        if (hideStatus == 1) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        WebSettings mWebSettings = webView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        webView.addJavascriptInterface(this, "wala");
        SQLiteCookieStore cookieStore = HttpUtils.getCookieStore();
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this);
        cookieSyncManager.sync();
        CookieManager cookieManager = CookieManager.getInstance();
        List<Cookie> cookies = cookieStore.getCookies();
        for(int i=0; i<cookies.size(); i++) {
            cookieManager.setCookie(url, cookies.get(i).getName() + "="+ cookies.get(i).getValue()+"; domain="+cookies.get(i).getDomain());
        }
        CookieSyncManager.getInstance().sync();
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    hide(progressBar);
                }
            }
        });

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                hide(progressBar);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                hide(progressBar);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url!=null && url.startsWith("wala://")){
                    startWalaSchema(url);
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }

    private void startWalaSchema(String url){
        Uri uri = Uri.parse(url);
        if (uri != null) {
            final String host = uri.getHost();
            final String link = uri.getQueryParameter("link");
            final String  imgUrl = uri.getQueryParameter("imgUrl");
            final String  title = uri.getQueryParameter("title");
            final String desc = uri.getQueryParameter("desc");

            final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon);
            if ("weixin.shareToTimeline".equals(host)) {
                if (TextUtils.isEmpty(imgUrl)) {
                    shareWeiXinWebPage(link, title, desc, bitmap);
                } else {
                    ImageLoader.getInstance().loadImage(imgUrl, new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            shareWeiXinWebPage(link, title, desc, bitmap);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            shareWeiXinWebPage(link, title, desc, loadedImage);
                        }
                    });
                }
            } else {
                if (TextUtils.isEmpty(imgUrl)) {
                    shareWeiXinFriendWebPage(link, title, desc, bitmap);
                } else {
                    ImageLoader.getInstance().loadImage(imgUrl, new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            shareWeiXinFriendWebPage(link, title, desc, bitmap);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            shareWeiXinFriendWebPage(link, title, desc, loadedImage);
                        }
                    });
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
                    ToastUtils.show(WebViewActivity.this, "播放出错了");
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
        if (userId != Long.parseLong(LoginUtil.getUserId(this))) {
            startActivity(new Intents(this, OthersFeedActivity.class).add(IConstant.USER_ID, userId + "").toIntent());
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
     * KK唱响
     * @param roomid
     * @param userid
     * @param nickname
     * @param sessionid
     * @param gender
     */
    @JavascriptInterface
    public void kkroom(final String roomid, final String userid, final String nickname, final String sessionid, final String gender) {
        if (LoginUtil.isLogin(this)) {
            Intent intent = new Intent(this, RoomLauncher.class);
            Bundle bundle = new Bundle();
            bundle.putString("userid", LoginUtil.getUserId(this));
            bundle.putString("usernickname", LoginUtil.getUserNickName(this));
            bundle.putString("roomid", roomid);
            bundle.putString("usersessionid", MD5Util.getMD5Str(LoginUtil.getUserId(this)));
//                bundle.putString("usergender", LoginUtil.get);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            startActivity(new Intents(this, LoginActivity.class).toIntent());
        }
    }

    /**
     * 登录
     */
    @JavascriptInterface
    public void login() {
        startActivity(new Intents(this, LoginActivity.class).toIntent());
    }

    /**
     * 明星
     * @param id
     */
    @JavascriptInterface
    public void enterCelebrity(final String id) {
        if (!TextUtils.isEmpty(id)) {
            startActivity(new Intents(this, StarActivity.class).add(IConstant.STAR_ID, id).toIntent());
        }
    }

    @JavascriptInterface
    public void enterUserNews(final String id) {
        if (!TextUtils.isEmpty(id)) {
            startActivity(new Intents(this, NewsWebViewActivity.class).add(IConstant.ID, id).toIntent());
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
