package com.mzs.guaji.ui;

import uk.co.senab.photoview.PhotoViewAttacher;
import uk.co.senab.photoview.PhotoViewAttacher.OnViewTapListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.mzs.guaji.R;

/**
 * 查看图片详情UI
 */
public class ImageDetailsActivity extends GuaJiActivity {

	private PhotoViewAttacher mAttacher;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.image_details_layout);
		String imageUrl = getIntent().getStringExtra("imageUrl");
		ImageView mImageView = (ImageView) findViewById(R.id.image_details_imageview);
		mImageLoader.displayImage(imageUrl, mImageView, options);
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setScaleType(ScaleType.FIT_CENTER);
		mAttacher.setOnViewTapListener(new OnViewTapListener() {
			
			@Override
			public void onViewTap(View view, float x, float y) {
				finish();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAttacher.cleanup();
	}
}
