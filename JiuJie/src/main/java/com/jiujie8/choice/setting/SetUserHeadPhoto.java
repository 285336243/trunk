package com.jiujie8.choice.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.core.ToastUtils;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 设置用户头像
 */
public class SetUserHeadPhoto extends DialogFragmentActivity {
    private static final int SET_PHOTO_REQUEST_CODE = 0x101;
    private static final int CUT_PHOTO_REQUEST_CODE = 0x102;
    protected File saveFile;
    private Dialog popDialog;
    private ImageLoader mImageLoader;
    protected ImageView mAvatarImage;
    private Bitmap mAvatarBitmap;
    private File mediaStorageDir;

    private void cancelDialog() {
        if (popDialog.isShowing() || popDialog != null)
            popDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        popDialog.setCanceledOnTouchOutside(false);
        mImageLoader = ImageLoader.getInstance();
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "JiuJie");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return;
            }
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
//                        saveFile =StorageUtil.createImageFile(this);
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
                    mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mAvatarImage, ImageUtils.avatarImageLoader());
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
     * 设置头像点击事件
     */
    View.OnClickListener userPhotoClickListeners = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setUserHeadPhoto();
        }
    };

    private void setUserHeadPhoto() {
        final SelectUserPhotoDialog mDialog = new SelectUserPhotoDialog();
        mDialog.show(getSupportFragmentManager(), "blur");
        mDialog.setDialogListener(new SelectUserPhotoDialog.DialogListener() {
            @Override
            public void takePicture() {
//                ToastUtils.show(SetUserHeadPhoto.this, "拍照hahahah");
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                saveFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
                startActivityForResult(intentCamera, SET_PHOTO_REQUEST_CODE);
            }

            @Override
            public void photoAlbum() {
//                ToastUtils.show(SetUserHeadPhoto.this,"相册hahahhahaha选择");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
            }
        });
    }

    /**
     * 调用系统裁剪头像图片
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
        this.recyclebitmap(mAvatarBitmap);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JiuJie";
        String[] fileName = new String[]{"photo"};
        StorageUtil.deleteAllFiles(path, fileName);
        System.gc();
    }

    protected void recyclebitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

}
