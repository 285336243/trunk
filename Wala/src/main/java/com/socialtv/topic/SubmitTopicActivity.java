package com.socialtv.topic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.Response;
import com.socialtv.core.AbstractRoboAsyncTask;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.LightProgressDialog;
import com.socialtv.core.ProgressDialogTask;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.http.HttpUtils;
import com.socialtv.personcenter.ImagePagerActivity;
import com.socialtv.personcenter.entity.PhotoAlbum;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.LoginUtil;
import com.socialtv.util.StorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

/**
 * Created by wlanjie on 14-7-7.
 * 发布话题页面
 */
@ContentView(R.layout.submit_topic)
public class SubmitTopicActivity extends DialogFragmentActivity {

    private final static int CAMERA = 0;

    private final static int PHOTO = 1;

    private final static int START_IMAGE_BROWSE = 2;

    private final static int GRID_SIZE = 9;

    @Inject
    private Activity activity;

    @InjectView(R.id.submit_topic_edit)
    private EditText editText;

    @InjectView(R.id.submit_topic_grid)
    private GridView gridView;

    private SubmitTopicAdapter adapter;

    @Inject
    private TopicServices services;

    @InjectExtra(value = IConstant.ACTIVITY_ID, optional = true)
    private String activityId;

    @InjectExtra(value = IConstant.STAR_ID, optional = true)
    private String starId;

    @InjectExtra(value = IConstant.PROGRAM_ID, optional = true)
    private String programId;

    private File saveFile;

    private Bitmap bitmap;

    private CancelDialog dialog;

    private boolean isPublish = true;

    private DisplayImageOptions options;

    private FileOutputStream outputStream = null;

    private InputMethodManager inputMethodManager;

    private PhotoAlbum photoAlbum;

    private List<Pics> items = new ArrayList<Pics>();

    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("发布话题");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (width * 0.6)));
        gridView.setLayoutParams(new LinearLayout.LayoutParams(3 * Utils.dip2px(activity, 96) + Utils.dip2px(activity, 18), LinearLayout.LayoutParams.WRAP_CONTENT));

        adapter = new SubmitTopicAdapter(this);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.default_image)
//                .showImageForEmptyUri(R.drawable.default_image)
//                .showImageOnFail(R.drawable.default_image)
//                .cacheInMemory(true).cacheOnDisc(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();

        dialog = new CancelDialog(activity, new CancelDialog.OnDialogDismissListener() {
            @Override
            public void onDialogDismissListener() {
                dialog.dismiss();
                finish();
            }
        });

        gridView.setAdapter(adapter);
        photoAlbum = new PhotoAlbum();
        photoAlbum.setPics(items);
        adapter.setItems(items);

        final Dialog uploadDailog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
        uploadDailog.setContentView(R.layout.upload_image_dialog);
        View cameraView = uploadDailog.findViewById(R.id.upload_image_camera);
        View albumView = uploadDailog.findViewById(R.id.upload_image_album);
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
                if (uploadDailog.isShowing()) {
                    uploadDailog.dismiss();
                }
            }
        });

        albumView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intents(activity, PhotoSelectedActivity.class).add(IConstant.KNOWN_SIZE, items.size()).toIntent(), PHOTO);
                if (uploadDailog.isShowing()) {
                    uploadDailog.dismiss();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == adapter.getCount() - 1 && adapter.getItemViewType(position) == 1) {
                    if (!uploadDailog.isShowing()) {
                        uploadDailog.show();
                    }
                } else {
                    startActivityForResult(new Intents(activity, ImagePagerActivity.class).add(IConstant.IS_SELF, true)
                            .add(IConstant.PHOTO_ALBUM, photoAlbum).add(IConstant.PHOTO_ALBUM_LIST_POSITION, position).toIntent(), START_IMAGE_BROWSE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO && resultCode != RESULT_CANCELED && resultCode == RESULT_OK) {
            if (data != null) {
                PhotoAlbum album = (PhotoAlbum) data.getSerializableExtra(IConstant.PHOTO_ALBUM);
                if (album != null) {
                    items.addAll(album.getPics());
                    photoAlbum.setPics(items);
                    adapter.clear();
                    adapter.setItems(items);
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
                        Pics pics = new Pics();
                        pics.setImg(saveFile.getAbsolutePath());
                        items.add(pics);
                        photoAlbum.setPics(items);
                        adapter.clear();
                        adapter.setItems(items);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (requestCode == START_IMAGE_BROWSE && resultCode != RESULT_CANCELED) {
            if (data != null) {
                PhotoAlbum album = (PhotoAlbum) data.getSerializableExtra(IConstant.PHOTO_ALBUM);
                if (album != null) {
                    //只有从浏览大图里我返回来的才能把items清空,从相册和camera回来的是不能清空items的
                    items.clear();
                    photoAlbum.setPics(album.getPics());
                    items.addAll(album.getPics());
                    adapter.clear();
                    adapter.setItems(photoAlbum.getPics());
                }
            }
        }
    }

    private void setGridHeight(final int size) {
        int count = (size + 2) / 3;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(3 * Utils.dip2px(activity, 96) + Utils.dip2px(activity, 18), Utils.dip2px(activity, 96) * count + Utils.dip2px(activity, (count - 1) * 8));
        gridView.setLayoutParams(params);
    }


    private Bitmap convertBitmap(File file) throws IOException {
        Bitmap bitmap = null;
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = new FileInputStream(file.getAbsolutePath());
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();
        final int REQUIRED_SIZE = 100;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                break;
            width_tmp /= 3;
            height_tmp /= 2;
            scale *= 2;
        }
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inSampleSize = scale;
        fis = new FileInputStream(file.getAbsolutePath());
        bitmap = BitmapFactory.decodeStream(fis, null, op);
        fis.close();
        // 保存压缩图片 替换临时图片
        FileOutputStream out = new FileOutputStream(file);
        if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
            out.flush();
            out.close();
        }

        return bitmap;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "发布").setIcon(R.drawable.btn_topsend_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancelDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showCancelDialog() {
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        final String text = editText.getText().toString();
        if (!TextUtils.isEmpty(text) || !items.isEmpty()) {
            if (!dialog.isShowing())
                dialog.show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showCancelDialog();
        } else if (item.getItemId() == 0) {
            if (isPublish) {
                sumitTopic();
                isPublish = false;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void sumitTopic() {
        final String text = editText.getText().toString();
        if (TextUtils.isEmpty(text) && items.isEmpty()) {
            isPublish = true;
            ToastUtils.show(activity, "内容不能为空");
            return;
        }
        AbstractRoboAsyncTask<Response> task = new AbstractRoboAsyncTask<Response>(activity){

            @Override
            protected Response run(Object data) throws Exception {
                if (!TextUtils.isEmpty(starId)) {
                    return (Response) HttpUtils.doRequest(services.createSubmitStarTopicRequest(starId, text, photoAlbum.getPics())).result;
                } else if (!TextUtils.isEmpty(activityId)) {
                    return (Response) HttpUtils.doRequest(services.createSubmitActivityTopicRequest(activityId, text, photoAlbum.getPics())).result;
                } else {
                    return (Response) HttpUtils.doRequest(services.createSubmitGroupTopicRequest(programId, text, photoAlbum.getPics())).result;
                }
            }

            @Override
            protected void onSuccess(Response response) throws Exception {
                super.onSuccess(response);
                isPublish = true;
                if (response != null) {
                    if (response.getResponseCode() == 0) {
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        ToastUtils.show(activity, response.getResponseMessage());
                    }
                }
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                isPublish = true;
            }
        };
        createDialog("请稍候").show();
        task.execute();
    }

    /**
     * 创建loading 框
     * @param message
     * @return
     */
    @TargetApi(11)
    private AlertDialog createDialog(final String message) {
        ProgressDialog dialog;
        if (SDK_INT >= ICE_CREAM_SANDWICH)
            dialog = new ProgressDialog(activity, AlertDialog.THEME_HOLO_LIGHT);
        else {
            dialog = new ProgressDialog(activity);
            dialog.setInverseBackgroundForced(true);
        }
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminateDrawable(getResources().getDrawable(
                R.drawable.spinner));
        return dialog;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        starId = null;
        activityId = null;
        programId = null;
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
            saveFile = null;
        }
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private class SubmitTopicAdapter extends MultiTypeAdapter {

        private final static int ITEM = 0;
        private final static int ADD = 1;

        public SubmitTopicAdapter(Activity activity) {
            super(activity);
        }

        public void setItems(List<Pics> items) {
            if (items != null) {
                if (items.size() <= GRID_SIZE) {
                    if (items.isEmpty()) {
                        addItem(ADD, items);
                    } else {
                        for (int i = 0; i < items.size(); i++) {
                            addItem(ITEM, items.get(i));
                            if (i != GRID_SIZE - 1 && i == items.size() - 1) {
                                addItem(ADD, items.get(i));
                            }
                        }
                    }
                }
                setGridHeight(getCount());
            }
        }

        @Override
        protected int getChildLayoutId(int type) {
            if (type == ITEM) {
                return R.layout.image;
            } else if (type == ADD) {
                return R.layout.submit_topic_photo_add;
            }
            return -1;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        protected int[] getChildViewIds(int type) {
            if (type == ITEM) {
                return new int[]{
                        R.id.imageview
                };
            } else if (type == ADD) {
                return new int[]{
                        R.id.submit_topic_photo_add_root
                };
            }
            return new int[0];
        }

        @Override
        protected void update(int position, Object item, int type) {
//            AbsListView.LayoutParams params = new AbsListView.LayoutParams(width / 3 - Utils.dip2px(activity, 8), width / 3 - Utils.dip2px(activity, 8));
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(Utils.dip2px(activity, 96), Utils.dip2px(activity, 96));
            if (type == ITEM) {
                Pics pics = (Pics) item;
                if (pics != null) {
                    view(0).setLayoutParams(params);
                    if (item != null)
                        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(pics.getImg())).toString(), imageView(0), ImageUtils.imageLoader(activity, 4));
                }
            } else {
                view(0).setLayoutParams(params);
            }
        }
    }
}
