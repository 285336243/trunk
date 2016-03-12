package com.shengzhish.xyj.gallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.ThirdPartyShareActivity;
import com.shengzhish.xyj.core.ThrowableLoader;
import com.shengzhish.xyj.core.ToastUtils;
import com.shengzhish.xyj.darrage.controller.DrawHandler;
import com.shengzhish.xyj.darrage.danmaku.model.BaseDanmaku;
import com.shengzhish.xyj.darrage.danmaku.model.DanmakuTimer;
import com.shengzhish.xyj.darrage.danmaku.model.android.DanmakuGlobalConfig;
import com.shengzhish.xyj.darrage.danmaku.model.android.Danmakus;
import com.shengzhish.xyj.darrage.danmaku.parser.DanmakuFactory;
import com.shengzhish.xyj.darrage.parser.PostDanmakuParser;
import com.shengzhish.xyj.darrage.parser.PostDataSource;
import com.shengzhish.xyj.darrage.ui.widget.DanmakuSurfaceView;
import com.shengzhish.xyj.gallery.entity.Gallery;
import com.shengzhish.xyj.gallery.entity.GalleryPost;
import com.shengzhish.xyj.gallery.entity.Pic;
import com.shengzhish.xyj.gallery.entity.Post;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.BitmapUtil;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.LoginUtilSh;
import com.shengzhish.xyj.util.NetWorkConnect;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

@ContentView(R.layout.gallery_layout)
public class GalleryActivity extends ThirdPartyShareActivity implements LoaderManager.LoaderCallbacks<Gallery> {

    private final static int LOADER_ID = 0;

    private final static int POST_ID = 1;

    private final static String GALLERY_POST_URI = "gallery/post.json";

    @Inject
    private Activity activity;

    @InjectExtra(IConstant.GALLERY_ID)
    private String galleryId;

    @Inject
    private GalleryServices services;

    @InjectView(R.id.gallery_pager)
    private ViewPager viewPager;

    @InjectView(R.id.sv_darrage)
    private DanmakuSurfaceView surfaceView;

    @InjectView(R.id.gallery_item_pager_number)
    private TextView pagerNumberText;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectView(R.id.gallery_back)
    private View backView;

    @InjectView(R.id.gallery_say)
    private View sayView;

    @InjectView(R.id.gallery_see)
    private View seeView;

    @InjectView(R.id.gallery_share)
    private View shareView;

    @InjectView(R.id.gallery_background_view)
    private View backgroundView;

    @InjectView(R.id.share_layout)
    private View shareLayout;


    @InjectView(R.id.share_sina)
    private View shareSina;

    @InjectView(R.id.share_weixin)
    private View shareWeiXin;

    @InjectView(R.id.share_tencent)
    private View shareTencent;

    private GalleryAdapter adapter;

    private String cusorId = "0";

    private TranslateAnimation showAction, hideAction;

    private PostDanmakuParser parser;
    @InjectView(R.id.front_view)
    private View frontView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        adapter = new GalleryAdapter(this);
        viewPager.setAdapter(adapter);
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

        shareSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pic pic = adapter.getItem(viewPager.getCurrentItem());
                ImageLoader.getInstance().displayImage(pic.getImg(), new ImageView(activity), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        sinaSharePic(loadedImage, pic.getShareText());
                    }
                });
            }
        });

        shareTencent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Pic pic = adapter.getItem(viewPager.getCurrentItem());
                RoboAsyncTask<Void> task = new RoboAsyncTask<Void>(activity) {
                    @Override
                    public Void call() throws Exception {
                        File file = ImageLoader.getInstance().loadImageFileSync(pic.getImg());
                        qqSharePic(file, pic.getShareText());
                        return null;
                    }
                };
                task.execute();
            }
        });
        shareWeiXin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Pic pic = adapter.getItem(viewPager.getCurrentItem());

                ImageLoader.getInstance().displayImage(pic.getImg(), new ImageView(activity), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        Bitmap bitmap = BitmapUtil.yaImage(loadedImage, 100);
                        shareWeiXinWebPge(bitmap, pic.getImg(), pic.getShareText(), pic.getShareText());
                        hide(shareLayout);
                        AnimationUtil.horizontalAnimation(shareLayout, false);
                    }
                });
            }
        });
        setCommentViewClickListener();
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

            viewPager.setOnTouchListener(new View.OnTouchListener() {
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
                    // dispatch the events to the ViewPager, to solve the problem that we can swipe only the middle view.
                    return viewPager.dispatchTouchEvent(event);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parser != null)
            parser.release();
        surfaceView.release();
    }

    /**
     * Take care of calling onBackPressed() for pre-Eclair platforms.
     *
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (shareLayout.getVisibility() == View.VISIBLE) {
                hide(backgroundView);
                hide(shareLayout);
                AnimationUtil.horizontalAnimation(shareLayout, false);
            } else {
                finish();
            }
        }
        return false;
    }

    private Request<Gallery> createGalleryRequest() {
        return services.createGalleryRequest(galleryId);
    }

    @Override
    public Loader<Gallery> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<Gallery>(this, null) {
            @Override
            public Gallery loadData() throws Exception {
                return (Gallery) HttpUtils.doRequest(createGalleryRequest()).result;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Gallery> loader, final Gallery data) {
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (data.getPics() != null && data.getPics().size() > 0) {
                    adapter.setItems(data.getPics());
                    pagerNumberText.setText("1/" + data.getPics().size());
                    viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(int position) {
                            pagerNumberText.setText(position + 1 + "/" + data.getPics().size());
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        hide(progressBar);
    }

    @Override
    public void onLoaderReset(Loader<Gallery> loader) {

    }


    private PostDanmakuParser createParser() {
        return new PostDanmakuParser(new PostDataSource() {
            @Override
            public Set<BaseDanmaku> next() {
                Set<BaseDanmaku> set = new HashSet<BaseDanmaku>();
                Request<GalleryPost> request = services.createGalleryPostRequest(galleryId, cusorId, 20);
                GalleryPost galleryPost = null;
                if (null != HttpUtils.doRequest(request)) {
                    galleryPost = (GalleryPost) HttpUtils.doRequest(request).result;
                }
                Danmakus danmakus = null;
                if (galleryPost != null) {
                    cusorId = galleryPost.getCusorId();
                    if (galleryPost.getResponseCode() == 0) {
                        danmakus = new Danmakus();
                        if (galleryPost.getPosts() != null && galleryPost.getPosts().size() > 0) {
                            if (getBaseDanmakuParser() == null || getBaseDanmakuParser().getmTimer() == null) {
                                return null;
                            }
                            long startTime = getBaseDanmakuParser().getmTimer().currMillisecond + 150;
                            for (Post post : galleryPost.getPosts()) {
                                BaseDanmaku item = DanmakuFactory.createDanmaku(1, getBaseDanmakuParser().getmDispWidth() / (getBaseDanmakuParser().getmDispDensity() - 0.6f));
                                if (item != null) {
                                    item.time = startTime;
                                    item.textSize = 25 * (getBaseDanmakuParser().getmDispDensity() - 0.6f);
                                    if (post.getColor() != null)
                                        item.textColor = Color.argb(0, post.getColor().getR(), post.getColor().getG(), post.getColor().getB());
                                    else
                                        item.textColor = Color.WHITE;
                                    item.index = Integer.valueOf(post.getId());
                                    DanmakuFactory.fillText(item, post.getMessage());
                                    item.setTimer(getBaseDanmakuParser().getmTimer());
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

    private void setCommentViewClickListener() {
        sayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginUtilSh.isLogin(activity)) {
                    final ColorDialog colorDialog = new ColorDialog(activity, GALLERY_POST_URI);
                    colorDialog.setId(galleryId);
                    View view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
                    colorDialog.setContentView(view);
                    if (!colorDialog.isShowing())
                        colorDialog.show();
                } else {
                    startActivity(new Intents(activity, LoginActivity.class).toIntent());
                }
            }
        });
    }
}
