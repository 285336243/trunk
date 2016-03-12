package com.jiujie8.choice.choice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.jiujie8.choice.R;
import com.jiujie8.choice.core.DialogFragmentActivity;
import com.jiujie8.choice.util.IConstant;
import com.jiujie8.choice.util.ImageUtils;
import com.jiujie8.choice.util.StorageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14/12/23.
 * 发布话题时的截图页面
 */
@ContentView(R.layout.crop_image_layout)
public class CropImageActivity extends DialogFragmentActivity {

    @InjectView(R.id.crop_image_view)
    private TouchImageView mCropImageView;

    @InjectView(R.id.crop_image_cancel)
    private View mCancelView;

    @InjectView(R.id.crop_image_designate)
    private View mDesignateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String mImagePath = getStringExtra(IConstant.IMAGE_PATH);
        ImageLoader.getInstance().loadImage(Uri.fromFile(new File(mImagePath)).toString(), ImageUtils.defaultImageLoader(), new SimpleImageLoadingListener(){

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                mCropImageView.setImageBitmap(loadedImage);
            }
        });

        mCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDesignateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream mOutputStream = null;
                try {
                    final File mFile = StorageUtil.createImageFile(activity);
                    mOutputStream = new FileOutputStream(mFile);
                    Bitmap mBitmap = mCropImageView.getCropBitmap();
                    final boolean compress = mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, mOutputStream);
                    if (compress) {
                        mOutputStream.flush();
                    }
                    mBitmap.recycle();
                    final Intent mIntent = new Intent(IConstant.CROP_IMAGE_ACTION);
                    mIntent.putExtra(IConstant.IMAGE_PATH, mFile.getAbsolutePath());
                    setResult(RESULT_OK, mIntent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mOutputStream != null) {
                        try {
                            mOutputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
