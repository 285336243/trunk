package com.shengzhish.xyj.home;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.AbsListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.shengzhish.xyj.R;
import com.shengzhish.xyj.home.entity.FeedItem;
import com.shengzhish.xyj.util.AnimationUtil;

/**
 * Created by wlanjie on 14-5-30.
 */
public class FeedAdapter extends SingleTypeAdapter<FeedItem> {

    private final Activity activity;
    private final int width;
    private final DisplayImageOptions mImageOptions;

    /**
     * Create adapter
     *
     * @param activity
     */
    public FeedAdapter(Activity activity) {
        super(activity, R.layout.feed_item);
        this.activity = activity;
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;


        mImageOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.load_image)
                .showImageForEmptyUri(R.drawable.load_image)
                .showImageOnFail(R.drawable.load_image)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY)
//                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }

    /**
     * Get child view ids to store
     * <p/>
     * The index of each id in the returned array should be used when using the
     * helpers to update a specific child view
     *
     * @return ids
     */
    @Override
    protected int[] getChildViewIds() {
        return new int[]{
                R.id.feed_item_root,
                R.id.feed_item_image,
                R.id.feed_item_title,
                R.id.feed_item_create_time,
                R.id.feed_item_icon
        };
    }

    /**
     * Update item
     *
     * @param position
     * @param item
     */
    @Override
    protected void update(int position, FeedItem item) {
        if (item != null) {
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(width, width / 2);
            view(0).setLayoutParams(params);
            ImageLoader.getInstance().displayImage(item.getImg(), imageView(1), mImageOptions);
            setText(2, item.getTitle());
            setText(3, item.getCreateTime());
            if ("gallery".equals(item.getType())) {
                setGone(4, false);
            } else {
                setGone(4, true);
            }
        }
    }
}
