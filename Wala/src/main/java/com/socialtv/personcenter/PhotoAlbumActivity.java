package com.socialtv.personcenter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.MultiGridActivity;
import com.socialtv.core.SingGridActivity;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.entity.PhotoAlbum;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.IConstant;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StorageUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-4.
 */
@ContentView(R.layout.photo_album_list)
public class PhotoAlbumActivity extends MultiGridActivity<PhotoAlbum> implements LoaderManager.LoaderCallbacks<PhotoAlbum> {

    private final static int CAMERA = 100;

    private final static int ALBUM = 200;

    @InjectExtra(value = IConstant.USER_ID, optional = true)
    private String userId;

    @Inject
    private PhotoAlbumAdapter adapter;

    @Inject
    private Activity activity;

    @Inject
    private PersonalUrlService services;

    private Dialog dialog;

    private View cameraView;

    private View albumView;

    private File saveFile;

    private Bitmap bitmap;

    private DisplayImageOptions options;

    private boolean isResult = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.default_image)
//                .showImageForEmptyUri(R.drawable.default_image)
//                .showImageOnFail(R.drawable.default_image)
//                .cacheInMemory(true).cacheOnDisc(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();

        getSupportActionBar().setLogo(R.drawable.btn_back_tomato);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("相册");

        emptyTextView.setText("暂无相片");
    }

    @Override
    protected MultiTypeAdapter createAdapter() {
        return adapter;
    }

    @Override
    protected Request<PhotoAlbum> createRequest() {
        return services.createPhotoAlbumRequest(userId);
    }

    @Override
    protected int getLoadingMessage() {
        return R.string.photo_album_error;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.photo_album_error;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        items.clear();
        if (bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }

    @Override
    public void onLoadFinished(Loader<PhotoAlbum> loader, final PhotoAlbum data) {
        super.onLoadFinished(loader, data);
        if (data != null) {
            if (data.getResponseCode() == 0) {
                if (isResult || isRefresh) {
                    adapter.clear();
                    isResult = false;
                    isRefresh = false;
                }
                if (data.getPics() != null) {
                    if (data.getPics().isEmpty()) {
                        hasMore = true;
                        List<Pics> pics = new ArrayList<Pics>();
                        pics.add(new Pics());
                        this.items = pics;
                    } else {
                        this.items = data.getPics();
                    }
                    adapter.setItems(data.getPics(), userId);

                    refreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            boolean isSelf;
                            if (LoginUtil.getUserId(activity).equals(userId)) {
                                isSelf = true;
                            } else {
                                isSelf = false;
                            }
                            if (position == adapter.getCount() - 1 && isSelf) {
                                showUploadImageDialog();
                                return;
                            }
                            startActivityForResult(new Intents(activity, ImagePagerActivity.class).add(IConstant.IS_SELF, isSelf)
                                    .add(IConstant.PHOTO_ALBUM, data).add(IConstant.PHOTO_ALBUM_LIST_POSITION, position)
                                    .add(IConstant.USER_ID, userId).toIntent(), 0);
                        }
                    });
                }
            } else {
                ToastUtils.show(this, data.getResponseMessage());
            }
        }
        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (LoginUtil.getUserId(activity).equals(userId))
            menu.add(Menu.NONE, 0, 0, "添加相片").setIcon(R.drawable.btn_topadd_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {
            showUploadImageDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUploadImageDialog() {
        if (dialog == null) {
            dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.upload_image_dialog);
            cameraView = dialog.findViewById(R.id.upload_image_camera);
            albumView = dialog.findViewById(R.id.upload_image_album);
        }
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String savePath = "";
                if (StorageUtil.isExternalStorageAvailable()) {
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Wala/photo";
                    File saveDir = new File(savePath);
                    if (!saveDir.exists())
                        saveDir.mkdirs();
                }
                if ("".equals(savePath)) {
                    ToastUtils.show(activity, "请检查sd卡");
                    return;
                }
                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String fileName = "tt_" + timeStamp + ".jpg";
                saveFile = new File(savePath, fileName);
                Uri uri = Uri.fromFile(saveFile);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, CAMERA);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, ALBUM);
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            isResult = true;
            refresh();
        }
        if (requestCode == ALBUM) {
            if (data != null) {
                Uri uri = data.getData();
                ContentResolver resolver = getContentResolver();
                FileOutputStream outputStream = null;
                try {
                    byte[] content = readStream(resolver.openInputStream(Uri.parse(uri.toString())));
                    bitmap = decodeBitmap(content);
                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String fileName = "tt_" + timeStamp + ".jpg";
                    saveFile = new File(StorageUtil.makeCacheDir("photo"), fileName);
                    outputStream = new FileOutputStream(saveFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
                    startUploadImage();
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
            }
        } else if (requestCode == CAMERA && resultCode != RESULT_CANCELED) {
            ImageLoader.getInstance().loadImage(Uri.fromFile(saveFile).toString(), options, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    try {
                        FileOutputStream outputStream = new FileOutputStream(saveFile);
                        if (loadedImage.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)) {
                            outputStream.flush();
                            outputStream.close();
                        }
                        startUploadImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void startUploadImage() {
        startActivityForResult(new Intents(activity, UploadImageActivity.class)
                .add(IConstant.IMAGE_PATH, saveFile.getAbsolutePath()).toIntent(), 0);
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
}
