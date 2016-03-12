package com.socialtv.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.socialtv.R;
import com.socialtv.activity.entitiy.ScreenShot;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.Intents;
import com.socialtv.core.ThrowableLoader;
import com.socialtv.core.ToastUtils;
import com.socialtv.http.HttpUtils;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.util.StorageUtil;
import com.socialtv.view.AdapterView;
import com.socialtv.view.HorizontalListView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import roboguice.inject.ContentView;
import roboguice.inject.InjectExtra;
import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-7-21.
 *
 * 截图页面
 */
@ContentView(R.layout.screen_shot)
public class ScreenShotActivity extends DialogFragmentActivity {

    private final static int LOADER_ID = 100;

    @InjectView(R.id.screen_shot_root)
    private View rootView;

    @InjectView(R.id.screen_shot_image_layout)
    private FrameLayout imageLayout;

    @InjectView(R.id.screen_shot_image)
    private ImageView imageView;

    @InjectView(R.id.screen_shot_mark)
    private ImageView markImageView;

    @InjectView(R.id.screen_shot_list)
    private HorizontalListView listView;

    @InjectView(R.id.screen_shot_repeat)
    private View repeatView;

    @InjectView(R.id.pb_loading)
    private ProgressBar progressBar;

    @InjectExtra(value = IConstant.ACTIVITY_ID, optional = true)
    private String activityId;

    @Inject
    private ActivityServices services;

    @Inject
    private Activity activity;

    @Inject
    private ScreenShotAdapter adapter;

    private Bitmap bitmap;

    private Bitmap markBitmap;

    private File saveFile;

    private Bitmap submitBitmap;

    private DisplayMetrics metrics;

    private String shareText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.screen_shot_title));
        setSupportProgressBarIndeterminateVisibility(true);

        metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imageLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, metrics.widthPixels / 4 * 3));

        getSupportLoaderManager().initLoader(LOADER_ID, null, callbacks);
        repeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.setPosition(0);
                setSupportProgressBarIndeterminateVisibility(true);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, callbacks);
            }
        });
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setPosition(position);
                HorizontalListView l = (HorizontalListView) parent;
                String item = (String) l.getItemAtPosition(position);
                ImageLoader.getInstance().displayImage(item, imageView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        bitmap = loadedImage;
                    }
                });
            }
        });
    }

    /**
     * Loader Callbacks
     */
    LoaderManager.LoaderCallbacks<ScreenShot> callbacks = new LoaderManager.LoaderCallbacks<ScreenShot>() {
        @Override
        public Loader<ScreenShot> onCreateLoader(int i, Bundle bundle) {
            return new ThrowableLoader<ScreenShot>(activity) {
                @Override
                public ScreenShot loadData() throws Exception {
                    return (ScreenShot) HttpUtils.doRequest(services.createScreenShotRequest(activityId)).result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ScreenShot> screenShotLoader, ScreenShot screenShot) {
            setSupportProgressBarIndeterminateVisibility(false);
            if (screenShot != null) {
                if (screenShot.getResponseCode() == 0) {
                    hide(progressBar).show(rootView);
                    if (screenShot.getShareTemplete() != null)
                        shareText = screenShot.getShareTemplete().getShareText();
                    if (screenShot.getPics() != null && !screenShot.getPics().isEmpty()) {
                        ImageLoader.getInstance().displayImage(screenShot.getPics().get(0), imageView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                bitmap = loadedImage;
                            }
                        });
                        adapter.setItems(screenShot.getPics());
                    }
                    ImageLoader.getInstance().displayImage(screenShot.getCoverImg(), markImageView, ImageUtils.imageLoader(activity, 0), new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            markBitmap = loadedImage;
                        }
                    });
                } else {
                    ToastUtils.show(activity, screenShot.getResponseMessage());
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<ScreenShot> screenShotLoader) {

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "发布话题")
                .setIcon(R.drawable.btn_topsend_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {
            startSubmitTopicActiivty();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 发布话题
     */
    private void startSubmitTopicActiivty() {
        if (bitmap != null && markBitmap != null) {
            try {
                submitBitmap = Bitmap.createBitmap(metrics.widthPixels, metrics.widthPixels / 4 * 3, Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(submitBitmap);
                Paint paint = new Paint();
//                canvas.drawBitmap(this.bitmap, 0, 0, null);
//                paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));
                canvas.drawBitmap(bitmap, null, new Rect(0, 0, metrics.widthPixels, metrics.widthPixels / 4 * 3), null);
                canvas.drawBitmap(markBitmap, null, new Rect(0, 0, metrics.widthPixels, metrics.widthPixels / 4 * 3), null);
                canvas.save( Canvas.ALL_SAVE_FLAG );
                canvas.restore();

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
                FileOutputStream outputStream = new FileOutputStream(saveFile);
                submitBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Intents intents = new Intents(activity, ScreenShotSubmitTopicActivity.class).add(IConstant.IMAGE_PATH, saveFile.getAbsolutePath())
                        .add(IConstant.ACTIVITY_ID, activityId);
                if (!TextUtils.isEmpty(shareText))
                    intents.add(IConstant.SHARE_TEXT, shareText);
                startActivityForResult(intents.toIntent(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (bitmap != null && !bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
//        if (markBitmap != null && !markBitmap.isRecycled()) {
//            markBitmap.recycle();
//        }
        if (submitBitmap != null && !submitBitmap.isRecycled()) {
            submitBitmap.recycle();
        }
        if (saveFile != null && saveFile.exists()) {
            saveFile.delete();
        }
    }
}
