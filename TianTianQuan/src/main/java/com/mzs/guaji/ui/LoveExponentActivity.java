package com.mzs.guaji.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.LoveExponent;
import com.mzs.guaji.share.DefaultThirdPartyShareActivity;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.view.FlakeView;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wlanjie on 14-2-18.
 */
public class LoveExponentActivity extends DefaultThirdPartyShareActivity {

    private ImageView mManPhotoImage;
    private ImageView mWomanPhotoImage;
    private EditText mManNameText;
    private EditText mWomanNameText;
    private RelativeLayout mStartLayout;
    protected static final int CUT_PHOTO_REQUEST_CODE = 2;
    protected static final int SET_PHOTO_REQUEST_CODE = 1;
    protected Context context = LoveExponentActivity.this;
    protected File saveFile;
    protected File mediaStorageDir;
    protected ImageView mAvatarImage;
    protected Bitmap mManBitmap;
    protected Bitmap mWomanBitmap;
    protected Bitmap bitmap;
    protected Dialog mDialog;
    private int manAndWoman = 1;
    private LinearLayout mLoveExponentLayout;
    private RelativeLayout mComputeLoveExponentLayout;
    private ImageView computeManPhotoImage;
    private ImageView computeWomanPhotoImage;
    private TextView mLoveExponentTipText;
    private TextView mComputeLoveExponentManName;
    private TextView mComputeLoveExponentWomanName;
    private TextView mComputeLoveScore;
    private ImageView mProgressImage;
    private ClipDrawable clipDrawable;
    private FlakeView flakeView;
    private FrameLayout mFrameLayout;
    private LinearLayout mShareLayout;
    private TextView mResultText;
    private RelativeLayout mShareSinaLayout;
    private RelativeLayout mShareTencentLayout;
    private RelativeLayout mShareWeiXinLayout;
    private TextView mAfreshOnceText;
    private Bitmap resultBitmap;
    private LoveExponent loveExponent;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what == 0) {
                clipDrawable.setLevel(clipDrawable.getLevel()+50);
                long score = Math.round((Math.random()*100))%100;
                mComputeLoveScore.setText(score + "");
                if (clipDrawable.getLevel() == 10000) {
                    mHandler.removeMessages(0);
                    showShareView();
                } else {
                    mHandler.sendEmptyMessageDelayed(0, 10);
                }
            } else if (what == 1) {
                resultBitmap = Bitmap.createBitmap(mComputeLoveExponentLayout.getWidth(), mComputeLoveExponentLayout.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(resultBitmap);
                mComputeLoveExponentLayout.layout(0, 0, mComputeLoveExponentLayout.getWidth(), mComputeLoveExponentLayout.getHeight());
                mComputeLoveExponentLayout.draw(canvas);
                mShareLayout.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.love_exponent_layout);
        ImageButton mBackButton = (ImageButton) findViewById(R.id.love_exponent_back);
        mBackButton.setOnClickListener(mBackClickListener);
        mAfreshOnceText = (TextView) findViewById(R.id.love_exponent_afresh_once_text);
        mManPhotoImage = (ImageView) findViewById(R.id.love_man_photo);
        mManPhotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manAndWoman = 1;
                showSetAvatarDialog();
            }
        });
        mWomanPhotoImage = (ImageView) findViewById(R.id.love_woman_photo);
        mWomanPhotoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manAndWoman = 2;
                showSetAvatarDialog();
            }
        });
        mManNameText = (EditText) findViewById(R.id.love_man_name);
        mWomanNameText = (EditText) findViewById(R.id.love_woman_name);
        mStartLayout = (RelativeLayout) findViewById(R.id.love_exponent_start);
        mStartLayout.setOnClickListener(mStartClickListener);
        mLoveExponentLayout = (LinearLayout) findViewById(R.id.love_exponent);
        mComputeLoveExponentLayout = (RelativeLayout) findViewById(R.id.compute_love_exonent);
        computeManPhotoImage = (ImageView) findViewById(R.id.compute_man_photo);
        computeWomanPhotoImage = (ImageView) findViewById(R.id.compute_woman_photo);
        mLoveExponentTipText = (TextView) findViewById(R.id.love_exponent_tip_text);
        mComputeLoveExponentManName = (TextView) findViewById(R.id.compute_love_exonent_man_name);
        mComputeLoveExponentWomanName = (TextView) findViewById(R.id.compute_love_exonent_woman_name);
        mComputeLoveScore = (TextView) findViewById(R.id.compute_love_exonent_scroe);
        mProgressImage = (ImageView) findViewById(R.id.compute_love_exonent_progress);
        mFrameLayout = (FrameLayout) findViewById(R.id.compute_love_exonent_anim_layout);
        mShareLayout = (LinearLayout) findViewById(R.id.compute_love_exonent_share_layout);
        mResultText = (TextView) findViewById(R.id.compute_love_exonent_result);
        mShareSinaLayout = (RelativeLayout) findViewById(R.id.compute_love_exonent_share_sina_layout);
        mShareTencentLayout = (RelativeLayout) findViewById(R.id.compute_love_exonent_share_tencent_layout);
        mShareWeiXinLayout = (RelativeLayout) findViewById(R.id.compute_love_exonent_share_weixin_layout);
        clipDrawable = (ClipDrawable) mProgressImage.getDrawable();
        flakeView = new FlakeView(this);

        mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
    }

    View.OnClickListener mStartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mManBitmap == null) {
                mLoveExponentTipText.setText("说出爱的人是谁,才能给你答案");
                return;
            }
            if (mWomanBitmap == null) {
                mLoveExponentTipText.setText("说出爱的人是谁,才能给你答案");
                return;
            }
            String manName = mManNameText.getText().toString();
            if (manName == null || "".equals(manName)) {
                mLoveExponentTipText.setText("说出爱的人是谁,才能给你答案");
                return;
            }

            String womanName = mWomanNameText.getText().toString();
            if (womanName == null || "".equals(womanName)) {
                mLoveExponentTipText.setText("说出爱的人是谁,才能给你答案");
                return;
            }
            mComputeLoveExponentManName.setText(manName);
            mComputeLoveExponentWomanName.setText(womanName);
            mLoveExponentLayout.setVisibility(View.GONE);
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("malename", manName);
            headers.put("femalename", womanName);
            mApi.requestPostData(getLoveExponentRequest(), LoveExponent.class, headers, new Response.Listener<LoveExponent>() {
                @Override
                public void onResponse(LoveExponent response) {
                    if (response != null) {
                        if (response.getResponseCode() == 0) {
                            loveExponent = response;
                        }
                    }
                }
            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loveExponent = null;
                        }
                    });
            mFrameLayout.addView(flakeView);
            clipDrawable.setLevel(0);
            mHandler.sendEmptyMessageDelayed(0, 10);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        flakeView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        flakeView.resume();
    }

    private void showShareView() {
        if (loveExponent != null) {
            mComputeLoveScore.setText(loveExponent.getIndex()+"");
            mResultText.setText(loveExponent.getPredictition());
        } else {
            mComputeLoveScore.setText("?");
            mResultText.setText("月老打盹去了 过会再来吧");
        }

        mAfreshOnceText.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(1, 500);
        mShareSinaLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultBitmap != null) {
                    MobclickAgent.onEvent(LoveExponentActivity.this, "game_lovetest_share_weibo");
                    sinaSharePic(resultBitmap, "我参与了#天天圈爱情测试#，你也来试试吧~ http://www.ttq2014.com");
                }
            }
        });
        mShareTencentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultBitmap != null) {
                    MobclickAgent.onEvent(LoveExponentActivity.this, "game_lovetest_share_qqweibo");
                    tencentSharePic(resultBitmap, "我参与了#天天圈爱情测试#，你也来试试吧~ http://www.ttq2014.com");
                }
            }
        });
        mShareWeiXinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (resultBitmap != null) {
                    MobclickAgent.onEvent(LoveExponentActivity.this, "game_lovetest_share_wechat");
                    shareWeiXinPic(resultBitmap);
                }
            }
        });
        mAfreshOnceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFrameLayout.removeAllViews();
                mManNameText.setText("");
                mWomanNameText.setText("");
                mManPhotoImage.setImageResource(R.drawable.transparent);
                mWomanPhotoImage.setImageResource(R.drawable.transparent);
                mShareLayout.setVisibility(View.GONE);
                mResultText.setText(R.string.bein_compute_love_exonent);
                mLoveExponentLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    protected void showSetAvatarDialog() {
        mDialog.setContentView(R.layout.setting_photo_pop);
        Button mSettingPhotoCameraButton = (Button) mDialog.findViewById(R.id.setting_photo_camera);
        mSettingPhotoCameraButton.setOnClickListener(mSettingPhotoCameraListener);
        Button mSelectLocalButton = (Button) mDialog.findViewById(R.id.setting_photo_local);
        mSelectLocalButton.setOnClickListener(mSelectLocalListener);
        Button mCancelButton = (Button) mDialog.findViewById(R.id.setting_photo_cancel);
        mCancelButton.setOnClickListener(mCancelPopupWindowListener);
        if(!mDialog.isShowing()) {
            mDialog.show();
        }
    }
    /**
     * popupwindow 选择本地图片按钮点击事件
     */
    View.OnClickListener mSelectLocalListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
        }
    };

    /**
     * popupwindow 拍照按钮点击事件
     */
    View.OnClickListener mSettingPhotoCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            saveFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
            startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
        }
    };

    /**
     * popupwindow 取消按钮点击事件
     */
    View.OnClickListener mCancelPopupWindowListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            cancelDialog();
        }
    };

    private void cancelDialog() {
        if(mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FileOutputStream outputStream = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CUT_PHOTO_REQUEST_CODE) {
                if (data != null) {
                    if (manAndWoman == 1) {
                        mManBitmap = (Bitmap) data.getExtras().get("data");
                        bitmap = mManBitmap;
                    } else {
                        mWomanBitmap = (Bitmap) data.getExtras().get("data");
                        bitmap = mWomanBitmap;
                    }

                    cancelDialog();
                    try {
                        saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis() + ".jpg");
                        outputStream = new FileOutputStream(saveFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (manAndWoman == 1) {
                        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mManPhotoImage, ImageUtils.imageLoader(context, 1000));
                        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), computeManPhotoImage, ImageUtils.imageLoader(context, 1000));
                    } else {
                        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mWomanPhotoImage, ImageUtils.imageLoader(context, 1000));
                        mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), computeWomanPhotoImage, ImageUtils.imageLoader(context, 1000));
                    }

                }
            } else {
                if (data != null) {
                    cropImageUri(data.getData());
                } else {
                    if (saveFile != null) {
                        cropImageUri(Uri.fromFile(saveFile));
                    }
                }
            }
        }
    }
    /**
     * 调用系统裁剪头像图片
     *
     */
    private void cropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 180);
        intent.putExtra("outputY", 180);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            mAvatarImage = null;
        }
        if (mWomanBitmap != null && !mWomanBitmap.isRecycled()) {
            mWomanBitmap.recycle();
            mWomanPhotoImage = null;
        }
        if ((mManBitmap != null && !mManBitmap.isRecycled())) {
            mManBitmap.recycle();
            mManPhotoImage = null;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GuaJi";
        String[] fileName = new String[]{"photo"};
        StorageUtil.deleteAllFiles(path, fileName);

    }

    private String getLoveExponentRequest() {
        return DOMAIN + "indexMeasure/affection_index_predict.json";
    }
}