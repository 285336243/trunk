package com.socialtv.topic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.google.inject.Inject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.socialtv.R;
import com.socialtv.core.DialogFragmentActivity;
import com.socialtv.core.ToastUtils;
import com.socialtv.core.Utils;
import com.socialtv.personcenter.entity.PhotoAlbum;
import com.socialtv.publicentity.Pics;
import com.socialtv.util.IConstant;
import com.socialtv.util.ImageUtils;
import com.socialtv.view.PullToRefreshGridView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * Created by wlanjie on 14-10-16.
 * 相片选择页面
 */
public class PhotoSelectedActivity extends DialogFragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final static int SIZE = 9;

    private final static int LOADER_ID = 0;

    private PhotoSelectedAdapter adapter;

    private GridView gridView;

    private ProgressBar progressBar;

    private final String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

    private PhotoAlbum photoAlbum = new PhotoAlbum();

    private List<Pics> items = new ArrayList<Pics>();

    private List<Pics> checkeds = new ArrayList<Pics>();

    @Inject
    private Activity activity;

    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout content = new FrameLayout(this);
        gridView = new GridView(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params);
        gridView.setNumColumns(3);
        gridView.setVerticalSpacing(Utils.dip2px(activity, 2));
        gridView.setHorizontalSpacing(Utils.dip2px(activity, 2));
        progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams progressBarParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        progressBarParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(progressBarParams);
        content.addView(gridView);
        content.addView(progressBar);
        setContentView(content);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.transparent)
                .showImageForEmptyUri(R.drawable.transparent)
                .showImageOnFail(R.drawable.transparent)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .considerExifParams(true)
                .build();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.btn_back_tomato);
        actionBar.setTitle("选择相片");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final int knownSize = getIntExtra(IConstant.KNOWN_SIZE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Pics pics = (Pics) adapterView.getItemAtPosition(position);
                if (pics.isChecked()) {
                    pics.setChecked(false);
                    checkeds.remove(pics);
                } else {
                    if (checkeds.size() >= SIZE - knownSize) {
                        ToastUtils.show(activity, "你最多只能选择9张图片");
                        return;
                    } else {
                        checkeds.add(pics);
                        pics.setChecked(true);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new PhotoSelectedAdapter(this);
        gridView.setAdapter(adapter);
        photoAlbum.setPics(items);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gridView != null) {
            gridView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), true, true, null));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> objectLoader, Cursor cursor) {
        if (cursor != null) {
            hide(progressBar);
            while (cursor.moveToNext()) {
                String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                Pics pics = new Pics();
                pics.setImg(imagePath);
                items.add(pics);
            }
            adapter.setItems(items);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> objectLoader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "完成").setIcon(R.drawable.btn_topsend_tomato).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == 0) {
            photoAlbum.setPics(checkeds);
            Intent intent = new Intent();
            intent.putExtra(IConstant.PHOTO_ALBUM, photoAlbum);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gridView = null;
        adapter = null;
        items.clear();
        items = null;
        checkeds.clear();
        checkeds = null;
    }

    private class PhotoSelectedAdapter extends SingleTypeAdapter<Pics> {

        private final Activity activity;

        private final int width;

        public PhotoSelectedAdapter(Activity activity) {
            super(activity, R.layout.photo_selected_item);
            this.activity = activity;
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            width = metrics.widthPixels;
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[] {
                    R.id.photo_selected_item_image,
                    R.id.photo_selected_item_checked
            };
        }

        @Override
        protected void update(int position, Pics item) {
            if (item != null) {
                CheckBox checkBox = view(1);
                if (item.isChecked()) {
                    checkBox.setChecked(true);
                } else {
                    checkBox.setChecked(false);
                }

                view(0).setLayoutParams(new RelativeLayout.LayoutParams(width / 3, (int) (width * 0.225)));
                if (item != null)
                    ImageLoader.getInstance().displayImage(Uri.fromFile(new File(item.getImg())).toString(), imageView(0), options);
            }
        }
    }
}
