package com.mzs.guaji.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mzs.guaji.R;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.StorageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wlanjie on 14-1-3.
 */
public class SetAvatarActivity extends GuaJiActivity {

    protected static final int CUT_PHOTO_REQUEST_CODE = 2;
    protected static final int SET_PHOTO_REQUEST_CODE = 1;
    protected Context context = SetAvatarActivity.this;
    protected File saveFile;
    protected File mediaStorageDir;
    protected ImageView mAvatarImage;
    protected Bitmap mAvatarBitmap;
    protected RelativeLayout mAvatarLayout;
    protected Dialog mDialog;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
        }
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
     * 设置头像点击事件
     */
    View.OnClickListener mSettingPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           showSetAvatarDialog();
        }
    };

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
                    mAvatarBitmap = (Bitmap) data.getExtras().get("data");
                    cancelDialog();
                    try {
                        saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis() + ".jpg");
                        outputStream = new FileOutputStream(saveFile);
                        mAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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
                    mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mAvatarImage, ImageUtils.imageLoader(context, 4));
                }
            } else {
                if (data != null) {
                    cropImageUri(data.getData());
                } else {
                    cropImageUri(Uri.fromFile(saveFile));
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

        if (mAvatarBitmap != null && !mAvatarBitmap.isRecycled()) {
            mAvatarBitmap.recycle();
            mAvatarImage = null;
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/GuaJi";
        String[] fileName = new String[]{"photo"};
        StorageUtil.deleteAllFiles(path, fileName);
    }


}
