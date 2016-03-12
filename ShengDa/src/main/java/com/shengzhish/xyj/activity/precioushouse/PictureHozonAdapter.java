package com.shengzhish.xyj.activity.precioushouse;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.activity.entity.CodeItem;
import com.shengzhish.xyj.util.ImageUtils;
import com.shengzhish.xyj.view.HorizontalAbsListView;
import com.shengzhish.xyj.view.HorizontalListView;

import java.util.List;

/**
 * 图片adapter
 */
public class PictureHozonAdapter extends MultiTypeAdapter {
    private final static int CONTENT = 0;
    private final static int EMPTY = 1;
    private final ImageLoader imageLoader;
    private final Activity context;
    private int position;
    private int width;
    private HorizontalAbsListView pictureHorizList;
    private int listHistoryPos;

    /**
     * Create adapter
     *
     * @param activity
     * @param width
     */
    public PictureHozonAdapter(Activity activity, int width) {
        super(activity);
        imageLoader = ImageLoader.getInstance();
        this.context = activity;
        this.width = width;
    }

    public void addItem(List<CodeItem> entries) {
        if (entries != null && !entries.isEmpty()) {
            for (int i = 0; i < entries.size(); i++) {
                if (i == 0) {
                    addItem(EMPTY, entries.get(i));
                    addItem(CONTENT, entries.get(i));
                } else if (i == entries.size() - 1) {
                    addItem(CONTENT, entries.get(i));
                    addItem(EMPTY, entries.get(i));
                } else {
                    addItem(CONTENT, entries.get(i));
                }
            }
        }
    }

    /**
     * Get layout id for type
     *
     * @param type
     * @return layout id
     */
    @Override
    protected int getChildLayoutId(int type) {
        if (type == CONTENT) {
            return R.layout.precious_pic_adapter_item;
        } else if (type == EMPTY) {
            return R.layout.empty;
        }
        return -1;
    }

    /**
     * Get child view ids for type
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @param type
     * @return array of view ids
     */
    @Override
    protected int[] getChildViewIds(int type) {
        if (type == CONTENT) {
            return new int[]{
                    R.id.back_ground,//图片内容 imageview 0
                    R.id.item_name,  //文字内容 textview   1
                    R.id.black_front_ground,  //图片黑景 imageview    2
                    R.id.red_frame_front_ground,  //图片红框 imageview   3
                    R.id.progress_bar,              //进度条 porgressbar   4
                    R.id.root_pic_layout          // 根 layout  5
            };
        }
        return new int[0];
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Update view for item
     *
     * @param position
     * @param item
     * @param type
     */
    @Override
    protected void update(final int position, Object item, int type) {
//         Log.v("person","person == "+position);

        CodeItem codeItem = (CodeItem) item;
        if (item != null && type == CONTENT) {
            HorizontalAbsListView.LayoutParams params = new HorizontalAbsListView.LayoutParams(width, width * 2);
            view(5).setLayoutParams(params);
            imageLoader.displayImage(codeItem.getImg(), imageView(0), ImageUtils.imageLoader(context, 0), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            view(4).setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                            view(4).setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                        progressBar.setMax(total);
//                        progressBar.setProgress(current);
                        }
                    }
            );

            textView(1).setText(codeItem.getName());
            if (codeItem.getIsGot() == 0) {
                imageView(2).setVisibility(View.VISIBLE);
            } else {
                imageView(2).setVisibility(View.INVISIBLE);
            }


            imageView(3).setVisibility(View.INVISIBLE);
            if (position == this.position) {
                imageView(3).setVisibility(View.VISIBLE);
            }

        }
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

}
