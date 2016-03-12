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
package com.socialtv.personcenter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.PhotoAlbum;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;
import uk.co.senab.photoview.PhotoView;

/**
 * 查看相册详情的页面
 */
@ContentView(R.layout.photo_list_image_pager)
public class ImagePagerActivity extends DialogFragmentActivity {

    private static final String STATE_POSITION = "STATE_POSITION";

    @InjectView(R.id.photo_list_image_pager)
    private ViewPager pager;

    @InjectView(R.id.photo_list_image_size)
    private TextView imageSizeTextView;

    @InjectView(R.id.photo_list_image_delete)
    private TextView deleteTextView;

    @Inject
    private Activity activity;

    @InjectExtra(value = IConstant.PHOTO_ALBUM, optional = true)
    private PhotoAlbum photoAlbum;

    @InjectExtra(value = IConstant.IS_SELF, optional = true)
    private boolean isSelf;

    @InjectExtra(value = IConstant.PHOTO_ALBUM_LIST_POSITION, optional = true)
    private int position;

    @Inject
    private PersonalUrlService services;

    private ImagePagerAdapter adapter;

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    private int removePosition = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        if(!isSelf) {
            deleteTextView.setVisibility(View.GONE);
        }
        adapter = new ImagePagerAdapter();
		if(photoAlbum.getPics() != null) {
			pager.setAdapter(adapter);
            adapter.addPic(photoAlbum.getPics());
			pager.setOnPageChangeListener(new OnPagerChangeListener());
			pager.setCurrentItem(position);
            imageSizeTextView.setText(position + 1 + "/" + photoAlbum.getPics().size());
		}

        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果 userId = null 则是从发布话题过来的, 其他是从相册里过来
                if (TextUtils.isEmpty(userId)) {
                    List<Pics> items = photoAlbum.getPics();
                    if (items != null && !items.isEmpty()) {
                        items.remove(removePosition);
                        if (items.isEmpty()) {
                            Intent intent = new Intent();
                            intent.putExtra(IConstant.PHOTO_ALBUM, photoAlbum);
                            setResult(RESULT_OK, intent);
                            finish();
                            return;
                        }
                        adapter.addPic(items);
                        imageSizeTextView.setText(removePosition + 1 + "/" + items.size());
                    }
                } else {
                    new ProgressDialogTask<Response>(activity) {

                        @Override
                        protected Response run(Object data) throws Exception {
                            try {
                                return (Response) HttpUtils.doRequest(services.createDeletePhotoAlbumRequest(photoAlbum.getPics().get(pager.getCurrentItem()).getId())).result;
                            } catch (Throwable e) {
                                e.printStackTrace();
                                return null;
                            }
                        }

                        @Override
                        protected void onSuccess(Response response) throws Exception {
                            super.onSuccess(response);
                            if (response != null) {
                                if (response.getResponseCode() == 0) {
                                    setResult(RESULT_OK);
                                    refreshPager();
                                } else {
                                    ToastUtils.show(activity, response.getResponseMessage());
                                }
                            }
                        }
                    }.start("正在删除");
                }
            }
        });
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //只有当userId为空时才把photoAlbum对象返回到result里
        //userId不为空时,是从相册里过来的,不能传photoAlbum对象回去
        if (keyCode == KeyEvent.KEYCODE_BACK && TextUtils.isEmpty(userId)) {
            Intent intent = new Intent();
            intent.putExtra(IConstant.PHOTO_ALBUM, photoAlbum);
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshPager() {
        new AbstractRoboAsyncTask<PhotoAlbum>(activity){

            @Override
            protected PhotoAlbum run(Object data) throws Exception {
                return (PhotoAlbum) HttpUtils.doRequest(services.createPhotoAlbumRequest(userId)).result;
            }

            @Override
            protected void onSuccess(PhotoAlbum photoAlbum) throws Exception {
                super.onSuccess(photoAlbum);
                if (photoAlbum != null) {
                    if (photoAlbum.getResponseCode() == 0) {
                        if (photoAlbum.getPics() != null && !photoAlbum.getPics().isEmpty()) {
                            ImagePagerActivity.this.photoAlbum = photoAlbum;
                            imageSizeTextView.setText(pager.getCurrentItem() + 1 + "/" + photoAlbum.getPics().size());
                            adapter.addPic(photoAlbum.getPics());
                        } else {
                            finish();
                        }
                    } else {
                        ToastUtils.show(activity, photoAlbum.getResponseMessage());
                    }
                }
            }
        }.execute();
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<Pics> mPics = new ArrayList<Pics>();
		private LayoutInflater inflater;

		ImagePagerAdapter() {
			inflater = getLayoutInflater();
		}
		
		public void addPic(List<Pics> mPics) {
			this.mPics.clear();
			for(Pics mPic : mPics) {
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
			PhotoView photoView = (PhotoView) imageLayout.findViewById(R.id.photo_list_pager_item_image);
			final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.photo_list_pager_item_loading);

            String imagePath = null;
            if (mPics.get(position).getImg().startsWith("http")) {
                imagePath = mPics.get(position).getImg();
            } else {
                imagePath = Uri.fromFile(new File(mPics.get(position).getImg())).toString();
            }
			ImageLoader.getInstance().displayImage(imagePath, photoView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
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

			view.addView(imageLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
            removePosition = position;
			imageSizeTextView.setText(position + 1 + "/" + photoAlbum.getPics().size());
		}
		
	}
}