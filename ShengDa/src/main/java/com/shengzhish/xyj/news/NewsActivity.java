package com.shengzhish.xyj.news;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.SharePage;
import com.shengzhish.xyj.activity.entity.ShareTemplete;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.ThirdPartyShareActivity;
import com.shengzhish.xyj.darrage.controller.DrawHandler;
import com.shengzhish.xyj.darrage.danmaku.model.BaseDanmaku;
import com.shengzhish.xyj.darrage.danmaku.model.DanmakuTimer;
import com.shengzhish.xyj.darrage.danmaku.model.android.DanmakuGlobalConfig;
import com.shengzhish.xyj.darrage.danmaku.parser.DanmakuFactory;
import com.shengzhish.xyj.darrage.parser.PostDanmakuParser;
import com.shengzhish.xyj.darrage.parser.PostDataSource;
import com.shengzhish.xyj.darrage.ui.widget.DanmakuSurfaceView;
import com.shengzhish.xyj.gallery.ColorDialog;
import com.shengzhish.xyj.gallery.entity.Post;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.http.HttpboLis;
import com.shengzhish.xyj.news.entity.NewsPost;
import com.shengzhish.xyj.news.entity.ShareResponse;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.BitmapUtil;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;
import com.shengzhish.xyj.util.NetWorkConnect;
import com.shengzhish.xyj.util.ToastUtil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

/**
 * Created by wlanjie on 14-6-3.
 */
@ContentView(R.layout.news_layout)
public class NewsActivity extends ThirdPartyShareActivity {

    private final static String NEWS_POST_URI = "news/post.json";

    @Inject
    private Activity activity;

    @InjectView(R.id.web_view)
    private WebView webView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectExtra(IConstant.NEWS_ID)
    private String newsId;

    @InjectView(R.id.news_surface)
    private DanmakuSurfaceView surfaceView;

    @InjectView(R.id.news_background_view)
    private View backgroundView;

    @InjectView(R.id.news_back)
    private View backView;

    @InjectView(R.id.news_say)
    private View sayView;

    @InjectView(R.id.news_see)
    private View seeView;

    @InjectView(R.id.front_view)
    private View frontView;

    @InjectView(R.id.news_share)
    private View shareView;

    @InjectView(R.id.news_share_layout)
    private View shareLayout;

    @InjectView(R.id.news_share_sina)
    private View shareSina;

    @InjectView(R.id.news_share_weixin)
    private View shareWeiXin;

    @InjectView(R.id.news_share_tencent)
    private View shareTencent;

    public static int NEWS_POST_REQUEST_CODE = 1777;

    @Inject
    private NewsServices services;

    private String cusorId = "0";

    private String webUrl;

    private PostDanmakuParser parser;
    private String id;
    private ShareTemplete shareTemplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webUrl = getStringExtra(IConstant.NEWS_EXTRA);

        webView.setWebViewClient(new WebViewClient() {
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
        webView.loadUrl(webUrl);
        setupDarrage();

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        seeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (surfaceView.isShown()) {
                    hide(backgroundView);
                    hide(surfaceView);
                    surfaceView.hide();
                } else {
                    show(backgroundView);
                    show(surfaceView);
                    surfaceView.show();
                }
            }
        });

        sayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginUtilSh.isLogin(activity)) {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                } else {
                    ColorDialog dialog = new ColorDialog(activity, NEWS_POST_URI);
                    dialog.setId(newsId);
                    View view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
                    dialog.setContentView(view);
                    if (!dialog.isShowing())
                        dialog.show();
                }
            }
        });
        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shareLayout.getVisibility() == View.GONE) {
                    show(backgroundView);
                    show(shareLayout);
                    hide(surfaceView);
                    surfaceView.hide();
                    AnimationUtil.horizontalAnimation(shareLayout, true);
                } else {
                    hide(backgroundView);
                    hide(shareLayout);
                    AnimationUtil.horizontalAnimation(shareLayout, false);
                }
            }
        });
        shareNetworkRequest();

    }

    private void shareNetworkRequest() {
        HttpboLis.getInstance().getHttp(this, ShareResponse.class, String.format("news/share.json?id=%s", newsId), new HttpboLis.OnCompleteListener<ShareResponse>() {
            @Override
            public void onComplete(ShareResponse response) {
                shareTemplete = response.getShareTemplete();
                if (shareTemplete.getShareImg() != null)
                    shareListener();
            }
        });
    }

    private void shareListener() {
        shareSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().displayImage(shareTemplete.getShareImg(), new ImageView(activity), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        sinaSharePic(loadedImage, shareTemplete.getShareText());
                    }
                });
            }
        });

        shareTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoboAsyncTask<Void> task = new RoboAsyncTask<Void>(activity) {
                    @Override
                    public Void call() throws Exception {
                        File file = ImageLoader.getInstance().loadImageFileSync(shareTemplete.getShareImg());
                        qqSharePic(file, shareTemplete.getShareText());
                        return null;
                    }
                };
                task.execute();
            }
        });
        shareWeiXin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharePage sharePage = shareTemplete.getSharePage();
                ImageLoader.getInstance().displayImage(sharePage.getIcon(), new ImageView(activity), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Bitmap bitmap = BitmapUtil.yaImage(loadedImage, 100);
                        shareWeiXinWebPge(bitmap, sharePage.getUrl(), sharePage.getTitle(), sharePage.getDesc());
                        hide(shareLayout);
                        AnimationUtil.horizontalAnimation(shareLayout, false);
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void openNews(String id) {
        startActivity(new Intents(this, NewsActivity.class).add(IConstant.NEWS_ID, id).add(IConstant.NEWS_EXTRA, String.format(IConstant.NEWS_URL, id)).toIntent());


    }

    private void setupDarrage() {
        if (!NetWorkConnect.isConnect(this)) {
            Toast.makeText(this, "网络不给力", Toast.LENGTH_SHORT).show();
            return;
        }
        DanmakuGlobalConfig.DEFAULT.setDanmakuStyle(DanmakuGlobalConfig.DANMAKU_STYLE_STROKEN, 3);
        if (surfaceView != null) {
            surfaceView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    surfaceView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {

                }
            });
            parser = createParser();

            surfaceView.prepare(parser);
            surfaceView.setZOrderOnTop(true);
            surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

            final GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (backgroundView.getVisibility() == View.GONE) {
                        show(backgroundView);
                        show(surfaceView);
                        surfaceView.show();
                    } else {
                        hide(backgroundView);
                        hide(surfaceView);
                        surfaceView.hide();
                    }
                    return false;
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    if (shareLayout.getVisibility() == View.VISIBLE) {
                        hide(backgroundView);
                        hide(shareLayout);
                        AnimationUtil.horizontalAnimation(shareLayout, false);
                    }
                    return false;
                }
            });

            webView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
            surfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return gestureDetector.onTouchEvent(event);
                }
            });
            frontView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return webView.dispatchTouchEvent(event);
                }
            });
        }
    }

    private PostDanmakuParser createParser() {
        return new PostDanmakuParser(new PostDataSource() {
            @Override
            public Set<BaseDanmaku> next() {
                Set<BaseDanmaku> set = new HashSet<BaseDanmaku>();
                Request<NewsPost> request = services.createNewsRequest(newsId, cusorId, 20);
                NewsPost newsPost = null;
                if (null != HttpUtils.doRequest(request)) {
                    newsPost = (NewsPost) HttpUtils.doRequest(request).result;
                }
                if (newsPost != null) {
                    cusorId = newsPost.getCusorId();
                    if (newsPost.getResponseCode() == 0) {
                        if (newsPost.getPosts() != null && newsPost.getPosts().size() > 0) {
                            if (getBaseDanmakuParser() == null || getBaseDanmakuParser().getmTimer() == null) {
                                return null;
                            }
                            long startTime = getBaseDanmakuParser().getmTimer().currMillisecond + 150;
                            for (Post post : newsPost.getPosts()) {
                                BaseDanmaku item = DanmakuFactory.createDanmaku(1, getBaseDanmakuParser().getmDispWidth() / (getBaseDanmakuParser().getmDispDensity() - 0.6f));
                                if (item != null) {
                                    item.time = startTime;
                                    item.textSize = 25 * (getBaseDanmakuParser().getmDispDensity() - 0.6f);
                                    if (post.getColor() != null)
                                        item.textColor = Color.argb(0, post.getColor().getR(), post.getColor().getG(), post.getColor().getB());
                                    else
                                        item.textColor = Color.WHITE;
                                    item.index = Integer.valueOf(post.getId()).intValue();
                                    item.setTimer(getBaseDanmakuParser().getmTimer());
                                    DanmakuFactory.fillText(item, post.getMessage());
                                    startTime = startTime + 150;
                                    set.add(item);
                                }
                            }
                        }
                    }
                }
                return set;
            }
        }, 5000, surfaceView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parser != null)
            parser.release();
        surfaceView.release();
    }
}
