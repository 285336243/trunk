package com.mzs.guaji.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.Drawlot;
import com.mzs.guaji.entity.ShakeResult;
import com.mzs.guaji.entity.ShakeSurplusTime;
import com.mzs.guaji.entity.SkipBrowser;
import com.mzs.guaji.entity.SkipWebView;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.ToastUtil;
import com.mzs.guaji.view.RotateAnimation;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by wlanjie on 14-2-11.
 */
public class ShakeActivity extends DefaultThirdPartyShareActivity {

    private Context context = ShakeActivity.this;
    private SensorManager sensorManager;
    private Vibrator vibrator;
    private Dialog mDialog;
    private int width;
    private int height;
    private static final int DIALOG_ROTATION = 1;
    private static final int SEND_TIME = 400;
    private long firstTime = 0;
    private TextView mSurplusTimeText;
    private RelativeLayout mSponsorLayout;
    private RelativeLayout mAwardLayout;
    private RelativeLayout mProgressLayout;
    private RelativeLayout mDialogRootLayout;
    private ImageView mShakeAwardImage;
    private ImageButton mCloseDialogButton;
    private LinearLayout mShakeShareContentLayout;
    private TextView mShakeAwardInfoText;
    private TextView mAwardTipText;
    private ImageView mSponsorImage;
    private TextView mClauseText;
    private boolean isSupperLimit;
    private RelativeLayout mShareSinaLayout;
    private RelativeLayout mShareTencentLayout;
    private RelativeLayout mShareWeixinLayout;
    private static final Gson mGson  = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().enableComplexMapKeySerialization()
            .serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS ")
            .setPrettyPrinting().setVersion(1.0).create();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.shake_layout);
        TextView mBackText = (TextView) findViewById(R.id.shake_back);
        mBackText.setOnClickListener(mBackClickListener);
        TextView mAwardRecord = (TextView) findViewById(R.id.shake_award_record);
        mSurplusTimeText = (TextView) findViewById(R.id.shake_first_surplus_time);
        mAwardRecord.setOnClickListener(mAwardRecordClickListener);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        DisplayMetrics mMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        width = mMetrics.widthPixels;
        height = mMetrics.heightPixels;
        execSurplusTime();
    }

    View.OnClickListener mAwardRecordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, WebViewActivity.class);
            mIntent.putExtra("url", DOMAIN + "drawlot/result.html");
            mIntent.putExtra("title", "中奖记录");
            startActivity(mIntent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(mDialog != null && !mDialog.isShowing()) {
                if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float[] values = event.values;
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];
                    int medumValue = 15;
                    if (Math.abs(x) > 17 || Math.abs(y) > 17 || Math.abs(z) > medumValue) {
                        long lastTime = System.currentTimeMillis();
                        if(lastTime - firstTime > 500) {
                            firstTime = lastTime;
                            vibrator.vibrate(200);
                            showDialog();
                        }
                    }
                }
             }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void showDialog() {
        mDialog.setContentView(R.layout.shake_sponsor_dialog);
        mCloseDialogButton = (ImageButton) mDialog.findViewById(R.id.shake_dialog_close);
        closeBodyClick(false);
        mDialogRootLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_dialog_root_layout);
        mSponsorLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_dialog_sponsor_layout);
        mAwardLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_dialog_award_layout);
        mProgressLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_progress_layout);
        mShakeAwardInfoText = (TextView) mDialog.findViewById(R.id.shake_award_info);
        mAwardTipText = (TextView) mDialog.findViewById(R.id.shake_award_tip_text);
        mClauseText = (TextView) mDialog.findViewById(R.id.shake_award_clause);
        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(width, width * 5 / 4);
        mShakeAwardImage = (ImageView) mDialog.findViewById(R.id.shake_award_image);
        mShakeShareContentLayout = (LinearLayout) mDialog.findViewById(R.id.shake_share_content);
        mShareSinaLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_share_sina);
        mShareTencentLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_share_tencent);
        mShareWeixinLayout = (RelativeLayout) mDialog.findViewById(R.id.shake_share_weixin);
        mParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mSponsorLayout.setLayoutParams(mParams);
        mAwardLayout.setLayoutParams(mParams);
        mProgressLayout.setLayoutParams(mParams);

        mSponsorImage = (ImageView) mDialog.findViewById(R.id.shake_sponsor_image);
        execDrawlot();

        Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_in_top);
        mDialogRootLayout.startAnimation(mAnimation);
        if(mDialog != null && isSupperLimit && !mDialog.isShowing()) {
            mDialog.show();
        }
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.shake_anim);
                mDialogRootLayout.startAnimation(mAnimation);
                mAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mHandler.sendEmptyMessageDelayed(DIALOG_ROTATION, SEND_TIME);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void execDrawlot() {
        mApi.requestGetData(getDrawlotRequest(), Drawlot.class, new Response.Listener<Drawlot>() {
            @Override
            public void onResponse(final Drawlot response) {
                if(response != null && response.getResponseCode() == 0) {
                    execSurplusTime();
                    mAwardTipText.setVisibility(View.VISIBLE);
                    mShakeShareContentLayout.setVisibility(View.VISIBLE);
                    if(response.getStimulate() != null) {
                        closeBodyClick(false);
                        mShakeShareContentLayout.setVisibility(View.GONE);
                        mShakeAwardInfoText.setText(response.getStimulate().getMsg());
                        mAwardTipText.setText(R.string.sorry);
                        mImageLoader.displayImage(response.getStimulate().getCoverImg(), mSponsorImage, ImageUtils.imageLoader(context, 4));
                        if(response.getStimulate().getImg() != null && !"".equals(response.getStimulate().getImg())) {
                            mImageLoader.displayImage(response.getStimulate().getImg(), mShakeAwardImage, ImageUtils.imageLoader(context, 4));
                        }
                    }
                    if(response.getPrize() != null) {
                        closeBodyClick(true);
                        mClauseText.setVisibility(View.VISIBLE);
                        mAwardTipText.setText(R.string.celebrate_you);
                        mShakeAwardInfoText.setText(response.getPrize().getMsg());
                        mImageLoader.displayImage(response.getPrize().getCoverImg(), mSponsorImage, ImageUtils.imageLoader(context, 4));
                        if(response.getPrize().getImg() != null && !"".equals(response.getPrize().getImg())) {
                            mImageLoader.displayImage(response.getPrize().getImg(), mShakeAwardImage, ImageUtils.imageLoader(context, 4));
                        }
                        if(response.getShareTemplete() != null) {
                            if(response.getShareTemplete().getShareImg() != null && !"".equals(response.getShareTemplete().getShareImg())) {
                                ImageView mImageView = new ImageView(context);
                                mImageLoader.displayImage(response.getShareTemplete().getShareImg(), mImageView, ImageUtils.imageLoader(context, 0));
                                shareThirdParty(response.getShareTemplete().getShareText(), mImageView);
                            }else {
                                shareThirdParty(response.getShareTemplete().getShareText(), null);
                            }
                        }
                    }
                }else {
                    ToastUtil.showCenterToast(context, response.getResponseMessage());
                    closeBodyClick(false);
                }
            }
        }, null);
    }

    private void shareThirdParty(final String shareContent, final ImageView mImageView) {
        mShareSinaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageView != null) {
                    sinaSharePic(mImageView, shareContent);
                }else {
                    sinaShareText(shareContent);
                }
            }
        });
        mShareTencentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageView != null) {
                    tencentSharePic(mImageView, shareContent);
                }else {
                    tencentShareText(shareContent);
                }
            }
        });
        mShareWeixinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareWeiXinText(shareContent);
            }
        });
    }

    private void execSurplusTime() {
        mApi.requestGetData(getSurplusTimeRequest(), ShakeSurplusTime.class, new Response.Listener<ShakeSurplusTime>() {
                    @Override
                    public void onResponse(ShakeSurplusTime response) {
                        if(response.getResponseCode() == 0) {
                            if (sensorManager != null) {
                                sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
                            }
                            isSupperLimit = response.getRetainAccessTimes() == 0 ? false : true;
                            mSurplusTimeText.setText(response.getNotice());
                        }else {
                            mSurplusTimeText.setText(response.getResponseMessage());
                            isSupperLimit = false;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    private void closeBodyClick(final boolean isTranslate) {
        View.OnClickListener mCloseClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationSet mAnimationSet = null;
                if(isTranslate) {
                    Animation mScaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.ZORDER_TOP, 0.5f, Animation.ZORDER_TOP, 0.5f);
                    mScaleAnimation.setDuration(280);
                    mScaleAnimation.setFillAfter(true);

                    Animation mTranslateAnimation = new TranslateAnimation(0.0f, width - 32, 0.0f, -height + 10);// 移动
                    mTranslateAnimation.setDuration(600);
                    mAnimationSet = new AnimationSet(false);
                    mAnimationSet.addAnimation(mScaleAnimation);
//                    mAnimationSet.setFillAfter(true);
                    mAnimationSet.addAnimation(mTranslateAnimation);
                    mDialogRootLayout.startAnimation(mAnimationSet);
                    mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }else {
                    Animation mAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_out_bottom);
                    mDialogRootLayout.startAnimation(mAnimation);
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

            }
        };
        mCloseDialogButton.setOnClickListener(mCloseClickListner);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(DIALOG_ROTATION == msg.what) {
                applyRotation();
            }
        }
    };

    private void applyRotation() {
        View mView = mSponsorLayout.getVisibility() == View.VISIBLE ? mSponsorLayout : mAwardLayout;
        if(mSponsorLayout.getVisibility() == View.VISIBLE) {
            RotateAnimation mAnimation = new RotateAnimation(true, true);
            mAnimation.setDuration(500);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    View nextView = null;
                    if(mSponsorLayout.getVisibility() == View.VISIBLE) {
                        nextView = mAwardLayout;
//                        nextView = mProgressLayout;
                        mSponsorLayout.setVisibility(View.INVISIBLE);
                    } else {
                        nextView = mSponsorLayout;
                        mShakeShareContentLayout.setVisibility(View.INVISIBLE);
                        mAwardTipText.setVisibility(View.INVISIBLE);
                        mAwardLayout.setVisibility(View.INVISIBLE);
                    }
                    nextView.setVisibility(View.VISIBLE);
                    RotateAnimation mAnimation = new RotateAnimation(false, true);
                    nextView.startAnimation(mAnimation);
                    mAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mCloseDialogButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mView.startAnimation(mAnimation);
        }else {
            RotateAnimation mAnimation = new RotateAnimation(true, false);
            mAnimation.setDuration(500);
            mAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    View nextView = null;
                    if(mSponsorLayout.getVisibility() == View.VISIBLE) {
                        nextView = mAwardLayout;
//                        nextView = mProgressLayout;
                        mSponsorLayout.setVisibility(View.INVISIBLE);
                    } else {
                        nextView = mSponsorLayout;
                        mAwardLayout.setVisibility(View.INVISIBLE);
                    }
                    nextView.setVisibility(View.VISIBLE);
                    nextView.startAnimation(new RotateAnimation(false, false));
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mView.startAnimation(mAnimation);
        }
    }

    private String getSurplusTimeRequest() {
        return DOMAIN + "drawlot/retainAccessTimes.json?platform=ANDROID";
    }

    private String getDrawlotRequest() {
        return DOMAIN + "drawlot/drawlot.json?platform=ANDROID";
    }

}
