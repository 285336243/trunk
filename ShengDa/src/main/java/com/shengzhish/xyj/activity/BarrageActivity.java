package com.shengzhish.xyj.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.ActivityDetails;
import com.shengzhish.xyj.activity.entity.SharePage;
import com.shengzhish.xyj.activity.entity.ShareTemplete;
import com.shengzhish.xyj.core.Intents;
import com.shengzhish.xyj.core.ThirdPartyShareActivity;
import com.shengzhish.xyj.darrage.controller.DrawHandler;
import com.shengzhish.xyj.darrage.danmaku.model.BaseDanmaku;
import com.shengzhish.xyj.darrage.danmaku.model.DanmakuTimer;
import com.shengzhish.xyj.darrage.danmaku.model.android.DanmakuGlobalConfig;
import com.shengzhish.xyj.darrage.danmaku.model.android.Danmakus;
import com.shengzhish.xyj.darrage.danmaku.parser.DanmakuFactory;
import com.shengzhish.xyj.darrage.parser.PostDanmakuParser;
import com.shengzhish.xyj.darrage.parser.PostDataSource;
import com.shengzhish.xyj.darrage.ui.widget.DanmakuSurfaceView;
import com.shengzhish.xyj.gallery.ColorDialog;
import com.shengzhish.xyj.gallery.entity.GalleryPost;
import com.shengzhish.xyj.gallery.entity.Pic;
import com.shengzhish.xyj.gallery.entity.Post;
import com.shengzhish.xyj.http.GsonRequest;
import com.shengzhish.xyj.http.HttpUtils;
import com.shengzhish.xyj.persionalcore.LoginActivity;
import com.shengzhish.xyj.util.AnimationUtil;
import com.shengzhish.xyj.util.BitmapUtil;
import com.shengzhish.xyj.util.IConstant;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.util.LoginUtilSh;
import com.shengzhish.xyj.util.NetWorkConnect;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import roboguice.util.RoboAsyncTask;

/**
 * Created by wlanjie on 14-6-12.
 */
@ContentView(R.layout.activity_barrage)
public class BarrageActivity extends ThirdPartyShareActivity {

    private final static String URL = "activity/post.json?id=%s&cusorId=%s&cnt=%s";

    private final static String POST_URI = "activity/post.json";

    private final static int REQUEST_COUNT = 20;

    @Inject
    private Activity activity;

    @InjectView(R.id.mask_viewpager)
    private ViewPager maskViewPager;

    @InjectView(R.id.activity_barrage_background)
    private ImageView backgroundImage;

    @InjectView(R.id.gallery_background_view)
    private View backgroundView;

    @InjectView(R.id.activity_barrage_surface)
    private DanmakuSurfaceView surfaceView;

    @InjectView(R.id.activity_barrage_back)
    private View backView;

    @InjectView(R.id.activity_barrage_say)
    private View sayView;

    @InjectView(R.id.activity_barrage_see)
    private View seeView;

    @InjectView(R.id.activity_share)
    private View shareView;

    @InjectView(R.id.share_layout)
    private View shareLayout;

    @InjectView(R.id.share_sina)
    private View shareSina;

    @InjectView(R.id.share_weixin)
    private View shareWeiXin;

    @InjectView(R.id.share_tencent)
    private View shareTencent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null) {
                ActivityDetails activityDetails = (ActivityDetails) msg.obj;
                if (activityDetails != null) {
                    ImageLoader.getInstance().displayImage(activityDetails.getActivity().getImg(), backgroundImage, ImageUtils.imageLoader(activity, 0));
                }
            }
        }
    };

    private String cusorId = "0";

    private String id;

    private PostDanmakuParser parser;
    private ShareTemplete shareTemplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        maskViewPager.setAdapter(new MyPagerAdapter());
        id = getStringExtra(IConstant.BILI_id);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (NetWorkConnect.isConnect(this)) {
            setupDarrage();
            seeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (surfaceView.getVisibility() == View.GONE) {
                        show(surfaceView);
                        show(backgroundView);
                        surfaceView.show();
                    } else {
                        hide(surfaceView);
                        hide(backgroundView);
                        surfaceView.hide();
                    }
                }
            });

            sayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginUtilSh.isLogin(activity)) {
                        final ColorDialog colorDialog = new ColorDialog(activity, POST_URI);
                        colorDialog.setId(id);
                        View view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
                        colorDialog.setContentView(view);
                        if (!colorDialog.isShowing())
                            colorDialog.show();
                    } else {
                        startActivity(new Intents(activity, LoginActivity.class).toIntent());
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

            RoboAsyncTask<Void> task = new RoboAsyncTask<Void>(activity, handler) {
                @Override
                public Void call() throws Exception {
                    GsonRequest<ActivityDetails> request = new GsonRequest<ActivityDetails>(Request.Method.GET, String.format("activity/detail.json?id=%s", id));
                    request.setClazz(ActivityDetails.class);
                    ActivityDetails activityDetails = (ActivityDetails) HttpUtils.doRequest(request).result;
                    Message msg = new Message();
                    shareTemplete = activityDetails.getActivity().getShareTemplete();
                    if (shareTemplete.getShareImg() != null) {
                        BarrageActivity.this.shareListener();
                    }
                    msg.obj = activityDetails;
                    handler.sendMessage(msg);
                    return null;
                }
            };
            task.execute();
        } else {
            Toast.makeText(this, "网络不给力", Toast.LENGTH_SHORT).show();
        }
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

    private void setupDarrage() {
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
                    if (surfaceView.getVisibility() == View.GONE) {
                        show(surfaceView);
                        show(backgroundView);
                        surfaceView.show();
                    } else {
                        hide(surfaceView);
                        hide(backgroundView);
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

            maskViewPager.setOnTouchListener(new View.OnTouchListener() {
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
        }
    }


    private PostDanmakuParser createParser() {
        return new PostDanmakuParser(new PostDataSource() {
            @Override
            public Set<BaseDanmaku> next() {
                Set<BaseDanmaku> set = new HashSet<BaseDanmaku>();
                GsonRequest<GalleryPost> request = new GsonRequest<GalleryPost>(Request.Method.GET, String.format(URL, id, cusorId, REQUEST_COUNT));
                request.setClazz(GalleryPost.class);
                GalleryPost galleryPost = null;
                if (request != null) {
                    Response<?> req = HttpUtils.doRequest(request);
                    if (req != null) {
                        Object postvalue = req.result;
                        if (postvalue != null) {
                            galleryPost = (GalleryPost) postvalue;
                        }
                    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parser != null)
            parser.release();
        surfaceView.release();
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView4 = new ImageView(BarrageActivity.this);
            ((ViewPager) container).addView(imageView4, 0);
            return imageView4;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
