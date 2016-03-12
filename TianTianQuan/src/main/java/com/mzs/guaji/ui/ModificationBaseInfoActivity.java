package com.mzs.guaji.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.mzs.guaji.R;
import com.mzs.guaji.entity.PersonCenterInfo;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.TipsUtil;
import com.mzs.guaji.util.ToastUtil;

/**
 * 修改个人资料页面
 * @author lenovo
 *
 */
public class ModificationBaseInfoActivity extends GuaJiActivity {

	private LinearLayout mBackLayout;
	private ImageView mAvatarImage;
	private RelativeLayout mAvatarLayout;
	private RelativeLayout mCoverLayout;
	private RelativeLayout mSexLayout;
	private EditText mNickNameText;
	private TextView mSexText;
	private EditText mAutographText;
	private long userId;
	private Context context = ModificationBaseInfoActivity.this;
	private String mSex = "f";
	private LinearLayout mSaveLayout;
	private PopupWindow mSettingPhotoPopupWindow;
	private LinearLayout mRootLayout;
	private File saveFile;
	private File backgroundFile;
	private File mediaStorageDir;
	private static final int CUT_PHOTO_REQUEST_CODE = 2;
	private static final int SET_PHOTO_REQUEST_CODE = 1;
	private String type;
	private Bitmap mAvatarBitmap;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.modification_base_info_layout);
		userId = getIntent().getLongExtra("userId", -1);
		mRootLayout = (LinearLayout) findViewById(R.id.modification_root_layout);
		mBackLayout = (LinearLayout) findViewById(R.id.modification_base_info_title_back);
		mBackLayout.setOnClickListener(mBackClickListener);
		mAvatarImage = (ImageView) findViewById(R.id.modification_avatar_image);
		mAvatarLayout = (RelativeLayout) findViewById(R.id.modification_avatar_layout);
		mAvatarLayout.setOnClickListener(mAvatarClickListener);
		mCoverLayout = (RelativeLayout) findViewById(R.id.modification_cover_layout);
		mCoverLayout.setOnClickListener(mCoverClickListener);
		mNickNameText = (EditText) findViewById(R.id.modification_nickname_text);
		mSexLayout = (RelativeLayout) findViewById(R.id.modification_sex_layout);
		mSexLayout.setOnClickListener(mSexClickListener);
		mSexText = (TextView) findViewById(R.id.modification_sex_text);
		mAutographText = (EditText) findViewById(R.id.modification_autograph_text);
		mSaveLayout = (LinearLayout) findViewById(R.id.modification_base_info_save);
		mSaveLayout.setOnClickListener(mSaveClickListener);
		
		mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi"); 
		if (!mediaStorageDir.exists()){
			if (!mediaStorageDir.mkdirs()){
				return;
          	}
		}
		mApi.requestGetData(getUserInfoRequest(userId), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {
			@Override
			public void onResponse(PersonCenterInfo response) {
				if(response != null) {
					if(response.getResponseCode() == 0) {
						mNickNameText.setText(response.getNickname());
						mSex = response.getGender();
						if("f".equals(response.getGender())) {
							mSexText.setText("女");
                            mSex = "f";
						}else {
							mSexText.setText("男");
                            mSex = "m";
						}
						mAutographText.setText(response.getSignature());
						mImageLoader.displayImage(response.getAvatar(), mAvatarImage, ImageUtils.imageLoader(context, 4));
					}else {
						ToastUtil.showToast(context, response.getResponseMessage());
					}
				}
			}
			
		}, this);
	}
	
	View.OnClickListener mBackClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};
	
	/**
	 * 修改头像
	 */
	View.OnClickListener mAvatarClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			type = "avatar";
			showModificationPopupWindow();
		}
	};
	
	/**
	 * 修改封面
	 */
	View.OnClickListener mCoverClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			type = "background";
			showModificationPopupWindow();
		}
	};
	
	/**
	 * 选择性别
	 */
	View.OnClickListener mSexClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int sex = 1;
			if("f".equals(mSex)) {
				sex = 1;
			}else {
				sex = 0;
			}
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
			mBuilder.setCancelable(false)
			.setSingleChoiceItems(new String[]{"男","女"}, sex, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	if(whichButton == 0) {
                		mSex = "m";
                	}else {
                		mSex = "f";
                	}
                }
            })
            .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	
                    }
            })
            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	if("m".equals(mSex)) {
                            mSexText.setText("男");
                        }else {
                            mSexText.setText("女");
                        }
                    }
            })
            .create();
			mBuilder.show();
		}
	};
	
	/**
	 * 保存按钮
	 */
	View.OnClickListener mSaveClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mSaveLayout.setEnabled(false);
			if("".equals(mNickNameText.getText().toString())) {
				ToastUtil.showToast(context, "昵称不能为空");
				return;
			}
			TipsUtil.showPopupWindow(context, mRootLayout, R.string.modification_sub);
			MultipartRequest<PersonCenterInfo> request = mApi.requestMultipartPostData(getModificationInfoRequest(), PersonCenterInfo.class, new Response.Listener<PersonCenterInfo>() {

				@Override
				public void onResponse(PersonCenterInfo response) {
					TipsUtil.dismissPopupWindow();
					mSaveLayout.setEnabled(true);
					if(response != null) {
						if(response.getResponseCode() == 0) {
                            ToastUtil.showToast(context, R.string.update_info_succeed);
                            if(response.getGivenScore() != null) {
                                showScoreDialog(response.getGivenScore().getMessage());
                            }else {
                                finish();
                            }
						}else {
							ToastUtil.showToast(context, response.getResponseMessage());
						}
					}
				}
			}, ModificationBaseInfoActivity.this);
            if("".equals(mAutographText.getText().toString())) {
                request.addMultipartStringEntity("nickname", mNickNameText.getText().toString()).addMultipartStringEntity("gender", mSex);
            }else {
                request.addMultipartStringEntity("nickname", mNickNameText.getText().toString()).addMultipartStringEntity("gender", mSex).addMultipartStringEntity("signature", mAutographText.getText().toString());
            }

			
			if(saveFile != null) {
				request.addMultipartFileEntity("avatar", saveFile);
			}
			if(backgroundFile != null) {
				request.addMultipartFileEntity("bgImg", backgroundFile);
			}
			mApi.addRequest(request);
		}
	};

    private void showScoreDialog(String message) {
        final Dialog mDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.first_login_dialog);
        TextView mTextView = (TextView) mDialog.findViewById(R.id.first_login_text);
        ImageButton mCloseButton = (ImageButton) mDialog.findViewById(R.id.first_login_close);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog.isShowing()) {
                    mDialog.dismiss();
                    finish();
                }
            }
        });
        mTextView.setText(message);
        if(!mDialog.isShowing()) {
            mDialog.show();
//            Message msg = Message.obtain();
//            msg.obj = mDialog;
//            scoreHandler.sendMessageDelayed(msg, 3000);
        }
    }
//
//    private Handler scoreHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Dialog mDialog = (Dialog) msg.obj;
//            if(mDialog.isShowing()) {
//                mDialog.dismiss();
//                finish();
//            }
//        }
//    };
	
	/**
	 * 获取个人资料
	 * @param userId
	 * @return
	 */
	private String getUserInfoRequest(long userId) {
		return DOMAIN + "user/self.json";
	}
	
	/**
	 * 上传修改过的资料
	 * @return
	 */
	private String getModificationInfoRequest() {
		return DOMAIN + "user/profile.json";
	}
	
	/**
	 * 照相popupwindow
	 */
	private void showModificationPopupWindow() {
		View mSettingPhotoView = View.inflate(context, R.layout.setting_photo_pop, null);
		mSettingPhotoPopupWindow = new PopupWindow(mSettingPhotoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		Button mSettingPhotoCameraButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_camera);
		mSettingPhotoCameraButton.setOnClickListener(mSettingPhotoCameraListener);
		Button mSelectLocalButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_local);
		mSelectLocalButton.setOnClickListener(mSelectLocalListener);
		Button mCancelButton = (Button) mSettingPhotoView.findViewById(R.id.setting_photo_cancel);
		mCancelButton.setOnClickListener(mCancelPopupWindowListener);
		if(!mSettingPhotoPopupWindow.isShowing()) {
			InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			mSettingPhotoPopupWindow.showAtLocation(mRootLayout, Gravity.BOTTOM, 0, 0);
		}
	}

	/**
	 * popupwindow 选择本地图片按钮点击事件
	 */
	View.OnClickListener mSelectLocalListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
//			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
//			intent.putExtra("crop", "true");
//			intent.putExtra("aspectX", 1);
//			intent.putExtra("aspectY", 1);
//			intent.putExtra("outputX", 180);
//			intent.putExtra("outputY", 180);
//			intent.putExtra("return-data", true);
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
			if("avatar".equals(type)) {
				saveFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"+System.currentTimeMillis()+".jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
			}else {
				backgroundFile = new File(mediaStorageDir.getPath()+File.separator+"IMG_"+System.currentTimeMillis()+"bg"+".jpg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(backgroundFile));
			}
			startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
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
	
	/**
	 * 得到返回的图片,如果type 为avatar 为头像图片
	 * 如果为background为封面图
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		FileOutputStream outputStream = null;
			if(resultCode == Activity.RESULT_OK) {
				if(requestCode == CUT_PHOTO_REQUEST_CODE) {
					if(data != null && data.getExtras() != null) {
                        if(data.getExtras().get("data") != null) {
                            mAvatarBitmap = (Bitmap) data.getExtras().get("data");
                            if("avatar".equals(type)) {
                                saveImage(type, outputStream, mAvatarBitmap);
                                mAvatarImage.setImageBitmap(mAvatarBitmap);
                            }else {
                                saveImage(type, outputStream, mAvatarBitmap);
                            }
                        }
					}else{
						if("avatar".equals(type)) {
							mImageLoader.displayImage(Uri.fromFile(saveFile).toString(), mAvatarImage,ImageUtils.imageLoader(context, 4));
						}
					}
			}else {
				if(data != null) {
					if("avatar".equals(type)) {
						saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+".jpg");
						cropImageUri(data.getData(), 180, 180, 1, 1, Uri.fromFile(saveFile));
					}else {
						backgroundFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+"_bg.jpg");
						cropImageUri(data.getData(), 480, 360, 4, 3,Uri.fromFile(backgroundFile));
					}
				}else{
					if("avatar".equals(type)) {
						cropImageUri(Uri.fromFile(saveFile), 180, 180, 1, 1,Uri.fromFile(saveFile));
					}else {
						cropImageUri(Uri.fromFile(backgroundFile), 480, 360, 4, 3,Uri.fromFile(backgroundFile));
					}
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(mSettingPhotoPopupWindow != null && mSettingPhotoPopupWindow.isShowing()) {
			mSettingPhotoPopupWindow.dismiss();
		}
	}

	/**
	 * 保存图片
	 * @param saveFile
	 * @param outputStream
	 * @param mAvatarBitmap
	 */
	private void saveImage(String type, FileOutputStream outputStream, Bitmap mAvatarBitmap) {
		if(mSettingPhotoPopupWindow != null && mSettingPhotoPopupWindow.isShowing()) {
			mSettingPhotoPopupWindow.dismiss();
		}
		try {
			if("avatar".equals(type)) {
				saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+".jpg");
				outputStream = new FileOutputStream(saveFile);
			}else {
				backgroundFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis()+"_bg"+".jpg");
				outputStream = new FileOutputStream(backgroundFile);
			}
			mAvatarBitmap.compress(CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
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
	}
	
	/**
	 * 调用系统裁剪头像图片
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int x, int y , int aspectX, int aspectY, Uri outUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", x);
		intent.putExtra("outputY", y);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);
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
			}else {
				finish();
				return super.onKeyDown(keyCode, event);
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(saveFile != null && saveFile.exists()) {
			saveFile.delete();
		}
		
		if(backgroundFile != null) {
			backgroundFile.delete();
		}
		
		if(mAvatarBitmap != null && !mAvatarBitmap.isRecycled()) {
			mAvatarBitmap.recycle();
		}
	}
}
