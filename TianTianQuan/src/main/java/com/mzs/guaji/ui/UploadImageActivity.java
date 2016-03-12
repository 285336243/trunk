package com.mzs.guaji.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.UploadPic;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class UploadImageActivity extends GuaJiActivity {

	private Bitmap mBitmap;
	private Context context = UploadImageActivity.this;
	private String imagePath;
	private File file;
	private RelativeLayout mCompleteLayout;
	private FrameLayout mRootLayout;
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.upload_image_layout);
		imagePath = getIntent().getStringExtra("imagepath");
		file = new File(imagePath);
		ImageButton mBackButton = (ImageButton) findViewById(R.id.upload_back);
		mBackButton.setOnClickListener(mBackClickListener);
		
		mRootLayout = (FrameLayout) findViewById(R.id.upload_image_root_layout);
		ImageView mImageView = (ImageView) findViewById(R.id.upload_image);
		mImageLoader.displayImage(Uri.fromFile(file).toString(), mImageView, new DisplayImageOptions.Builder().imageScaleType(ImageScaleType.IN_SAMPLE_INT).build(),new SimpleImageLoadingListener(){

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				OutputStream stream = null;
				try {
					stream = new FileOutputStream(file);
					loadedImage.compress(CompressFormat.JPEG, 70, stream);
					stream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			
		});
		mCompleteLayout = (RelativeLayout) findViewById(R.id.upload_complete);
		mCompleteLayout.setOnClickListener(mCompleteClickListener);
	}
	
	View.OnClickListener mCompleteClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.upload_image_text);
			mCompleteLayout.setEnabled(false);
			MultipartRequest<UploadPic> mRequest = mApi.requestMultipartPostData(getRequestUploadUrl(), UploadPic.class, new Response.Listener<UploadPic>() {
				@Override
				public void onResponse(UploadPic response) {
					TipsUtil.dismissPopupWindow();
					mCompleteLayout.setEnabled(true);
					if(response != null && response.getResponseCode() == 0) {
						ToastUtil.showToast(context, R.string.upload_pic_complete);
						setResult(Activity.RESULT_OK, new Intent().setAction("upload"));
						UploadImageActivity.this.finish();
					}else {
						ToastUtil.showToast(context, R.string.upload_pic_error);
					}
				}
			}, UploadImageActivity.this);
			mRequest.addMultipartFileEntity("pic", file);
			mApi.addRequest(mRequest);
		}
	};
	
	private String getRequestUploadUrl() {
		return DOMAIN + "user/pic.json";
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
		if(file != null && file.exists()) {
			file.delete();
		}
	};
}
