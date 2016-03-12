package com.mzs.guaji.offical;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.Volley;
import com.google.inject.Inject;
import com.mzs.guaji.R;
import com.mzs.guaji.core.DialogFragmentActivity;
import com.mzs.guaji.entity.DefaultReponse;
import com.mzs.guaji.http.HttpUtils;
import com.mzs.guaji.http.MultipartRequest;
import com.mzs.guaji.ui.SubmitTopicImageBrowseActivity;
import com.mzs.guaji.util.GiveUpEditingDialog;
import com.mzs.guaji.util.ImageUtils;
import com.mzs.guaji.util.StorageUtil;
import com.mzs.guaji.util.ToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 13-12-27.
 * 官方圈子发布新话题UI
 */
@ContentView(R.layout.official_tvcircle_sumit_topic_layout)
public class OfficialTvCircleSubmitTopicActivity extends DialogFragmentActivity {

    @Inject
    private Context context;
    private static final int SET_CAMERA_REQUEST_CODE = 2;
    private static final int SET_PHOTO_REQUEST_CODE = 1;
    private static final int SET_DELETE_REQUEST_CODE = 3;

    @InjectView(R.id.official_tvcircle_submit_topic_back)
    private View backView;

    @InjectView(R.id.official_tvcircle_submit_topic_release)
    private View mSubmitTopicLayout;

    @InjectView(R.id.official_tvcircle_submit_topic_from)
    private TextView mFromText;
    @InjectView(R.id.publish_groupname)
    private TextView publishgroupname;

    @InjectView(R.id.official_tvcircle_submit_topic_edit_title)
    private EditText mTitleEdit;

    @InjectView(R.id.official_tvcircle_submit_topic_edit_content)
    private EditText mContentEdit;

    @InjectView(R.id.official_tvcircle_submit_camera)
    private RelativeLayout mCameraLayout;

    @InjectView(R.id.official_tvcircle_submit_photo)
    private RelativeLayout mPhotoLayout;

    @InjectView(R.id.official_tvcircle_submit_share_image)
    private ImageView mSubmitImage;

    @InjectView(R.id.official_tvcircle_submit_sina_layout)
    private RelativeLayout mSinaLayout;

    @InjectView(R.id.official_tvcircle_submit_sina_image)
    private ImageView mSinaImage;

    @InjectView(R.id.official_tvcircle_submit_qq_layout)
    private RelativeLayout mQQLayout;

    @InjectView(R.id.official_tvcircle_submit_qq_image)
    private ImageView mQQImage;

    private long mTvCircleId;
    private File saveFile;
    private File mediaStorageDir;
    private Bitmap mBitmap;
    private boolean isStar = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String mGroupName = getStringExtra("groupName");
        mTvCircleId = getLongExtra("tvcircleId");
        isStar = getIntent().getBooleanExtra("isStar", false);
        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GuaJi");
        backView.setOnClickListener(mBackClickListener);

        mSubmitTopicLayout.setOnClickListener(mSubmitLayoutClickListener);
        mFromText.setText(mGroupName);
        if (isStar)
            publishgroupname.setText("名人");
        mSubmitImage.setOnClickListener(mSubmitImageClickListener);
        mCameraLayout.setOnClickListener(mCameraClickListener);
        mPhotoLayout.setOnClickListener(mPhotoClickListener);
    }

    View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GiveUpEditingDialog.showGiveUpEditingDialog(OfficialTvCircleSubmitTopicActivity.this, mTitleEdit.getText().toString());
        }
    };

    /**
     * 发布按钮点击事件
     */
    View.OnClickListener mSubmitLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            String mTitleText = mTitleEdit.getText().toString();
            String mContentText = mContentEdit.getText().toString();
            if (TextUtils.isEmpty(mTitleText)) {
                ToastUtil.showToast(context, R.string.submit_topic_title_tip);
                return;
            }
            if (TextUtils.isEmpty(mContentText)) {
                ToastUtil.showToast(context, R.string.submit_topic_content_tip);
                return;
            }

            MultipartRequest<DefaultReponse> request = new MultipartRequest<DefaultReponse>(getNewTopicRequest(), DefaultReponse.class, new Response.Listener<DefaultReponse>() {
                @Override
                public void onResponse(DefaultReponse response) {
                    if (response != null) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtil.showToast(context, response.getResponseMessage());
                    }
                }
            }, null);
            if (isStar)
                request.addMultipartStringEntity("uid", String.valueOf(mTvCircleId));
            else {
                request.addMultipartStringEntity("groupId", mTvCircleId + "");
            }
            request.addMultipartStringEntity("title", mTitleText);
            request.addMultipartStringEntity("desc", mContentText);
            if (saveFile != null) {
                request.addMultipartFileEntity("img", saveFile);
            }
            Volley.newRequestQueue(context, new HttpClientStack(HttpUtils.getHttpClient(context))).add(request);

        }
    };

    private String getNewTopicRequest() {
        if (isStar)
            return "http://social.api.ttq2014.com/topic/celebrity.json";
        else
            return "http://social.api.ttq2014.com/topic/new.json";

    }

    /**
     * 相机点击事件
     */
    View.OnClickListener mCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            saveFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(saveFile));
            startActivityForResult(intent, SET_CAMERA_REQUEST_CODE);
        }
    };

    /**
     * 图库点击事件
     */
    View.OnClickListener mPhotoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SET_PHOTO_REQUEST_CODE);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ContentResolver mResolver = getContentResolver();
        FileOutputStream mOutputStream = null;
        if (requestCode == SET_PHOTO_REQUEST_CODE) {
            if (data != null) {
                try {
                    Uri mUri = data.getData();
                    byte[] mContent = readStream(mResolver.openInputStream(Uri.parse(mUri.toString())));
                    saveFile = new File(StorageUtil.makeCacheDir("photo"), System.currentTimeMillis() + ".jpg");
                    mBitmap = decodeBitmap(mContent);
                    mOutputStream = new FileOutputStream(saveFile);
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 70, mOutputStream);
                    mSubmitImage.setImageBitmap(mBitmap);
                    mSubmitImage.setVisibility(View.VISIBLE);
                    mCameraLayout.setVisibility(View.GONE);
                    mPhotoLayout.setVisibility(View.GONE);
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
        } else if (requestCode == SET_CAMERA_REQUEST_CODE) {
            ImageLoader.getInstance().displayImage(Uri.fromFile(saveFile).toString(), mSubmitImage, ImageUtils.imageLoader(context, 0));
            mSubmitImage.setVisibility(View.VISIBLE);
            mCameraLayout.setVisibility(View.GONE);
            mPhotoLayout.setVisibility(View.GONE);
        } else if (resultCode == RESULT_OK) {
            if (saveFile != null) {
                saveFile.delete();
            }
            mSubmitImage.setVisibility(View.GONE);
            mCameraLayout.setVisibility(View.VISIBLE);
            mPhotoLayout.setVisibility(View.VISIBLE);
        }

    }

    public byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    private Bitmap decodeBitmap(byte[] mContent) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        ;
        int dw = mDisplayMetrics.widthPixels;
        int dh = mDisplayMetrics.heightPixels;
        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeByteArray(mContent, 0, mContent.length, bmpFactoryOptions);
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) dh);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) dw);
        bmpFactoryOptions.inJustDecodeBounds = false;

        if (heightRatio > widthRatio) {
            bmpFactoryOptions.inSampleSize = heightRatio;
        } else {
            bmpFactoryOptions.inSampleSize = widthRatio;
        }
        bmp = BitmapFactory.decodeByteArray(mContent, 0, mContent.length, bmpFactoryOptions);
        return bmp;
    }

    /**
     * 上传的图片点击事件,点击跳转到其它页面
     */
    View.OnClickListener mSubmitImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent mIntent = new Intent(context, SubmitTopicImageBrowseActivity.class);
            if (saveFile != null) {
                mIntent.putExtra("imagePath", saveFile.getAbsolutePath());
            }
            startActivityForResult(mIntent, SET_DELETE_REQUEST_CODE);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }

        if (saveFile != null) {
            saveFile.delete();
        }
    }

    @Override
    public void onBackPressed() {
        GiveUpEditingDialog.showGiveUpEditingDialog(OfficialTvCircleSubmitTopicActivity.this, mTitleEdit.getText().toString());
    }
}
