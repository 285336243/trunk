package com.socialtv;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;

import com.actionbarsherlock.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.util.ImageUtils;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;

/**
 * 查看图片详情UI
 */
public class ImageDetailsActivity extends DialogFragmentActivity {

	private PhotoViewAttacher mAttacher;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.large_pic);
        String imageUrl = getIntent().getStringExtra("imageUrl");
        ImageView imageView = (ImageView) findViewById(R.id.large_pic_image);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.large_pic_progress);

        mAttacher = new PhotoViewAttacher(imageView);
        mAttacher.setScaleType(ScaleType.FIT_CENTER);
        mAttacher.setOnViewTapListener(new OnViewTapListener() {

            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        ImageLoader.getInstance().displayImage(imageUrl, imageView, ImageUtils.imageLoader(this, 0), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                progressBar.setVisibility(View.GONE);
                mAttacher.update();
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                progressBar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                progressBar.setMax(total);
                progressBar.setProgress(current);
            }
        });
	}

    @Override
	protected void onDestroy() {
		super.onDestroy();
		mAttacher.cleanup();
	}
}
