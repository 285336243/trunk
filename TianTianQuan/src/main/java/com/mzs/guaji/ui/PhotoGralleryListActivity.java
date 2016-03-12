package com.mzs.guaji.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.mzs.guaji.R;
import com.mzs.guaji.adapter.PhotoGralleryAdapter;
import com.mzs.guaji.core.Log;
import com.mzs.guaji.entity.DeletePhoto;
import com.mzs.guaji.entity.PhotoList;
import com.mzs.guaji.entity.Pic;
import com.mzs.guaji.util.ListViewLastItemVisibleUtil;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 相册列表
 * @author lenovo
 *
 */
public class PhotoGralleryListActivity extends GuaJiActivity {

	private PullToRefreshGridView mRefreshGridView;
	private Context context = PhotoGralleryListActivity.this;
	private PhotoGralleryAdapter mAdapter;
	private PopupWindow mSettingPhotoPopupWindow;
	private static final int SET_PHOTO_CAMERA_REQUEST_CODE = 1;
	private static final int SET_PHOTO_ALBUM_REQUEST_CODE = 2;
	private static final int UPLOAD_IMAGE_FINISH = 3;
	private static final int VIEWPAGER_DELETE_FINISH = 4;
	private LinearLayout mRootLayout;
	private File saveFile;
	private File mediaStorageDir;
	private Bitmap mBitmap;
	private PopupWindow mDeletePhotoPopupWindow;
	private TextView mEmptyText;
    private boolean isSelf;
    private long userId;
    private PhotoList mSelfPhotoList;
    private PhotoList mOthersPhotoList;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.photo_grallery_list_layout);
        isSelf = getIntent().getBooleanExtra("isSelf", true);
        userId = getIntent().getLongExtra("userId", -1);
		LinearLayout mBackLayout = (LinearLayout) findViewById(R.id.photo_list_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mEmptyText = (TextView) findViewById(R.id.photo_empty_text);
		mRootLayout = (LinearLayout) findViewById(R.id.photo_grallery_root_layout);
		LinearLayout mAddPicLayout = (LinearLayout) findViewById(R.id.photo_list_add_pic);
		mAddPicLayout.setOnClickListener(mSettingPhotoListener);
		
		mRefreshGridView = (PullToRefreshGridView) findViewById(R.id.photo_list_grid);

        if(isSelf) {
            mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                @Override
                public void onResponse(PhotoList response) {
                    mSelfPhotoList = response;
                    if(response != null && response.getPics() != null && response.getPics().size() > 0) {
                        setGridEvent(response);
                        mRefreshGridView.getRefreshableView().setOnItemLongClickListener(mGridLongItemClickListener);
                    }else {
                        showEmptyText();
                    }
                }
            }, this);
        }else {
            mAddPicLayout.setVisibility(View.GONE);
            mApi.requestGetData(getOthersPhotoListRequest(userId, page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                @Override
                public void onResponse(PhotoList response) {
                    mOthersPhotoList = response;
                    setGridEvent(response);

                }
            }, this);
        }

		
		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory( 
                Environment.DIRECTORY_PICTURES), "GuaJi"); 
		if (! mediaStorageDir.exists()){  
          if (! mediaStorageDir.mkdirs()){  
              return;  
        }  
      }
	}

	/**
	 * 当adapter或者list内容为空时,显示emptytext
	 */
	private void showEmptyText() {
		mEmptyText.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 当adapter或者list内空不为空时, 隐藏emptytext
	 */
	private void goneEmptyText() {
		mEmptyText.setVisibility(View.GONE);
	}
	
	/**
	 * 设置gridview事件
	 * @param response
	 */
	private void setGridEvent(PhotoList response) {
		goneEmptyText();
		mAdapter = new PhotoGralleryAdapter(context, response.getPics());
        Log.v("person", "每次  " + response.getPics().size() + " 条");
        SwingBottomInAnimationAdapter mAnimationAdapter = new SwingBottomInAnimationAdapter(mAdapter, 150);
		mAnimationAdapter.setAbsListView(mRefreshGridView.getRefreshableView());
		mRefreshGridView.setAdapter(mAnimationAdapter);
        mRefreshGridView.setOnItemClickListener(mGridItemClickListener);
		mRefreshGridView.setOnRefreshListener(mRefreshListener);
		mRefreshGridView.setOnLastItemVisibleListener(mLastItemVisibleListener);

	}

    PullToRefreshBase.OnRefreshListener<GridView> mRefreshListener = new PullToRefreshBase.OnRefreshListener<GridView>() {
        @Override
        public void onRefresh(final PullToRefreshBase<GridView> refreshView) {
            if(isSelf) {
                mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                    @Override
                    public void onResponse(PhotoList response) {
                        if(response != null) {
                            mRefreshGridView.onRefreshComplete();
                            String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                            if(response.getResponseCode() == 0) {
                                if(response.getPics() != null) {
                                    mAdapter.clear();
                                    mAdapter.addPic(response.getPics());
                                    Log.v("person", "刷新每次  " + response.getPics().size() + " 条");
                                }
                            }else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }
                }, PhotoGralleryListActivity.this);
            }else {
                mApi.requestGetData(getOthersPhotoListRequest(userId, page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                    @Override
                    public void onResponse(PhotoList response) {
                        if(response != null) {
                            mRefreshGridView.onRefreshComplete();
                            String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                            if(response.getResponseCode() == 0) {
                                if(response.getPics() != null) {
                                    mAdapter.clear();
                                    mAdapter.addPic(response.getPics());
                                }
                            }else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }

                    }
                }, PhotoGralleryListActivity.this);
            }
        }
    };
	
//	PullToRefreshBase.OnRefreshListener2<GridView> mRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<GridView>() {
//
//		@Override
//		public void onPullDownToRefresh(final PullToRefreshBase<GridView> refreshView) {
//			page = 1;
//			mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>() {
//				@Override
//				public void onResponse(PhotoList response) {
//					mRefreshGridView.onRefreshComplete();
//                    String label = DateUtils.formatDateTime(context, System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
//					if(mAdapter != null && response != null && response.getPics() != null) {
//						mPhotoList = response;
//						mAdapter.clear();
//						mAdapter.addPic(response.getPics());
//					}
//				}
//			}, PhotoGralleryListActivity.this);
//
//
//		}
//
//		@Override
//		public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
//
//		}
//	};
	
	PullToRefreshBase.OnLastItemVisibleListener mLastItemVisibleListener = new PullToRefreshBase.OnLastItemVisibleListener() {

		@Override
		public void onLastItemVisible() {
            if(isSelf) {
                if(mSelfPhotoList != null) {
                    if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mSelfPhotoList.getTotal())) {
                        if(mSelfPhotoList.getTotal() > count) {
                            if (!isFootShow) {
                                ToastUtil.showToast(context, R.string.toast_last_page_tip);
                                isFootShow = true;
                            }
                        }
                    return;
                    }
                }
                page = page + 1;
                mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                    @Override
                    public void onResponse(PhotoList response) {
                        if(response != null) {
                            mRefreshGridView.onRefreshComplete();
                            if(response.getResponseCode() == 0) {
                                if(response.getPics() != null) {
                                    mAdapter.addPic(response.getPics());
                                    Log.v("person", "加载更多每次  " + response.getPics().size() + " 条");
                                }
                            }else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }
                    }
                }, PhotoGralleryListActivity.this);
            }else {
                if(mOthersPhotoList != null) {
                    if(ListViewLastItemVisibleUtil.isLastItemVisible(page, count, mOthersPhotoList.getTotal())) {
                        if(mOthersPhotoList.getTotal() > count) {
                            if (!isFootShow) {
                                ToastUtil.showToast(context, R.string.toast_last_page_tip);
                                isFootShow = true;
                            }
                        }
                        return;
                    }
                }
                page = page + 1;
                mApi.requestGetData(getOthersPhotoListRequest(userId, page, count), PhotoList.class, new Response.Listener<PhotoList>() {
                    @Override
                    public void onResponse(PhotoList response) {
                        if(response != null) {
                            mRefreshGridView.onRefreshComplete();
                            if(response.getResponseCode() == 0) {
                                if(response.getPics() != null) {
                                    mAdapter.addPic(response.getPics());
                                    if (ListViewLastItemVisibleUtil.isLastItemVisible(page, count, response.getTotal())) {
                                        mRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        isLastItemVisible = true;
                                    }
                                }
                            }else {
                                ToastUtil.showToast(context, response.getResponseMessage());
                            }
                        }

                    }
                }, PhotoGralleryListActivity.this);
            }
		}
	};
	
	/**
	 * gridview item 长按事件, 长按弹出删除popupwindow
	 */
	AdapterView.OnItemLongClickListener mGridLongItemClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			View mDeletePhotoView = View.inflate(context, R.layout.delete_photo_pop, null);
			Button mDeleteButton = (Button) mDeletePhotoView.findViewById(R.id.delete_photo_button);
			if(mAdapter != null) {
				mDeleteButton.setTag(mAdapter.getListPic().get(position));
			}
			mDeleteButton.setOnClickListener(mDeletePhotoListener);
			mDeletePhotoPopupWindow = new PopupWindow(mDeletePhotoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			if(!mDeletePhotoPopupWindow.isShowing()) {
				InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				mDeletePhotoPopupWindow.showAtLocation(mRootLayout, Gravity.CENTER, 0, 0);
			}
			return true;
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		mRefreshGridView.setOnScrollListener(new PauseOnScrollListener(mImageLoader, true, true));
	};
	
	/**
	 * deletepopupwindow 删除图片按钮点击事件
	 */
	View.OnClickListener mDeletePhotoListener = new View.OnClickListener() {
		@Override
		public void onClick(final View v) {
			Pic mPic = (Pic) v.getTag();
			mApi.requestJsonPostData(Method.POST, getRequestDeletePhotoUrl(), new long[]{mPic.getId()}, DeletePhoto.class, new Response.Listener<DeletePhoto>() {
				@Override
				public void onResponse(DeletePhoto response) {
					if(response != null) {
						if(response.getResponseCode() == 0) {
							deleteRefreshGrid();
							dismissDeletePhotoPopupWindow();
						}else {
							dismissDeletePhotoPopupWindow();
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, null);
		}
	};
	
	/**
	 * 删除成功, 刷新grid列表
	 */
	private void deleteRefreshGrid() {
		page = 1;
		mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>(){
			@Override
			public void onResponse(PhotoList response) {
				if(response != null && response.getPics() != null && mAdapter != null) {
					goneEmptyText();
					mAdapter.clear();
					mAdapter.addPic(response.getPics());
				}else {
					showEmptyText();
				}
			}
		}, null);
	}
	
	/**
	 * 上传成功, 刷新grid列表
	 */
	private void uploadRefreshGrid() {
		page = 1;
		mApi.requestGetData(getRequestPhotoListUrl(page, count), PhotoList.class, new Response.Listener<PhotoList>(){
			@Override
			public void onResponse(PhotoList response) {
				if(response != null && response.getPics() != null && mAdapter != null) {
					goneEmptyText();
					mAdapter.clear();
					mAdapter.addPic(response.getPics());
				}else {
					setGridEvent(response);
				}
			}
		}, null);
	}

	/**
	 * 添加图片按钮的点击事件, 点击弹出popupwindow
	 */
	View.OnClickListener mSettingPhotoListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			View mSettingPhotoView = View.inflate(context, R.layout.setting_photo_pop, null);
			mSettingPhotoPopupWindow = new PopupWindow(mSettingPhotoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			Button mSettingPhotoCameraButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_camera);
			mSettingPhotoCameraButton.setOnClickListener(mSettingPhotoCameraListener);
			Button mSelectLocalButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_local);
			mSelectLocalButton.setOnClickListener(mSelectLocalListener);
			Button mCancelButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_cancel);
			mCancelButton.setOnClickListener(mCancelPopupWindowListener);
			if(!mSettingPhotoPopupWindow.isShowing()) {
				mSettingPhotoPopupWindow.showAtLocation(mRootLayout, Gravity.BOTTOM, 0, 0);
			}
		}
	};
	
	/**
	 * popupwindow 选择本地图片按钮点击事件
	 */
	View.OnClickListener mSelectLocalListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
			mIntent.addCategory(Intent.CATEGORY_OPENABLE);
			mIntent.setType("image/*");
			startActivityForResult(mIntent, SET_PHOTO_ALBUM_REQUEST_CODE);
		}
	};
	
	/**
	 * popupwindow 拍照按钮点击事件
	 */
	View.OnClickListener mSettingPhotoCameraListener = new View.OnClickListener() {
		@SuppressLint("SimpleDateFormat")
		@Override
		public void onClick(View v) {
			String savePath = "";
			if(StorageUtil.isExternalStorageAvailable()) {
				savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Guaji/photo/";//存放照片的文件夹
				File savedir = new File(savePath);
				if (!savedir.exists()) {
					savedir.mkdirs();
				}
			}
			if("".equals(savePath)){
				ToastUtil.showToast(context, R.string.sd_no_exists);
				return;
			}
			String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			String fileName = "tt_" + timeStamp + ".jpg";
			saveFile = new File(savePath, fileName);
			Uri uri = Uri.fromFile(saveFile);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(intent, SET_PHOTO_CAMERA_REQUEST_CODE);
		}
	};
	
	/**
	 * popupwindow 取消按钮点击事件
	 */
	View.OnClickListener mCancelPopupWindowListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mSettingPhotoPopupWindow != null && mSettingPhotoPopupWindow.isShowing()) {
				mSettingPhotoPopupWindow.dismiss();
			}
		}
	};
	
	public byte[] readStream ( InputStream inStream ) throws Exception
	{
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1)
		{
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		FileOutputStream outputStream = null;
		ContentResolver mResolver = getContentResolver();
			if(resultCode == Activity.RESULT_OK ) {
				dismissSettingPhotoPopupWindow();
				try {
					if(requestCode == SET_PHOTO_ALBUM_REQUEST_CODE) {
						if(data != null){
							Uri mUri = data.getData();
							byte[] mContent = readStream(mResolver.openInputStream(Uri.parse(mUri.toString())));
							mBitmap = decodeBitmap(mContent);
							saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+".jpg");
							outputStream = new FileOutputStream(saveFile);
							mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
							startUploadImage();
						}
					}else if (requestCode == SET_PHOTO_CAMERA_REQUEST_CODE) {
						startUploadImage();
					}else if (requestCode == UPLOAD_IMAGE_FINISH) {
						uploadRefreshGrid();
					}else if (requestCode == VIEWPAGER_DELETE_FINISH) {
						deleteRefreshGrid();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					if(outputStream != null) {
						try {
							outputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		}else if(resultCode == Activity.RESULT_CANCELED) {
			dismissSettingPhotoPopupWindow();
		}
	}

	/**
	 * 关闭deletePhotoPopupWindow
	 */
	private void dismissDeletePhotoPopupWindow() {
		if (mDeletePhotoPopupWindow != null && mDeletePhotoPopupWindow.isShowing()) {
			mDeletePhotoPopupWindow.dismiss();
		}
	}
	
	/**
	 * 关闭settingphotopopupwindow
	 */
	private void dismissSettingPhotoPopupWindow() {
		if(mSettingPhotoPopupWindow != null && mSettingPhotoPopupWindow.isShowing()) {
			mSettingPhotoPopupWindow.dismiss();
		}
	}
	
//	private void getCameraPhoto(Intent data) {
//		Uri thisUri = data.getData();
//    	String thePath = ImageUtils.getAbsolutePathFromNoStandardUri(thisUri);
//
//    	//如果是标准Uri
//    	if("".equals(thePath)){
//    		photoAbsoluthPath = ImageUtils.getAbsoluteImagePath(this,thisUri);
//    	}else {
//    		photoAbsoluthPath = thePath;
//    	}
//    	String attFormat = StorageUtil.getFileFormat(photoAbsoluthPath);
//    	if(!"photo".equals(StorageUtil.getContentType(attFormat)))
//    	{
//    		Toast.makeText(context, getString(R.string.choose_image), Toast.LENGTH_SHORT).show();
//    		return;
//    	}
//    	String imgName = StorageUtil.getFileName(photoAbsoluthPath);
//		Bitmap bitmap = ImageUtils.loadImgThumbnail(this, imgName, MediaStore.Images.Thumbnails.MICRO_KIND);
//	}
//	
	/**
	 * 激活上传图片页面
	 */
	private void startUploadImage() {
		Intent mIntent = new Intent(context, UploadImageActivity.class);
		mIntent.putExtra("imagepath", saveFile.getAbsolutePath());
		startActivityForResult(mIntent, UPLOAD_IMAGE_FINISH);
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
	
	/**
	 * girdview item 点击事件,点击跳出到浏览大图
	 */
	AdapterView.OnItemClickListener mGridItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
            MobclickAgent.onEvent(context, "user_photo_read");
			Intent mIntent = new Intent(context, ImagePagerActivity.class);
			Bundle mBundle = new Bundle();
            if(isSelf) {
                mBundle.putSerializable("photolist", mSelfPhotoList);
            }else {
                mBundle.putSerializable("photolist", mOthersPhotoList);
            }
			mIntent.putExtra("position", position);
            mIntent.putExtra("isSelf", isSelf);
			mIntent.putExtras(mBundle);
			startActivityForResult(mIntent, VIEWPAGER_DELETE_FINISH);
		}
	};
	
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

    private String getOthersPhotoListRequest(long userId, long page, long count) {
        return DOMAIN + "user/read_pics.json" + "?userId=" + userId + "&p=" + page + "&cnt=" + count;
    }
	
	/**
	 * 拦截后退键,如果popupwindow显示,就dismiss掉,否则finish当前activity
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mSettingPhotoPopupWindow != null && mSettingPhotoPopupWindow.isShowing()) {
				mSettingPhotoPopupWindow.dismiss();
				return false;
			}else if (mDeletePhotoPopupWindow != null && mDeletePhotoPopupWindow.isShowing()) {
				mDeletePhotoPopupWindow.dismiss();
				return false;
			} else {
				finish();
				return super.onKeyDown(keyCode, event);
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
		}
	}
}
