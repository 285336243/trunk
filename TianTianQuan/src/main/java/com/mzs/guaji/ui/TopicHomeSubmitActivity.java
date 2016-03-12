package com.mzs.guaji.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;
import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.engine.GuaJiAPI;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-4-10.
 */

@ContentView(R.layout.topic_home_sumit_layout)
public class TopicHomeSubmitActivity extends RoboSherlockFragmentActivity {

    private static final int SET_CAMERA_REQUEST_CODE = 2;
    private static final int SET_PHOTO_REQUEST_CODE = 1;
    private static final int SET_DELETE_REQUEST_CODE = 3;
    private File saveFile;
    private File mediaStorageDir;
    private Bitmap mBitmap;
    public static final String DOMAIN = "http://social.api.ttq2014.com/";

    @Inject
    private Activity activity;

    @InjectExtra("activity_id")
    private long activityId;

    @InjectView(R.id.topic_home_submit_back)
    private LinearLayout backLayout;

    @InjectView(R.id.topic_home_submit_release)
    private LinearLayout releaseLayout;

    @InjectView(R.id.topic_home_submit_edit_title)
    private EditText titleEdit;

    @InjectView(R.id.topic_home_submit_edit_content)
    private EditText contentEdit;

    @InjectView(R.id.topic_home_submit_camera)
    private RelativeLayout cameraLayout;

    @InjectView(R.id.topic_home_submit_photo)
    private RelativeLayout photoLayout;

    @InjectView(R.id.topic_home_submit_share_image)
    private ImageView shareImage;

    @InjectView(R.id.topic_home_submit_root_layout)
    LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi");
        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GiveUpEditingDialog.showGiveUpEditingDialog(activity, titleEdit.getText().toString());
            }
        });
        photoLayout.setOnClickListener(mPhotoClickListener);
        cameraLayout.setOnClickListener(mCameraClickListener);
        releaseLayout.setOnClickListener(mSubmitLayoutClickListener);
        shareImage.setOnClickListener(mSubmitImageClickListener);
    }

    /**
     * 发布按钮点击事件
     */
    View.OnClickListener mSubmitLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            String mTitleText = titleEdit.getText().toString();
            String mContentText = contentEdit.getText().toString();
            if(TextUtils.isEmpty(mTitleText)) {
                ToastUtil.showToast(activity, R.string.submit_topic_title_tip);
                return;
            }
            if(TextUtils.isEmpty(mContentText)) {
                ToastUtil.showToast(activity, R.string.submit_topic_content_tip);
                return;
            }
            TipsUtil.showPopupWindow(activity, rootLayout, R.string.modification_sub);
            v.setEnabled(false);
            MultipartRequest<DefaultReponse> mRequest = GuaJiAPI.newInstance(activity).requestMultipartPostData(getNewTopicRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
                @Override
                public void onResponse(DefaultReponse response) {
                    TipsUtil.dismissPopupWindow();
                    v.setEnabled(true);
                    if (response != null) {
                        if (response.getResponseCode() == 0) {
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.showToast(activity, response.getResponseMessage());
                        }
                    }
                }
            }, null);
            mRequest.addMultipartStringEntity("activityId", activityId+"");
            mRequest.addMultipartStringEntity("title", mTitleText);
            mRequest.addMultipartStringEntity("desc", mContentText);
            if(saveFile != null) {
                mRequest.addMultipartFileEntity("img", saveFile);
            }
            GuaJiAPI.newInstance(activity).addRequest(mRequest);
        }
    };

    /**
     * 上传的图片点击事件,点击跳转到其它页面
     */
    View.OnClickListener mSubmitImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(activity, SubmitTopicImageBrowseActivity.class);
            if(saveFile != null) {
                mIntent.putExtra("imagePath", saveFile.getAbsolutePath());
            }
            startActivityForResult(mIntent, SET_DELETE_REQUEST_CODE);
        }
    };

    /**
     * 相机点击事件
     */
    View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            saveFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
            startActivityForResult(intent, SET_CAMERA_REQUEST_CODE);
        }
    };

    /**
     * 图库点击事件
     */
    View.OnClickListener mPhotoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver mResolver = getContentResolver();
        FileOutputStream mOutputStream = null;
        if(requestCode == SET_PHOTO_REQUEST_CODE) {
            if(data != null) {
                try {
                    Uri mUri = data.getData();
                    byte[] mContent = readStream(mResolver.openInputStream(Uri.parse(mUri.toString())));
                    saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+".jpg");
                    mBitmap = decodeBitmap(mContent);
                    mOutputStream = new FileOutputStream(saveFile);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, mOutputStream);
                    shareImage.setImageBitmap(mBitmap);
                    shareImage.setVisibility(View.VISIBLE);
                    cameraLayout.setVisibility(View.GONE);
                    photoLayout.setVisibility(View.GONE);
                }catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(mOutputStream != null) {
                        try {
                            mOutputStream.close();
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }else if(requestCode == SET_CAMERA_REQUEST_CODE) {
            ImageLoader.getInstance().displayImage(Uri.fromFile(saveFile).toString(), shareImage, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    shareImage.setVisibility(View.GONE);
                    cameraLayout.setVisibility(View.VISIBLE);
                    photoLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    shareImage.setVisibility(View.VISIBLE);
                    cameraLayout.setVisibility(View.GONE);
                    photoLayout.setVisibility(View.GONE);
                }
            });
        }else if (resultCode == RESULT_OK){
            if(saveFile != null) {
                saveFile.delete();
            }
            shareImage.setVisibility(View.GONE);
            cameraLayout.setVisibility(View.VISIBLE);
            photoLayout.setVisibility(View.VISIBLE);
        }

    }

    public byte[] readStream (InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    private Bitmap decodeBitmap(byte[] mContent) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);;
        int dw = mDisplayMetrics.widthPixels;
        int dh = mDisplayMetrics.heightPixels;
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(mContent, 0, mContent.length, bmpFactoryOptions);
        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)dh);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)dw);
        bmpFactoryOptions.inJustDecodeBounds = false;

        if(heightRatio>widthRatio){
            bmpFactoryOptions.inSampleSize = heightRatio;
        }else{
            bmpFactoryOptions.inSampleSize = widthRatio;
        }
        bmp = BitmapFactory.decodeByteArray(mContent, 0, mContent.length, bmpFactoryOptions);
        return bmp;
    }

    private String getNewTopicRequest() {
        return DOMAIN + "topic/activity.json";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }

        if(saveFile != null) {
            saveFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(activity, titleEdit.getText().toString());
    }
}
