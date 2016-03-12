/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.mzs.guaji.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.DeletePhoto;
import com.mzs.guaji.entity.PhotoList;
import com.mzs.guaji.entity.Pic;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class ImagePagerActivity extends GuaJiActivity {

	private static final String STATE_POSITION = "STATE_POSITION";
	private ViewPager pager;
	private Context context = ImagePagerActivity.this;
	private ImagePagerAdapter mAdapter;
	private PhotoList mPhotoList;
	private PopupWindow mPopupWindow;
	private FrameLayout mRootLayout;
	private LinearLayout mDeletePhotoLayout;
	private TextView mPagerSizeText;
    private boolean isSelf;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_list_image_pager);

		Bundle bundle = getIntent().getExtras();
		mPhotoList = (PhotoList) bundle.getSerializable("photolist");
		int pagerPosition = bundle.getInt("position", 0);
        isSelf = bundle.getBoolean("isSelf", true);
		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}
		
		mRootLayout = (FrameLayout) findViewById(R.id.photo_list_pager_root);
		mPagerSizeText = (TextView) findViewById(R.id.pager_image_size);
		mDeletePhotoLayout = (LinearLayout) findViewById(R.id.pager_image_delete_layout);
        if(!isSelf) {
            mDeletePhotoLayout.setVisibility(View.GONE);
        }
		mDeletePhotoLayout.setOnClickListener(mDeleteClickListener);

		pager = (ViewPager) findViewById(R.id.photo_list_image_pager);
		if(mPhotoList.getPics() != null) {
			mAdapter = new ImagePagerAdapter(mPhotoList.getPics());
			pager.setAdapter(mAdapter);
			pager.setOnPageChangeListener(new OnPagerChangeListener());
			pager.setCurrentItem(pagerPosition);
		}
		mPagerSizeText.setText(pager.getCurrentItem() + 1 + "/" + mPhotoList.getPics().size());
	}

	View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mPhotoList.getPics() != null) {
				mDeletePhotoLayout.setEnabled(false);
				showPopupWindow();
				Pic mPic = mPhotoList.getPics().get(pager.getCurrentItem());
				executeDelete(mPic);
			}
		}
	};
	
	private void executeDelete(Pic mPic) {
		mApi.requestJsonPostData(Method.POST, getRequestDeletePhotoUrl(), new long[]{mPic.getId()}, DeletePhoto.class, new Response.Listener<DeletePhoto>() {

			@Override
			public void onResponse(DeletePhoto response) {
				if(response != null && response.getResponseCode() == 0) {
					refreshPager();
					dimissUploadDialog();
					mDeletePhotoLayout.setEnabled(true);
				}else {
					mDeletePhotoLayout.setEnabled(true);
					dimissUploadDialog();
					ToastUtil.showToast(context, response.getResponseMessage());
				}
			}
			
		},  null);
	}
	
	/**
	 * 获取删除图像url
	 * @return
	 */
	private String getRequestDeletePhotoUrl() {
		return DOMAIN + "user/del_pic.json";
	}
	
	/**
	 * 获取相册列表url
	 * @param page
	 * @param count
	 * @return
	 */
	private String getRequestPhotoListUrl(long page ,long count) {
		return DOMAIN + "user/self_pics.json" + "?p=" + page + "&cnt=" + count;
	}
	
	/**
	 * 删除成功, 刷新grid列表
	 */
	private void refreshPager() {
		page = 1;
		mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>(){
			@Override
			public void onResponse(PhotoList response) {
				if(response != null && response.getResponseCode() == 0) {
                    setResult(Activity.RESULT_OK, new Intent().setAction("delete"));
                    if(response.getPics() != null && mAdapter != null) {
                        mAdapter.addPic(response.getPics());
                        mPagerSizeText.setText(pager.getCurrentItem() + 1 + "/" + mPhotoList.getPics().size());
                    }else {
                        finish();
                    }
 				}
			}
		}, null);
	}
	
	private void showPopupWindow() {
		View v = View.inflate(context, R.layout.image_pop, null);
		mPopupWindow = new PopupWindow(v, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		TextView mDialogMessageText = (TextView) v.findViewById(R.id.dialog_message_text);
		mDialogMessageText.setText(R.string.delete_image_text);
		mPopupWindow.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
	}
	
	private void dimissUploadDialog() {
		if(mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<Pic> mPics;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<Pic> mPics) {
			this.mPics = mPics;
			inflater = getLayoutInflater();
		}
		
		public void addPic(List<Pic> mPics) {
			this.mPics.clear();
			for(Pic mPic : mPics) {
				this.mPics.add(mPic);
			}
			notifyDataSetChanged();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return mPics.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.photo_list_item_pager_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.photo_list_pager_item_image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.photo_list_pager_item_loading);

			mImageLoader.displayImage(mPics.get(position).getImg(), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			((ViewPager) view).addView(imageLayout, 0);
			return imageLayout;
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}
	
	private class OnPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			mPagerSizeText.setText(position + 1 + "/" + mPhotoList.getPics().size());
		}
		
	}
}