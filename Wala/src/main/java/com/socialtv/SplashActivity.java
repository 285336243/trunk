package com.socialtv;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Request;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.home.HomeActivity;
import com.socialtv.http.GsonRequest;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;

/**
 * Created by wlanjie on 14-7-10.
 *
 * Splash页面
 */
public class SplashActivity extends DialogFragmentActivity {

    private final static String URL = "system/loading.json?cid=%s";

    private final static int WHAT = 0;

    private DisplayImageOptions options;

    private String marketHouseId;

    /**
     * Loading图下载成功的时候接收的Handler消息,判断跳转到引导页还是主页
     */
    private final Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final SharedPreferences preferences = getSharedPreferences(IConstant.GUIDE, MODE_PRIVATE);
            long guide = preferences.getLong(IConstant.GUIDE, 0);
            if (guide == 0) {
                startActivity(new Intents(SplashActivity.this, GuideActivity.class).toIntent());
            } else {
                startActivity(new Intents(SplashActivity.this, HomeActivity.class).toIntent());
            }
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundResource(R.drawable.launch);
        setContentView(imageView);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.launch)
                .showImageForEmptyUri(R.drawable.launch)
                .showImageOnFail(R.drawable.launch)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
                        // .displayer(new FadeInBitmapDisplayer(300))
                .build();


        try {
            //获取渠道号
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Object object = applicationInfo.metaData.get("WALA_CHANNEL");
            if (object != null) {
                marketHouseId = object.toString();
                if (marketHouseId.length() == 1) {
                    marketHouseId = "0" + marketHouseId;
                }
                DistributeAPI.activateDevice(this, marketHouseId, getPackageName(), versionName);

                new AbstractRoboAsyncTask<Splash>(this){
                    /**
                     * Execute task with an authenticated account
                     *
                     * @param data
                     * @return result
                     * @throws Exception
                     */
                    @Override
                    protected Splash run(Object data) throws Exception {
                        GsonRequest<Splash> request = new GsonRequest<Splash>(Request.Method.GET, String.format(URL, marketHouseId));
                        request.setClazz(Splash.class);
                        return (Splash) HttpUtils.doRequest(request).result;
                    }

                    @Override
                    protected void onSuccess(Splash splash) throws Exception {
                        super.onSuccess(splash);
                        if (splash != null) {
                            if (splash.getResponseCode() == 0) {
                                ImageLoader.getInstance().loadImage(splash.getLoading(), options, new SimpleImageLoadingListener(){

                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                                        super.onLoadingComplete(imageUri, view, loadedImage);
                                        imageView.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageView.setImageBitmap(loadedImage);
                                            }
                                        }, 1500);
                                    }
                                });
                            }
                        }
                    }
                }.execute();
                mHander.sendEmptyMessageDelayed(WHAT, 3000);
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHander.removeMessages(WHAT);
    }
}
